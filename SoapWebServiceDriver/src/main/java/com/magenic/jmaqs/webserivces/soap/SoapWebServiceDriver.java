package com.magenic.jmaqs.webserivces.soap;

import com.magenic.jmaqs.webservices.jdk8.WebServiceDriver;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.function.Consumer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SoapWebServiceDriver extends WebServiceDriver {


    private final SOAPMessage soapMessage;
    private JAXBContext jaxbContext;

    public SoapWebServiceDriver(SOAPMessage message, String baseAddress) throws URISyntaxException {
        super(baseAddress);
        soapMessage = message;
    }

    public SoapWebServiceDriver(URI baseAddress) throws SOAPException {
        super(baseAddress);
        soapMessage = SoapWebServiceDriverFactory.getDefaultMessage();
    }

    public SoapWebServiceDriver(CloseableHttpClient newHttpClient, SOAPMessage message) {
        super(newHttpClient);
        soapMessage = message;
    }

    public String getDefaultSoapMessage(String outputStream) throws SOAPException, ParserConfigurationException, SAXException, IOException {
        //this.getLogger().logMessage(MessageType.INFORMATION, "Crafting SOAP Message");
        modifySoapPrefix();
        addNameSpaceCollectionToMessage((HashMap<String, String>) SoapConfig.getSoapNamespaces());
        addStringToBody(outputStream);
        String messageOutputStream = getSoapBodyString();
        //this.getLogger().logMessage(MessageType.INFORMATION, "Completed and saved SOAP Message changes");
        //this.getLogger().logMessage(MessageType.VERBOSE, messageOutputStream);
        return messageOutputStream;
    }

    public String getSoapBodyString() throws SOAPException, IOException {
        return getSoapBodyString(soapMessage);
    }

    public String getSoapBodyString(SOAPMessage message) throws SOAPException, IOException {
        message.saveChanges();
        OutputStream messageOutputStream = new ByteArrayOutputStream();
        message.writeTo(messageOutputStream);
        return messageOutputStream.toString();
    }

    public StringEntity getSoapBodyStringEntity() throws IOException, SOAPException {
        return new StringEntity(getSoapBodyString());
    }

    public void addStringToBody(String outputStream) throws ParserConfigurationException, SAXException, IOException, SOAPException {
        addStringToBody(outputStream, soapMessage);
    }

    public void addStringToBody(String outputStream, SOAPMessage message) throws ParserConfigurationException, SAXException, IOException, SOAPException {
        Document document = getDocument(outputStream);
        message.getSOAPBody().addDocument(document);
    }

    public void addNameSpaceToMessage(String key, String value) throws SOAPException {
        addNameSpaceToMessage(soapMessage.getSOAPPart().getEnvelope(), key, value);
    }

    public void addNameSpaceToMessage(SOAPEnvelope envelope, String key, String value) throws SOAPException {
        envelope.addNamespaceDeclaration(key, value);
    }

    public void addNameSpaceCollectionToMessage(HashMap<String, String> map) throws SOAPException {
        addNameSpaceCollectionToMessage(soapMessage.getSOAPPart().getEnvelope(), map);
    }

    public void addNameSpaceCollectionToMessage(SOAPEnvelope envelope, HashMap<String, String> map) {
        Consumer consumer = (key) -> {
            String value = map.get(key);
            try {
                //this.getLogger().logMessage(MessageType.INFORMATION, "Adding namespace to message: key: %s, value: %s", key, value);
                addNameSpaceToMessage(envelope, (String) key, value);
            } catch (SOAPException e) {
                e.printStackTrace();
            }
        };
        map.keySet().stream().iterator().forEachRemaining(consumer);
    }


    public  <T> String createSoapPayload(T object) throws JAXBException, IOException, ClassNotFoundException {
        //this.getLogger().logMessage(MessageType.INFORMATION, "Starting marshalling process");
        OutputStream outputStream = new ByteArrayOutputStream();
        Marshaller marshaller = getMarshaller(object);
        marshaller.marshal(object, outputStream);
        outputStream.flush();
        outputStream.close();
        //this.getLogger().logMessage(MessageType.INFORMATION, "Completed marshalling");
        //this.getLogger().logMessage(MessageType.VERBOSE, outputStream.toString());
        return outputStream.toString();
    }

    public <T> Marshaller getMarshaller(T object) throws JAXBException {
        jaxbContext = JAXBContext.newInstance(object.getClass().getPackage().getName());
        return getMarshaller();
    }

    public Marshaller getMarshaller() throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return marshaller;
    }

    public <T> Unmarshaller getUnMarshaller(T object) throws JAXBException {
        jaxbContext = JAXBContext.newInstance(object.getClass().getPackage().getName());
        return getUnMarshaller();
    }

    public <T> Unmarshaller getUnMarshaller() throws JAXBException {

        return jaxbContext.createUnmarshaller();

    }

    private Document getDocument(String bodyContent) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(new InputSource(new StringReader(bodyContent)));
    }

    private SOAPEnvelope modifySoapPrefix() throws SOAPException {
        return modifySoapPrefix(SoapConfig.getSoapPrefix());
    }



    private SOAPEnvelope modifySoapPrefix(String prefix) throws SOAPException {
        return modifySoapPrefix(soapMessage.getSOAPPart().getEnvelope(), prefix, "http://schemas.xmlsoap.org/soap/envelope/");
    }

    private SOAPEnvelope modifySoapPrefix(SOAPEnvelope envelope, String prefix, String url) throws SOAPException {
        //this.getLogger().logMessage(MessageType.INFORMATION, "removing default prefix: %s", envelope.getPrefix());
        envelope.removeNamespaceDeclaration(envelope.getPrefix());
        addNameSpaceToMessage(envelope, prefix, url);
        envelope.setPrefix(prefix);
        envelope.getBody().setPrefix(prefix);
        envelope.getHeader().setPrefix(prefix);
        return envelope;
    }

    public <T> void addObjectToSoapMessage(T object)
        throws JAXBException, IOException, ClassNotFoundException, SOAPException, SAXException,
        ParserConfigurationException {
        String soapPayload = createSoapPayload(object);
        addStringToBody(soapPayload);
    }
}
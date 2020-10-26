package jmaqs.helpers;

import com.magenic.jmaqs.utilities.logging.MessageType;
import com.magenic.jmaqs.webservices.jdk8.WebServiceDriver;
import org.apache.http.impl.client.CloseableHttpClient;
import org.example.models.countries.GetCountryRequest;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.function.Consumer;

public class SoapWebServiceDriver extends WebServiceDriver {

    private final MessageFactory messageFactory = MessageFactory.newInstance();
    private final SOAPMessage soapMessage = messageFactory.createMessage();

    public SoapWebServiceDriver(String baseAddress) throws URISyntaxException, SOAPException {
        super(baseAddress);
    }

    public SoapWebServiceDriver(URI baseAddress) throws SOAPException {
        super(baseAddress);
    }

    public SoapWebServiceDriver(CloseableHttpClient newHttpClient) throws SOAPException {
        super(newHttpClient);
    }

    private String getDefaultSoapMessage(String outputStream) throws SOAPException, ParserConfigurationException, SAXException, IOException {
        //this.getLogger().logMessage(MessageType.INFORMATION, "Crafting SOAP Message");
        modifySoapPrefix();
        addNameSpaceCollectionToMessage((HashMap<String, String>) SoapConfig.getSoapNamespaces());
        addStringToBody(outputStream);
        String messageOutputStream = finalizeSoapMessage();
        //this.getLogger().logMessage(MessageType.INFORMATION, "Completed and saved SOAP Message changes");
        //this.getLogger().logMessage(MessageType.VERBOSE, messageOutputStream);
        return messageOutputStream;
    }

    private String finalizeSoapMessage() throws SOAPException, IOException {
        return finalizeSoapMessage(soapMessage);
    }

    private String finalizeSoapMessage(SOAPMessage message) throws SOAPException, IOException {
        message.saveChanges();
        OutputStream messageOutputStream = new ByteArrayOutputStream();
        message.writeTo(messageOutputStream);
        return messageOutputStream.toString();
    }

    private void addStringToBody(String outputStream) throws ParserConfigurationException, SAXException, IOException, SOAPException {
        addStringToBody(outputStream, soapMessage);
    }

    private void addStringToBody(String outputStream, SOAPMessage message) throws ParserConfigurationException, SAXException, IOException, SOAPException {
        Document document = getDocument(outputStream);
        message.getSOAPBody().addDocument(document);
    }

    private void addNameSpaceToMessage(String key, String value) throws SOAPException {
        addNameSpaceToMessage(soapMessage.getSOAPPart().getEnvelope(), key, value);
    }

    private void addNameSpaceToMessage(SOAPEnvelope envelope, String key, String value) throws SOAPException {
        envelope.addNamespaceDeclaration(key, value);
    }

    private void addNameSpaceCollectionToMessage(HashMap<String, String> map) throws SOAPException {
        addNameSpaceCollectionToMessage(soapMessage.getSOAPPart().getEnvelope(), map);
    }

    private void addNameSpaceCollectionToMessage(SOAPEnvelope envelope, HashMap<String, String> map) {
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


    private String createSoapPayload(GetCountryRequest getCountryRequest) throws JAXBException, IOException {
        //this.getLogger().logMessage(MessageType.INFORMATION, "Starting marshalling process");
        OutputStream outputStream = new ByteArrayOutputStream();
        JAXBContext jaxbContext = JAXBContext.newInstance(GetCountryRequest.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(getCountryRequest, outputStream);
        outputStream.flush();
        outputStream.close();
        //this.getLogger().logMessage(MessageType.INFORMATION, "Completed marshalling");
        //this.getLogger().logMessage(MessageType.VERBOSE, outputStream.toString());
        return outputStream.toString();
    }

    private Document getDocument(String bodyContent) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(new InputSource(new StringReader(bodyContent)));
    }

    private SOAPEnvelope modifySoapPrefix() throws SOAPException {
        return modifySoapPrefix(soapMessage.getSOAPPart().getEnvelope());
    }


    private SOAPEnvelope modifySoapPrefix(SOAPEnvelope envelope) throws SOAPException {
        //this.getLogger().logMessage(MessageType.INFORMATION, "removing default prefix: %s", envelope.getPrefix());
        envelope.removeNamespaceDeclaration(envelope.getPrefix());
        addNameSpaceToMessage(envelope, SoapConfig.getSoapPrefix(), "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.setPrefix(SoapConfig.getSoapPrefix());
        envelope.getBody().setPrefix(SoapConfig.getSoapPrefix());
        envelope.getHeader().setPrefix(SoapConfig.getSoapPrefix());
        return envelope;
    }
}
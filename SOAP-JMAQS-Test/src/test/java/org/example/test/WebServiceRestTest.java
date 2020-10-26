package org.example.test;

import com.magenic.jmaqs.utilities.logging.MessageType;
import com.magenic.jmaqs.webservices.jdk8.BaseWebServiceTest;
import com.magenic.jmaqs.webservices.jdk8.WebServiceUtilities;
import jmaqs.helpers.SoapConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.example.models.countries.GetCountryRequest;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * The type Web service test.
 */
public class WebServiceRestTest extends BaseWebServiceTest {

  private final MessageFactory messageFactory = MessageFactory.newInstance();
  private final SOAPMessage soapMessage = messageFactory.createMessage();

  public WebServiceRestTest() throws SOAPException {
  }

  @Test
  public void SOAPJMAQSXMLStringTest() throws IOException, URISyntaxException, JAXBException {
    String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\r\nxmlns:gs=\"http://spring.io/guides/gs-producing-web-service\">\r\n<soapenv:Header/>\r\n<soapenv:Body>\r\n<gs:getCountryRequest>\r\n<gs:name>Spain</gs:name>\r\n</gs:getCountryRequest>\r\n</soapenv:Body>\r\n</soapenv:Envelope>";
    System.out.println(xml);
    StringEntity entity = new StringEntity(xml, ContentType.TEXT_XML);
    CloseableHttpClient client = HttpClientBuilder.create().build();
    //CloseableHttpClient closeableHttpClient = this.getTestObject().getWebServiceDriver().getHttpClient("text/xml");
    HttpPost request = new HttpPost("http://localhost:8080/ws");
    request.setHeader("Content-Type", "text/xml");
    request.setEntity(entity);
    CloseableHttpResponse execute = client.execute(request);
//    CloseableHttpResponse closeableHttpResponse = this.getTestObject().getWebServiceDriver()
//        .postContent("http://localhost:8080/ws", stringEntity, ContentType.TEXT_XML, false);
   Assert.assertEquals(execute.getStatusLine().getStatusCode(), 200);
  }

  @Test
  public void SOAPJMAQSMarshallTest() throws JAXBException, IOException, SOAPException, ParserConfigurationException, SAXException {
    GetCountryRequest getCountryRequest = new GetCountryRequest();
    getCountryRequest.setName("Spain");
    JAXBContext jaxbContext = JAXBContext.newInstance(GetCountryRequest.class);
    Marshaller marshaller = jaxbContext.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    OutputStream outputStream = new ByteArrayOutputStream();
    marshaller.marshal(getCountryRequest, outputStream);
    outputStream.flush();
    outputStream.close();
    System.out.println(outputStream.toString());

    MessageFactory messageFactory = MessageFactory.newInstance();
    SOAPMessage message = messageFactory.createMessage();
    SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
    envelope.removeNamespaceDeclaration(envelope.getPrefix());
    envelope.addNamespaceDeclaration("gs","http://spring.io/guides/gs-producing-web-service");
    envelope.addNamespaceDeclaration("soapenv","http://schemas.xmlsoap.org/soap/envelope/");
    envelope.setPrefix("soapenv");
    SOAPBody body = envelope.getBody();
    SOAPHeader header = envelope.getHeader();
    header.setPrefix("soapenv");
    body.setPrefix("soapenv");
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(true);
    documentBuilderFactory.setSchema(marshaller.getSchema());
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    Document document = documentBuilder.parse(new InputSource(new StringReader(outputStream.toString())));
    body.addDocument(document);

    message.saveChanges();
    OutputStream messageOutputStream = new ByteArrayOutputStream();
    message.writeTo(messageOutputStream);
    System.out.println(messageOutputStream);
    StringEntity entity = new StringEntity(messageOutputStream.toString(), ContentType.TEXT_XML);
    CloseableHttpClient client = HttpClientBuilder.create().build();
    //CloseableHttpClient closeableHttpClient = this.getTestObject().getWebServiceDriver().getHttpClient("text/xml");
    HttpPost request = new HttpPost("http://10.10.10.100:8080/ws");
    request.setHeader("Content-Type", "text/xml");
    request.setEntity(entity);
    CloseableHttpResponse execute = client.execute(request);
//    CloseableHttpResponse closeableHttpResponse = this.getTestObject().getWebServiceDriver()
//        .postContent("http://localhost:8080/ws", stringEntity, ContentType.TEXT_XML, false);
    Assert.assertEquals(execute.getStatusLine().getStatusCode(), 200);
  }

  @Test
  public void SOAPJMAQSTest() throws JAXBException, IOException, SOAPException, ParserConfigurationException, SAXException, URISyntaxException {
    this.getLogger().logMessage(MessageType.INFORMATION, "Starting data manipulations");
    GetCountryRequest getCountryRequest = new GetCountryRequest();
    getCountryRequest.setName("Spain");
    String outputStream = createSoapPayload(getCountryRequest);

    this.getLogger().logMessage(MessageType.INFORMATION, "Crafting SOAP Message");
    SOAPEnvelope envelope = modifySoapPrefix(soapMessage.getSOAPPart().getEnvelope());
    addNameSpaceToMessage(envelope, "gs","http://spring.io/guides/gs-producing-web-service");
    addStringToBody(outputStream, soapMessage);
    String messageOutputStream = finalizeSoapMessage(soapMessage);
    this.getLogger().logMessage(MessageType.INFORMATION, "Completed and saved SOAP Message changes");
    this.getLogger().logMessage(MessageType.VERBOSE, messageOutputStream);
    StringEntity entity = new StringEntity(messageOutputStream, ContentType.TEXT_XML);
    CloseableHttpResponse closeableHttpResponse = this.getTestObject().getWebServiceDriver()
        .postContent("/ws", entity, ContentType.TEXT_XML, false);
    Assert.assertEquals(closeableHttpResponse.getStatusLine().getStatusCode(), 200);
  }

  @Test
  public void SOAPJMAQSUsingOverloadsTest() throws JAXBException, IOException, SOAPException, ParserConfigurationException, SAXException, URISyntaxException {
    this.getLogger().logMessage(MessageType.INFORMATION, "Starting data manipulations");
    GetCountryRequest getCountryRequest = new GetCountryRequest();
    getCountryRequest.setName("Spain");
    String outputStream = createSoapPayload(getCountryRequest);
    String messageOutputStream = getDefaultSoapMessage(outputStream);
    StringEntity entity = WebServiceUtilities.createStringEntity(messageOutputStream, ContentType.TEXT_XML);
    CloseableHttpResponse closeableHttpResponse = this.getTestObject().getWebServiceDriver()
            .postContent("/ws", entity, ContentType.TEXT_XML, false);
    Assert.assertEquals(closeableHttpResponse.getStatusLine().getStatusCode(), 200);
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


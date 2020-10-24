package org.example.test;

import com.magenic.jmaqs.utilities.logging.MessageType;
import com.magenic.jmaqs.webservices.jdk8.BaseWebServiceTest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.example.models.countries.GetCountryRequest;
import org.springframework.ws.soap.SoapEnvelope;
import org.springframework.ws.soap.SoapMessage;
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
import javax.xml.validation.Schema;
import java.io.*;
import java.net.URISyntaxException;

/**
 * The type Web service test.
 */
public class WebServiceRestTest extends BaseWebServiceTest {



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
    this.getLogger().logMessage(MessageType.INFORMATION, "Starting marshalling process");
    JAXBContext jaxbContext = JAXBContext.newInstance(GetCountryRequest.class);
    Marshaller marshaller = jaxbContext.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    OutputStream outputStream = new ByteArrayOutputStream();
    marshaller.marshal(getCountryRequest, outputStream);
    outputStream.flush();
    outputStream.close();
    this.getLogger().logMessage(MessageType.INFORMATION, "Completed marshalling");
    this.getLogger().logMessage(MessageType.VERBOSE, outputStream.toString());

    this.getLogger().logMessage(MessageType.INFORMATION, "Crafting SOAP Message");
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
    this.getLogger().logMessage(MessageType.INFORMATION, "Completed and saved SOAP Message changes");
    this.getLogger().logMessage(MessageType.VERBOSE, messageOutputStream.toString());
    StringEntity entity = new StringEntity(messageOutputStream.toString(), ContentType.TEXT_XML);
    CloseableHttpResponse closeableHttpResponse = this.getTestObject().getWebServiceDriver()
        .postContent("/ws", entity, ContentType.TEXT_XML, false);
    Assert.assertEquals(closeableHttpResponse.getStatusLine().getStatusCode(), 200);
  }


}


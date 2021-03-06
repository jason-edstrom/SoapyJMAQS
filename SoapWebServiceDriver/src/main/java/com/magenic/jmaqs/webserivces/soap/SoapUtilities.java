package com.magenic.jmaqs.webserivces.soap;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.apache.http.client.methods.CloseableHttpResponse;

public class SoapUtilities {

  private SoapUtilities()
  {

  }
  public static Object getResponseBodyasObjects(CloseableHttpResponse closeableHttpResponse, Unmarshaller unMarshaller)
      throws SOAPException, IOException, JAXBException {
    SOAPMessage soapResp = MessageFactory.newInstance().createMessage(null, closeableHttpResponse.getEntity().getContent());
    return unMarshaller.unmarshal(soapResp.getSOAPBody().extractContentAsDocument());
  }
}

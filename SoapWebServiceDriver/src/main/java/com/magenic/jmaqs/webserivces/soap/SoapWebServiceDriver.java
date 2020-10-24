package com.magenic.jmaqs.webserivces.soap;

import com.fasterxml.jackson.databind.util.ClassUtil;
import com.magenic.jmaqs.webservices.jdk8.WebServiceConfig;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class SoapWebServiceDriver {

    private Jaxb2Marshaller marshaller;

    private WebServiceTemplate webServiceTemplate;

    private URI baseAddress;

    public SoapWebServiceDriver(String baseAddress) throws URISyntaxException {
        this.baseAddress = new URI(baseAddress);
        this.marshaller = new Jaxb2Marshaller();
    }

    public SoapWebServiceDriver(Jaxb2Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public SoapWebServiceDriver(Jaxb2Marshaller marshaller, String baseAddress) throws URISyntaxException {
        this.marshaller = marshaller;
        this.baseAddress = new URI(baseAddress);
    }

    public void setupWebServiceTemplate(){
        webServiceTemplate = new WebServiceTemplate(this.marshaller);
    }

    public <T> void setupMarshaller(T type) throws Exception {
        marshaller.setPackagesToScan(ClassUtil.getPackageName(type.getClass()));
        //marshaller.afterPropertiesSet();
    }

    public <T> Object sendMessage(T object) throws Exception {
        setupMarshaller(object);
        setupWebServiceTemplate();
        return webServiceTemplate.marshalSendAndReceive(WebServiceConfig.getWebServiceUri(), object);
    }


    public Jaxb2Marshaller getMarshaller() {
        return marshaller;
    }
}

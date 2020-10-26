package org.example.test;

import com.magenic.jmaqs.webservices.jdk8.BaseWebServiceTest;
import jmaqs.helpers.SoapWebServiceDriver;
import jmaqs.helpers.SoapWebServiceDriverFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.soap.SOAPException;
import java.net.URISyntaxException;

public class SoapWebServiceDriverTest extends BaseWebServiceTest {

    @BeforeMethod
    public void setUp() throws URISyntaxException, SOAPException {
        SoapWebServiceDriver defaultSoapDriver = SoapWebServiceDriverFactory.getDefaultSoapDriver();
    }

    @Test
    public void SOAPDriverTest () throws Exception {

    }
}

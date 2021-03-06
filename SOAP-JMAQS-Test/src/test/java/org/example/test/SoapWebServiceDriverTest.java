package org.example.test;

import com.magenic.jmaqs.utilities.logging.MessageType;
import com.magenic.jmaqs.webservices.jdk8.BaseWebServiceTest;
import java.net.URISyntaxException;
import javax.xml.soap.SOAPException;
import jmaqs.helpers.SoapUtilities;
import jmaqs.helpers.SoapWebServiceDriver;
import jmaqs.helpers.SoapWebServiceDriverFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.example.models.countries.GetCountryRequest;
import org.example.models.countries.GetCountryResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SoapWebServiceDriverTest extends BaseWebServiceTest {

    @BeforeMethod
    public void setUp() throws URISyntaxException, SOAPException {
        SoapWebServiceDriver defaultSoapDriver = SoapWebServiceDriverFactory.getDefaultSoapDriver();
        this.setWebServiceDriver(defaultSoapDriver);
    }

    @Test
    public void SOAPDriverTest () throws Exception {
        GetCountryRequest getCountryRequest = new GetCountryRequest();
        getCountryRequest.setName("Spain");
        this.getSoapDriver().addObjectToSoapMessage(getCountryRequest);
        this.getLogger().logMessage(MessageType.INFORMATION, getSoapDriver().getSoapBodyString());
        CloseableHttpResponse closeableHttpResponse =
            this.getSoapDriver().postContent("ws", this.getSoapDriver().getSoapBodyStringEntity(), ContentType.TEXT_XML, true);
        GetCountryResponse countryResponse =
            (GetCountryResponse) SoapUtilities.getResponseBodyasObjects(closeableHttpResponse, this.getSoapDriver().getUnMarshaller(
                new GetCountryResponse()));

        Assert.assertEquals(countryResponse.getCountry().getCapital(), "Madrid");
        Assert.assertEquals(countryResponse.getCountry().getPopulation(), 46704314);
    }

    public SoapWebServiceDriver getSoapDriver()
    {
        return (SoapWebServiceDriver) this.getTestObject().getWebServiceDriver();
    }
}

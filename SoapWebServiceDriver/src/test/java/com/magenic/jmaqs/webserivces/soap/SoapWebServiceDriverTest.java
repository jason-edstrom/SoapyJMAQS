package com.magenic.jmaqs.webserivces.soap;

import com.magenic.jmaqs.webserivces.soap.models.GetCountryRequest;
import org.testng.annotations.Test;

public class SoapWebServiceDriverTest extends BaseSoapWebServiceTest {

    @Test
    public void testMarhsaller() throws Exception {
        GetCountryRequest request = new GetCountryRequest();
        request.setName("Spain");
        this.getWebServiceDriver().sendMessage(request);
    }
}
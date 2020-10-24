package org.example.test;

import com.magenic.jmaqs.webserivces.soap.BaseSoapWebServiceTest;
import org.example.models.countries.GetCountryRequest;
import org.testng.annotations.Test;

/**
 * The type Web service test.
 */
public class WebServiceSoapTest extends BaseSoapWebServiceTest {

  @Test
  public void SOAPDriverTest () throws Exception {
    GetCountryRequest request = new GetCountryRequest();
    request.setName("Spain");

    this.getTestObject().getWebServiceDriver().sendMessage(request);

    //SoapWebServiceDriver driver = new SoapWebServiceDriver(WebServiceConfig.getWebServiceUri());
    //driver.sendMessage(request);
  }

//  @Test
//  public void SOAPDriverTestNull () throws Exception {
//    GetCountryRequest request = new GetCountryRequest();
//    request.setName(null);
//
//    this.getTestObject().getWebServiceDriver().sendMessage(request);
//
//    //SoapWebServiceDriver driver = new SoapWebServiceDriver(WebServiceConfig.getWebServiceUri());
//    //driver.sendMessage(request);
//  }
}

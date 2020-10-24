package com.magenic.jmaqs.webserivces.soap;/*
 * Copyright 2020 (C) Magenic, All rights Reserved
 */

import com.magenic.jmaqs.base.BaseExtendableTest;
import com.magenic.jmaqs.utilities.helper.StringProcessor;
import com.magenic.jmaqs.utilities.logging.Logger;
import com.magenic.jmaqs.webservices.jdk8.WebServiceConfig;
import org.testng.ITestResult;

import java.net.URISyntaxException;

/**
 * Base web service test class.
 */
public class BaseSoapWebServiceTest extends BaseExtendableTest<SoapWebServiceTestObject> {

  /**
   * Get the Web Service Driver.
   *
   * @return WebServiceDriver
   */
  public SoapWebServiceDriver getWebServiceDriver() {
    return this.getTestObject().getWebServiceDriver();
  }

  /**
   * Set the webServiceDriver.
   *
   * @param webServiceDriver the webservice driver object
   */
  public void setWebServiceDriver(SoapWebServiceDriver webServiceDriver) {
    this.getTestObject().setWebServiceDriver(webServiceDriver);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.magenic.jmaqs.utilities.BaseTest.BaseTest# beforeLoggingTeardown
   * (org.testng.ITestResult)
   */
  @Override
  protected void beforeLoggingTeardown(ITestResult resultType) {
    // There is no before logging tear-down required
  }

  /**
   * Gets new WebServiceDriver.
   *
   * @return WebServiceDriver
   * @throws URISyntaxException when URI is incorrect
   */
  protected SoapWebServiceDriver getWebServiceClient() throws URISyntaxException {
    return new SoapWebServiceDriver(WebServiceConfig.getWebServiceUri());
  }

  /**
   * Creates a new test object.
   */
  @Override
  protected void createNewTestObject() {
    Logger logger = this.createLogger();
    try {

      SoapWebServiceTestObject webServiceTestObject = new SoapWebServiceTestObject(
          this.getWebServiceClient(), logger, this.getFullyQualifiedTestClassName());
      this.setTestObject(webServiceTestObject);
    } catch (URISyntaxException e) {
      getLogger().logMessage(
          StringProcessor.safeFormatter("Test Object could not be created: %s", e.getMessage()));
    }
  }
}

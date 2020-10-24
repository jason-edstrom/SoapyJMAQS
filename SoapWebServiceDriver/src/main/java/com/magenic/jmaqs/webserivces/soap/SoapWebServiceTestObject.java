package com.magenic.jmaqs.webserivces.soap;

import com.magenic.jmaqs.base.BaseTestObject;
import com.magenic.jmaqs.utilities.logging.Logger;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.function.Supplier;

public class SoapWebServiceTestObject extends BaseTestObject {
    public SoapWebServiceTestObject(Supplier<Jaxb2Marshaller> getDriverSupplier, Logger logger, String fullyQualifiedTestName) {
        super(logger, fullyQualifiedTestName);
        this.getManagerStore().put((SoapWebServiceDriverManager.class).getCanonicalName(), new SoapWebServiceDriverManager(getDriverSupplier, this));
    }

    public SoapWebServiceTestObject(SoapWebServiceDriver driver,
                                    Logger logger, String fullyQualifiedTestName) {
        super(logger, fullyQualifiedTestName);
        this.getManagerStore().put((SoapWebServiceDriverManager.class).getCanonicalName(),
                new SoapWebServiceDriverManager(driver, this));
    }

    /**
     * Get the web service wrapper for the WebServiceTestObject.
     *
     * @return A web service driver
     */
    public SoapWebServiceDriver getWebServiceDriver() {
        return this.getWebServiceDriverManager().getWebServiceDriver();
    }

    /**
     * Gets the Web Service driver managers.
     *
     * @return the web service manager.
     */
    public SoapWebServiceDriverManager getWebServiceDriverManager() {
        return (SoapWebServiceDriverManager) this.getManagerStore()
                .get(SoapWebServiceDriverManager.class.getCanonicalName());
    }

    /**
     * overrides the web service driver using the WebServiceDriver.
     *
     * @param driver the Web Service Driver to be committed.
     */
    public void setWebServiceDriver(SoapWebServiceDriver driver) {
        this.getManagerStore().put(SoapWebServiceDriverManager.class.getCanonicalName(),
                new SoapWebServiceDriverManager(driver, this));
    }

    /**
     * overrides the web service driver using a closable Http Client.
     *
     * @param httpClient the Http Client to be set.
     */
    public void setWebServiceDriver(Jaxb2Marshaller httpClient) {
        this.getManagerStore().put(SoapWebServiceDriverManager.class.getCanonicalName(),
                new SoapWebServiceDriverManager((() -> httpClient), this));
    }

    /**
     * overrides the Web Service driver using a supplier.
     *
     * @param webServiceSupplier the web service driver supplier.
     */
    public void setWebServiceDriver(Supplier<Jaxb2Marshaller> webServiceSupplier) {
        this.getManagerStore().put(SoapWebServiceDriverManager.class.getCanonicalName(),
                new SoapWebServiceDriverManager(webServiceSupplier, this));
    }
}

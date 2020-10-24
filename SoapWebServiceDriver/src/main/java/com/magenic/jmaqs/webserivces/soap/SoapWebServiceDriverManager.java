package com.magenic.jmaqs.webserivces.soap;

import com.magenic.jmaqs.base.DriverManager;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.function.Supplier;


public class SoapWebServiceDriverManager extends DriverManager<Jaxb2Marshaller> {

    /**
     * Web Service Driver variable.
     */
    private SoapWebServiceDriver webServiceDriver;

    public SoapWebServiceDriverManager(Supplier<Jaxb2Marshaller> getDriverSupplier, SoapWebServiceTestObject soapWebServiceTestObject) {
        super(getDriverSupplier, soapWebServiceTestObject);
    }

    public SoapWebServiceDriverManager(SoapWebServiceDriver driver, SoapWebServiceTestObject soapWebServiceTestObject) {
        super(driver::getMarshaller, soapWebServiceTestObject);
        this.webServiceDriver = driver;
    }

    public SoapWebServiceDriver getWebServiceDriver()
    {
        if (this.webServiceDriver == null) {
            this.webServiceDriver = new SoapWebServiceDriver(this.getBase());
        }

        return this.webServiceDriver;
    }

    public void overrideDriver (SoapWebServiceDriver driver)
    {
        this.webServiceDriver = driver;
    }

    @Override
    public void close() throws Exception {
        this.setBaseDriver(null);
    }
}

package com.fias;

import java.math.BigDecimal;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BigDecimalXmlAdapter extends XmlAdapter<String,BigDecimal> {

    @Override
    public String marshal(BigDecimal bigDecimal) throws Exception {
        if (bigDecimal != null){
            return bigDecimal.toString();
        }
        else {
            return null;
        }

    }

    @Override
    public BigDecimal unmarshal(String string) throws Exception {
        try {
            return new BigDecimal(string);
        } catch (NumberFormatException e) {
            return null;
        }

    }

}
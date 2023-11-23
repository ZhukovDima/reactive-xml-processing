package com.datasrc.processor;

import java.io.InputStream;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.xml.namespace.QName;

public abstract class XmlProcessor {
    protected static final QName ELEMENT_TO_EXTRACT = new QName("http://www.example.com/WebServices", "ZipFile");
    protected static final QName TRANSACTION_ELEMENT = new QName("transaction");
    protected static final Decoder Base64Decoder = Base64.getMimeDecoder();
    protected static final int BUFFER_SIZE = 8192;
    protected static final int COMMIT_COUNT = 100;

    public abstract void process(InputStream stream);
}

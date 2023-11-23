package com.datasrc.processor;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public abstract class AbstractXmlStreamProcessorTest {

    private static final String XML_FILE = "src/test/resources/xml/response.xml";

    private final XmlProcessor xmlProcessor;

    public AbstractXmlStreamProcessorTest(XmlProcessor xmlProcessor) {
        this.xmlProcessor = xmlProcessor;
    }

    @Test
    public void testProcess() throws Exception {
        try (InputStream stream = Files.newInputStream(Paths.get(XML_FILE))) {
            xmlProcessor.process(stream);
        }
    }
}

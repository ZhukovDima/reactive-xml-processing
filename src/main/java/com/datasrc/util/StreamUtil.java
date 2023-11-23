package com.datasrc.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.input.ReaderInputStream;

import com.datasrc.util.xml.AutoClosableXMLEventReader;
import com.datasrc.util.xml.AutoClosableXMLStreamReader;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StreamUtil {

    private static final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();

    public static InputStream toInputStream(Reader reader) {
        try {
            return ReaderInputStream.builder()
                    .setCharset(StandardCharsets.UTF_8)
                    .setReader(reader)
                    .get();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static AutoClosableXMLStreamReader toAutoClosableXMLStreamReader(InputStream stream) {
        try {
            return new AutoClosableXMLStreamReader(stream, xmlInputFactory.createXMLStreamReader(stream));
        } catch (XMLStreamException ex) {
            throw new UncheckedIOException(new IOException(ex));
        }
    }

    public static AutoClosableXMLEventReader toAutoClosableXMLEventReader(InputStream stream) {
        try {
            return new AutoClosableXMLEventReader(stream, xmlInputFactory.createXMLEventReader(stream));
        } catch (XMLStreamException ex) {
            throw new UncheckedIOException(new IOException(ex));
        }
    }
}

package com.datasrc.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A wrapper class that implements the {@link XMLEventReader} interface and
 * also extends the {@link AutoCloseable} interface. It provides automatic
 * closing of the underlying XML event reader and input stream.
 */
@Slf4j
@RequiredArgsConstructor
public class AutoClosableXMLEventReader implements XMLEventReader, AutoCloseable {

    private final InputStream inputStream;
    private final XMLEventReader xmlEventReader;

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        return xmlEventReader.nextEvent();
    }

    @Override
    public boolean hasNext() {
        return xmlEventReader.hasNext();
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        return xmlEventReader.peek();
    }

    @Override
    public String getElementText() throws XMLStreamException {
        return xmlEventReader.getElementText();
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        return xmlEventReader.nextTag();
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return xmlEventReader.getProperty(name);
    }

    @Override
    public void close() {
        log.info("XmlEventReader0.close()");
        try {
            xmlEventReader.close();
        } catch (XMLStreamException ex) {
            throw new UncheckedIOException(new IOException(ex));
        }

        // XMLStreamReader does not close the underlying input source.
        log.info("InputStream.close()");
        try {
            inputStream.close();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public Object next() {
        return xmlEventReader.next();
    }
}

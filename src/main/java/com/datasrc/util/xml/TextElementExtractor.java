package com.datasrc.util.xml;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is a custom implementation of the Reader class that extracts text elements from an XML stream.
 */
@Slf4j
@RequiredArgsConstructor
public class TextElementExtractor extends Reader {

    private final XMLStreamReader xmlStreamReader;
    private final QName qName;

    private boolean initialized;
    private boolean closed;
    private CharArrayReader reader;

    /**
     * Reads a single character from the text element.
     *
     * @return The character read, or -1 if the end of the text element has been reached.
     * @throws IOException If an I/O error occurs.
     */
    public int read() throws IOException {
        if (prepareBuffer()) {
            return reader.read();
        }
        return -1;
    }

    /**
     * Reads characters into an array from the text element.
     *
     * @param cbuf The buffer into which the data is read.
     * @param off  The start offset in the buffer.
     * @param len  The maximum number of characters to read.
     * @return The number of characters read, or -1 if the end of the text element has been reached.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (prepareBuffer()) {
            return reader.read(cbuf, off, len);
        }
        return -1;
    }

    /**
     * Closes the text element extractor.
     */
    @Override
    public void close() {
        log.info("TextElementExtractor.close()");
        if (closed) {
            return;
        }
        this.closed = true;
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }

    private boolean prepareBuffer() throws IOException {
        ensureInit();
        try {
            if (reader != null && reader.ready()) {
                return true;
            }
            if (!xmlStreamReader.hasNext()) {
                return false;
            }
            xmlStreamReader.next();
            if (xmlStreamReader.isCharacters()) {
                char[] buf = xmlStreamReader.getTextCharacters();
                int offset = xmlStreamReader.getTextStart();
                int length = xmlStreamReader.getTextLength();
                this.reader = new CharArrayReader(buf, offset, length);
                return true;
            } else {
                return false;
            }
        } catch (XMLStreamException ex) {
            throw new IOException(ex);
        }
    }

    private void ensureInit() throws IOException {
        if (closed) {
            throw new IOException("Stream is closed");
        }
        if (initialized) {
            return;
        }
        try {
            while (xmlStreamReader.hasNext()) {
                xmlStreamReader.next();
                if (xmlStreamReader.isStartElement() && qName.equals(xmlStreamReader.getName())) {
                    break;
                }
            }
        } catch (XMLStreamException ex) {
            throw new IOException(ex);
        }
        this.initialized = true;
    }
}

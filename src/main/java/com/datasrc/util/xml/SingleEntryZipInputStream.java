package com.datasrc.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import lombok.extern.slf4j.Slf4j;

/**
 * This class extends the {@link ZipInputStream} class to provide a specialized implementation
 * that allows reading a single entry from a ZIP file.
 */

@Slf4j
public class SingleEntryZipInputStream extends ZipInputStream {

    private boolean initialized;
    private boolean closed;

    /**
     * Constructs a new SingleEntryZipInputStream with the specified input stream.
     *
     * @param in the input stream to read from
     */
    public SingleEntryZipInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        ensureInit();
        return super.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        ensureInit();
        return super.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
        log.info("ZipInputStream.close()");
        if (closed) {
            return;
        }
        this.closed = true;
        super.close();
    }

    private void ensureInit() throws IOException {
        if (closed) {
            throw new IOException("Stream is closed");
        }

        if (initialized) {
            return;
        }
        getNextEntry();
        initialized = true;
    }

}

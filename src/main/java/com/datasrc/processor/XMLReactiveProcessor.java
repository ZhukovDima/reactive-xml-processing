package com.datasrc.processor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import com.datasrc.util.CloseableHolder;
import com.datasrc.util.StreamUtil;
import com.datasrc.util.xml.SingleEntryZipInputStream;
import com.datasrc.util.xml.TextElementExtractor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * This class represents a reactive XML processor that extends the XmlProcessor class.
 * It provides methods to process XML data in a reactive manner.
 */
@Slf4j
public class XMLReactiveProcessor extends XmlProcessor {

    /**
     * Holder for managing the closeable resource associated with XML processing.
     */
    private final CloseableHolder streamCloseableHolder = new CloseableHolder();
    private final CloseableHolder fluxCloseableHolder = new CloseableHolder();

    @Override
    public void process(InputStream input) {
        var stream = Stream.of(input)
                .map(it -> StreamUtil.toAutoClosableXMLStreamReader(input))
                .map(it -> streamCloseableHolder.add(it))
                .map(it -> new TextElementExtractor(it, ELEMENT_TO_EXTRACT))
                .map(it -> StreamUtil.toInputStream(it))
                .map(it -> Base64Decoder.wrap(it))
                .map(it -> new BufferedInputStream(it, BUFFER_SIZE))
                .map(it -> new SingleEntryZipInputStream(it))
                .map(it -> streamCloseableHolder.add(it))
                .onClose(streamCloseableHolder);

        Flux.fromStream(stream)
                .map(it -> StreamUtil.toAutoClosableXMLEventReader(it))
                .map(it -> fluxCloseableHolder.add(it))
                .flatMapIterable(XMLEventIterable::new)
                .filter(XMLEvent::isStartElement)
                .map(XMLEvent::asStartElement)
                .filter(it -> TRANSACTION_ELEMENT.equals(it.getName()))
                .buffer(COMMIT_COUNT)
                .doOnNext(it -> {
                    log.info("Commiting {} elements {}", COMMIT_COUNT, it);
                })
                .doFinally(it -> fluxCloseableHolder.run())
                .subscribe();
    }

    /**
     * An iterable implementation for iterating over XML events.
     */

    private record XMLEventIterable(XMLEventReader xmlEventReader) implements Iterable<XMLEvent> {

        @Override
        public Iterator<XMLEvent> iterator() {
            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return xmlEventReader.hasNext();
                }

                @Override
                public XMLEvent next() {
                    try {
                        return xmlEventReader.nextEvent();
                    } catch (XMLStreamException ex) {
                        throw new UncheckedIOException(new IOException(ex));
                    }
                }
            };
        }

        @Override
        public void forEach(Consumer<? super XMLEvent> action) {
            Iterable.super.forEach(action);
        }

        @Override
        public Spliterator<XMLEvent> spliterator() {
            return Iterable.super.spliterator();
        }
    }
}

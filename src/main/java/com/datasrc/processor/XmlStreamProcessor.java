package com.datasrc.processor;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.xml.stream.events.XMLEvent;

import com.datasrc.util.StreamUtil;
import com.datasrc.util.xml.AutoClosableXMLEventReader;
import com.datasrc.util.xml.AutoClosableXMLStreamReader;
import com.datasrc.util.xml.SingleEntryZipInputStream;
import com.datasrc.util.xml.TextElementExtractor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XmlStreamProcessor extends XmlProcessor {

    @Override
    public void process(InputStream stream) {
        try (
                AutoClosableXMLStreamReader xmlStreamReader = StreamUtil.toAutoClosableXMLStreamReader(stream);
                Reader textElementExtractor = new TextElementExtractor(xmlStreamReader, ELEMENT_TO_EXTRACT);
                InputStream base64 = Base64Decoder.wrap(StreamUtil.toInputStream(textElementExtractor));
                BufferedInputStream bufferedInputStream = new BufferedInputStream(base64, BUFFER_SIZE);
                ZipInputStream zipInputStream = new SingleEntryZipInputStream(bufferedInputStream);
                AutoClosableXMLEventReader xmlEventReader = StreamUtil.toAutoClosableXMLEventReader(zipInputStream)
        ) {
            long total = 0;
            long cnt = 0;
            List<XMLEvent> toCommit = new ArrayList<>();
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement() && TRANSACTION_ELEMENT.equals(xmlEvent.asStartElement().getName())) {
                    total++;
                    cnt++;
                    toCommit.add(xmlEvent);
                    if (cnt > COMMIT_COUNT) {
                        log.info("Committing {} elements {}", cnt, toCommit);
                        cnt = 0;
                        toCommit.clear();
                    }
                }
            }

            if (cnt > 0) {
                log.info("Committing {} elements {}", cnt, toCommit);
                toCommit.clear();
            }
            log.info("Total elements: {}", total);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

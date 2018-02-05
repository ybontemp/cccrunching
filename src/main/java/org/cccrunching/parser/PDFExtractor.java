package org.cccrunching.parser;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.*;

public class PDFExtractor {

    private final static Logger LOG = LoggerFactory.getLogger(PDFExtractor.class);

    private String pdfInputStreamToText(InputStream is) throws TikaException, SAXException, IOException {
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();

        ParseContext pcontext = new ParseContext();

        //parsing the document using PDF parser
        PDFParser pdfparser = new PDFParser();
        pdfparser.parse(is, handler, metadata, pcontext);

        //getting the content of the document
        String content = handler.toString();

        //getting metadata of the document
        LOG.debug(metadata.toString());
        return content;
    }

    public String pdfAsText(File file){
        LOG.info("Processing PDF extraction from file {} -- START", file);
        try (InputStream inputstream = new BufferedInputStream(new FileInputStream(file))) {
            String str = pdfInputStreamToText(inputstream);
            LOG.info("Processing PDF extraction from file {} -- DONE", file);
            return str;
        } catch (IOException | TikaException | SAXException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Converts a PDF from the classpath into text
     * @param classPathURL
     * @return
     */
    public String pdfAsText(String classPathURL) {
        LOG.info("Processing PDF extraction from classpath URL {} -- START", classPathURL);
        try (InputStream inputstream = this.getClass().getResourceAsStream(classPathURL)) {
            String str = pdfInputStreamToText(inputstream);
            LOG.info("Processing PDF extraction from classpathURL {} -- DONE", classPathURL);
            return str;
        } catch (IOException | TikaException | SAXException e) {
            throw new RuntimeException(e);
        }

    }

}

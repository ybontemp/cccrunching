package org.cccrunching.parser;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.cccrunching.data.MeetingItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MeetingItemAnalyzer {

    private final static Logger LOG = LoggerFactory.getLogger(MeetingItemAnalyzer.class);

    private final StanfordCoreNLP pipeline;

    public MeetingItemAnalyzer(){
        Properties props = new Properties();
        try(InputStream is = this.getClass().getResourceAsStream("/StanfordCoreNLP-french.properties")) {
            props.load(is);
            props.setProperty("annotators", "tokenize, ssplit, pos, parse,lemma,ner");
            String annotators = StanfordCoreNLP.ensurePrerequisiteAnnotators(props.getProperty("annotators").split("[, \t]+"), props);
            props.setProperty("annotators", annotators);
            props.setProperty("ner.language", "french");
            pipeline = new StanfordCoreNLP(props);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void analyze(MeetingItem item) throws IOException {
        if (item.getDiscussion() != null){
            // read some text in the text variable
            String text = item.getDiscussion();

            // create an empty Annotation just with the given text
            Annotation document = new Annotation(text);

            // run all Annotators on this text
            pipeline.annotate(document);
            for (Class<?> key : document.keySet()) {
                System.out.println(key);
            }

            LOG.debug("Analysis done");
        }
    }
}

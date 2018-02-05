package org.cccrunching;

import com.google.gson.Gson;
import org.cccrunching.data.Meeting;
import org.cccrunching.data.MeetingItem;
import org.cccrunching.parser.lalouviere.LaLouvMeetingParser;
import org.cccrunching.parser.MeetingParser;
import org.cccrunching.parser.PDFExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class App {

    private final static Logger LOG = LoggerFactory.getLogger(App.class);

    public final static void main(String[] args) throws IOException {
        if (args.length < 2){
            throw new RuntimeException("Too few arguments, missing directory to index and output file");
        }
        String directory = args[0];
        String output    = args[1];
        List<Meeting> allMeetings = new ArrayList<>();
        LOG.info("Processing all PDFs in {}",directory);
        Files.list(Paths.get(directory))
                .filter(Files::isRegularFile)
                .forEach(f -> {
                            LOG.info("Parsing meeting information from {}",f);
                          PDFExtractor pdfExtractor = new PDFExtractor();
                          String text = pdfExtractor.pdfAsText(f.toFile());
                          MeetingParser meetingParser = new LaLouvMeetingParser();
                          try {
                              Meeting meeting = meetingParser.parse(text);
                              allMeetings.add(meeting);
                          } catch (IllegalArgumentException e){
                              LOG.warn("Skipping {} because it was not parseable",f);
                              LOG.warn("Exception that failed the parsing",e);
                          }
                      }
              );
        Collections.sort(allMeetings, Comparator.comparing(m -> m.getMeetingDate().orElse(LocalDate.MIN)));
        LOG.info("Found {} meetings for a total of {} items", allMeetings.size(), allMeetings.stream().map(Meeting::getItems).collect(Collectors.summingInt(List::size)));
        Gson gson = new Gson();
        LOG.info("Storing result in {}", output);
        try(BufferedWriter w =Files.newBufferedWriter(Paths.get(output))){
            gson.toJson(allMeetings,w);
        }

        LOG.info("Number of unanimous votes: {}", allMeetings.stream().map(Meeting::getItems).flatMap(List::stream).filter(x -> x.isUnanimousVote().orElse(true)).collect(Collectors.counting()));
        LOG.info("Attendance report {}",allMeetings.stream().map(Meeting::getAttendees).flatMap(List::stream).collect(Collectors.groupingBy(p -> p.getName(), Collectors.counting())));

        List<MeetingItem> contentiousItems = allMeetings.stream().map(Meeting::getItems).flatMap(List::stream).filter(x -> !x.isUnanimousVote().orElse(true)).collect(Collectors.toList());
        LOG.info("Number of contentious items: {}", contentiousItems.size());
        /*
        LOG.info("Indexing in Elasticsearch");
        try (Indexer indexer = new ESIndexer()) {
            indexer.index(allMeetings);
        }
        */
        /*
        List<String> list = textExtractor.extractAgendaItems(text);
        Map<String, MeetingItem> debateItems = textExtractor.splitDebateItems(list.get(1));
        System.out.println(debateItems);
        String firstDebate = debateItems.values().stream().filter(i -> i.getDiscussion() != null).map(MeetingItem::getDiscussion).findFirst().orElse("");
        MeetingItemAnalyzer meetingItemAnalyzer = new MeetingItemAnalyzer();
        meetingItemAnalyzer.analyze(debateItems.values().stream().filter(i -> i.getDiscussion() != null).findFirst().orElseThrow(() ->new RuntimeException("No item found")));
        */
    }
}

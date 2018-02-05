package org.cccrunching.parser.lalouviere;

import org.cccrunching.data.Meeting;
import org.cccrunching.data.MeetingItem;
import org.cccrunching.data.Person;
import org.cccrunching.parser.MeetingParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A class implementing Meeting parsing logic for meeting minutes
 * from the city of La Louvière.
 */
public class LaLouvMeetingParser implements MeetingParser {

    private final static Logger LOG = LoggerFactory.getLogger(LaLouvMeetingParser.class);

    private static final String INTRODUCTION = "Avant-Séance";
    public static final String ATTENDEES_BLOCK_START = "Sont présents : ";
    private static Pattern PATTERN_OPENING = Pattern.compile("La séance est ouverte à [0-9]+ h(eures)? [0-9]*", Pattern.DOTALL);

    private static Pattern PATTERN_DEBATE_ITEMS = Pattern.compile("\\s*([0-9]+\\.- .*)");
    private static Pattern PATTERN_DECISION_MARKER = Pattern.compile("\\s*Le Conseil,.*");
    private static Pattern PATTERN_DEBATE_MARKER = Pattern.compile("\\s*(M\\..*)\\s*:.*");


    public List<String> extractAgendaItems(String str){
        String[] split = PATTERN_OPENING.split(str);
        return Arrays.asList(split);
    }

    private enum ParserStates { TEXT, DEBATE, DECISION, TITLE};

    public static class Token {

        final String text;
        final TokenType type;

        public enum TokenType {TEXT, ITEM, DECISION, DISCUSSION}

        private static Optional<Token> m(Pattern p, String str, TokenType type){
            Matcher matcher = p.matcher(str);
            if (matcher.matches()){
                return Optional.of(new Token(type,matcher.group(0)));
            } else {
                return Optional.empty();
            }
        }

        Token(TokenType type, String input){
            this.text = input;
            this.type = type;
        }

        static Token parse(String s){
            return   m(PATTERN_DEBATE_ITEMS, s, TokenType.ITEM)
                    .orElse(m(PATTERN_DECISION_MARKER, s, TokenType.DECISION)
                            .orElse(m(PATTERN_DEBATE_MARKER, s, TokenType.DISCUSSION).orElse(new Token(TokenType.TEXT, s))));
        }
    }

    public Map<String, MeetingItem> splitDebateItems(String str){
        Map<String,MeetingItem> map = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new StringReader(str))){
            String line;
            String currentTopic = "";
            String currentDiscussion = null;
            String currentDecision = null;
            StringBuffer buffer = new StringBuffer();
            ParserStates currentState = ParserStates.TITLE;
            while ((line = reader.readLine()) != null){
                Token tok = Token.parse(line);
                if (buffer.length() > 0 || !line.isEmpty()) {
                    switch (currentState) {
                        case DECISION: {
                            if (tok.type == Token.TokenType.TEXT) {
                                buffer.append(line + "\n");
                            } else {
                                currentDecision = buffer.toString();

                                if (tok.type == Token.TokenType.DECISION) {
                                    currentState = ParserStates.DECISION;
                                } else if (tok.type == Token.TokenType.ITEM) {
                                    currentState = ParserStates.TITLE;
                                    map.put(currentTopic, new MeetingItem(currentTopic, currentDiscussion, currentDecision, currentDecision== null ? null :  currentDecision.contains("A l'unanimité,")));
                                    currentDecision = null;
                                    currentDiscussion = null;
                                } else if (tok.type == Token.TokenType.DISCUSSION) {
                                    currentState = ParserStates.DEBATE;
                                }
                                buffer = new StringBuffer();
                                buffer.append(tok.text);
                            }
                            break;
                        }
                        case DEBATE: {
                            if (tok.type == Token.TokenType.TEXT || tok.type == Token.TokenType.DISCUSSION) {
                                buffer.append(line + "\n");
                            } else {
                                currentDiscussion = buffer.toString();

                                if (tok.type == Token.TokenType.DECISION) {
                                    currentState = ParserStates.DECISION;
                                } else if (tok.type == Token.TokenType.ITEM) {
                                    currentState = ParserStates.TITLE;
                                    map.put(currentTopic, new MeetingItem(currentTopic, currentDiscussion, currentDecision, currentDecision== null ? null :  currentDecision.contains("A l'unanimité,")));
                                    currentDecision = null;
                                    currentDiscussion = null;
                                }
                                buffer = new StringBuffer();
                                buffer.append(tok.text);
                            }
                            break;
                        }
                        case TITLE: {
                            if (tok.type == Token.TokenType.TEXT || tok.type == Token.TokenType.ITEM) {
                                buffer.append(line + "\n");
                            } else {
                                currentTopic = buffer.toString();

                                if (tok.type == Token.TokenType.DECISION) {
                                    currentState = ParserStates.DECISION;
                                }  else if (tok.type == Token.TokenType.DISCUSSION) {
                                    currentState = ParserStates.DEBATE;
                                }
                                buffer = new StringBuffer();
                                buffer.append(tok.text);
                            }
                            break;
                        }
                    }

                }
            }
            switch (currentState) {
                case TITLE: {
                    currentTopic = buffer.toString();
                    break;
                }
                case DECISION: {
                    currentDecision = buffer.toString();
                    break;
                }
                case DEBATE: {
                    currentDiscussion = buffer.toString();
                    break;
                }
            }
            map.put(currentTopic, new MeetingItem(currentTopic, currentDiscussion, currentDecision, currentDecision== null ? null :  currentDecision.contains("A l'unanimité,")));
            return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String,Integer> MONTHS_FR;
    static {
        MONTHS_FR = new HashMap<>(14);
        MONTHS_FR.put("JANVIER",1);
        MONTHS_FR.put("FÉVRIER",2);
        MONTHS_FR.put("FEVRIER",2);
        MONTHS_FR.put("MARS",3);
        MONTHS_FR.put("AVRIL",4);
        MONTHS_FR.put("MAI",5);
        MONTHS_FR.put("JUIN",6);
        MONTHS_FR.put("JUILLET",7);
        MONTHS_FR.put("AOUT",8);
        MONTHS_FR.put("AOÛT",8);
        MONTHS_FR.put("SEPTEMBRE",9);
        MONTHS_FR.put("OCTOBRE",10);
        MONTHS_FR.put("NOVEMBRE",11);
        MONTHS_FR.put("DÉCEMBRE",12);
        MONTHS_FR.put("DECEMBRE",12);
    }
    private static final Pattern MEETING_TITLE_PATTERN = Pattern.compile("CONSEIL COMMUNAL DU \\w+ ([0-9]?[0-9]) ("+MONTHS_FR.keySet().stream().collect(Collectors.joining("|"))+") ([0-9]{4})", Pattern.DOTALL);


    Meeting createMeeting(String text){
        for (String line : text.split("\\n")) {
            Matcher matcher = MEETING_TITLE_PATTERN.matcher(line.trim());
            if (matcher.matches()) {
                String title = matcher.group(0);
                Integer day = Integer.parseInt(matcher.group(1));
                Integer month = MONTHS_FR.get(matcher.group(2));
                Integer year = Integer.parseInt(matcher.group(3));

                return new Meeting(title, LocalDate.of(year, month, day));
            }
        }
        throw new IllegalArgumentException("No title could be found in text");
    }
    private static Pattern PERSON_PREFIX_TO_CLEAN = Pattern.compile("M\\.|Mme||MM.|MMes|Monsieur|Madame");
    private static String[] PERSON_PREFIXES_CERTAIN =new String[]{"MM.","Mmes","Mme"};

    List<Person> extractAttendees(String text){
        int start = text.indexOf(ATTENDEES_BLOCK_START);
        int end   = text.toUpperCase().indexOf("ORDRE DU JOUR");

        if (start <0 || end <0){
            throw new IllegalArgumentException("No valid attendee block found");
        }

        List<Person> persons = new ArrayList<>();
        String paragraph = text.substring(start+ATTENDEES_BLOCK_START.length(),end).trim().replaceAll("\\n",",");
        for (String token : paragraph.split(",|( et )")){
            token = token.trim();
            for (String prefix : PERSON_PREFIXES_CERTAIN){
                if (token.startsWith(prefix)){
                    token = token.substring(prefix.length(), token.length());
                    break;
                }
            }

            //assuming that there is always one initial
            int indexFirstDot = token.indexOf(".");
            int indexLastDot  = token.lastIndexOf(".");
            if (indexFirstDot < indexLastDot){
                token = token.substring(indexFirstDot+1,token.length());
            }
            token = token.trim();
            if (token.isEmpty()){

            } else if (token.toUpperCase().equals(token)){

                //it's a name
                persons.add(new Person(token));
                if (token.startsWith("MD.")){
                    LOG.debug("Problew with {}", token);
                }
            } else {
                //it's something else, like a title
            }
        }
        return persons;
    }

    @Override
    public Meeting parse(String meetingText) {
        int index = meetingText.indexOf("CONSEIL COMMUNAL DU");
        if (index < 0) {
            throw new IllegalArgumentException("Unable to find start of meeting minutes");
        } else if (index > 0){
            LOG.info("Skipping the first {} characters from the minute (fast forward to title)",index);
        }
        String fromTitle = meetingText.substring(index, meetingText.length());
        List<String> split = extractAgendaItems(fromTitle);
        if (split.size() != 2){
            throw new IllegalArgumentException("Unable to split the input text into foreword and debate items");
        }
        Meeting meeting = createMeeting(split.get(0));
        meeting = meeting.addAll(splitDebateItems(split.get(1)).values());
        meeting = meeting.addAttendees(extractAttendees(split.get(0)));
        return meeting;
    }
}

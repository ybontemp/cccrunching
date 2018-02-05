package org.cccrunching.parser;

import org.cccrunching.data.Meeting;

public interface MeetingParser {

    Meeting parse(String meetingText);

}

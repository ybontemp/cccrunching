package org.cccrunching.data;

import java.time.LocalDate;
import java.util.*;

public class Meeting {

    private final List<MeetingItem> items = new ArrayList<>();
    private final String title;
    private final LocalDate meetingDate;
    private final String id;
    private final List<Person> attendees = new ArrayList<>();


    public Meeting(String title){
        this(title,null);
    }

    public Meeting(String title, LocalDate meetingDate){
        this(UUID.randomUUID().toString(), title, meetingDate);
    }

    private Meeting(String id, String title, LocalDate meetingDate){
        if (title == null) throw new IllegalArgumentException("Meeting title may not be null");
        this.title = title;
        this.meetingDate = meetingDate;
        this.id = id;
    }

    public Meeting add(MeetingItem item){
        return addAll(Collections.singletonList(item));
    }

    public Meeting addAll(Collection<MeetingItem> item){
        Meeting that = new Meeting(title,meetingDate);
        that.items.addAll(this.items);
        that.items.addAll(item);
        that.attendees.addAll(this.attendees);
        return that;
    }

    public Meeting addAttendee(Person attendee){
        return addAttendees(Collections.singletonList(attendee));
    }

    public Meeting addAttendees(Collection<Person> attendees){
        Meeting that = new Meeting(title,meetingDate);
        that.items.addAll(this.items);
        that.attendees.addAll(this.attendees);
        that.attendees.addAll(attendees);
        return that;
    }

    public List<MeetingItem> getItems() {
        return items;
    }

    public String getTitle() {
        return title;
    }

    public Optional<LocalDate> getMeetingDate() {
        return Optional.ofNullable(meetingDate);
    }

    public String getId() {
        return id;
    }

    public List<Person> getAttendees() {
        return attendees;
    }
}

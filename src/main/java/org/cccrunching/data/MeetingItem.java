package org.cccrunching.data;


import java.util.Objects;
import java.util.Optional;

public class MeetingItem {

    private final String discussion;
    private final String title;
    private final String decision;

    private Boolean unanimousVote;

    public MeetingItem(String title){
        this(title, null, null,null);
    }

    public MeetingItem(String title, String decision){
        this(title, null, decision, null);
    }

    public MeetingItem(String title, String discussion, String decision, Boolean unanimousVote){
        this.title = title;
        this.discussion = discussion;
        this.decision = decision;
        this.unanimousVote = unanimousVote;
    }

    public String getTitle() {
        return title;
    }

    public String getDiscussion() {
        return discussion;
    }

    public String getDecision() {
        return decision;
    }

    public Optional<Boolean> isUnanimousVote(){
        return Optional.ofNullable(unanimousVote);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeetingItem that = (MeetingItem) o;
        return Objects.equals(discussion, that.discussion) &&
                Objects.equals(title, that.title) &&
                Objects.equals(decision, that.decision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(discussion, title, decision);
    }

    @Override
    public String toString() {
        return "MeetingItem{" +
                "discussion='" + discussion + '\'' +
                ", title='" + title + '\'' +
                ", decision='" + decision + '\'' +
                '}';
    }
}

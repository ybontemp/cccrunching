package org.cccrunching.search;

import org.cccrunching.data.Meeting;

import java.util.Collection;

public interface Indexer extends AutoCloseable{

    void index(Collection<Meeting> meetings);

    void close();
}

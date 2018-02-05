package org.cccrunching.search;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import org.cccrunching.data.Meeting;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;

public class ESIndexer implements Indexer, AutoCloseable{

    private static final Logger LOG = LoggerFactory.getLogger(ESIndexer.class);

    private final String host = "localhost";
    private final TransportClient client;

    public ESIndexer(){
        try {
            client = new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(host), 9300));
        }catch (UnknownHostException e) {
            throw new RuntimeException("Unable to connect to elasticsearch on "+host, e);
        }
    }

    public void close(){
        this.client.close();
    }

    private void initializeMapping(){
        try {
            String mapping = IOUtils.toString(this.getClass().getResourceAsStream("/search/minute-mapping.json"),Charsets.UTF_8);
            client.admin().indices().prepareCreate("citycouncil").addMapping("meetingminute", mapping);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read mapping definition file",e);
        }
    }

    @Override
    public void index(Collection<Meeting> meetings) {
        LOG.info("Indexing {} meetings", meetings.size());
            Gson gson = new Gson();
            initializeMapping();
            meetings.forEach( m ->
                client.prepareIndex("citycouncil", "meetingminute", m.getId())
                        .setSource(
                                gson.toJson(m).getBytes(Charsets.UTF_8),
                                XContentType.JSON
                        )
                        .get()
        );
    }
}

package com.westorres9.springelastic.services;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class ElasticsearchService {

    @Autowired
    private TransportClient transportClient;

    public IndexResponse createDocument(String index, String type, String id, Map<String, Object> document) {
        return transportClient.prepareIndex(index, type, id)
                .setSource(document).get();
    }

    public GetResponse getDocument(String index, String type, String id) {
        return transportClient.prepareGet(index, type, id).get();
    }

    public UpdateResponse updateDocument(String index, String type, String id, Map<String, Object> updateFields) throws IOException {
        XContentBuilder contentBuilder = XContentFactory.jsonBuilder().startObject();
        for (Map.Entry<String, Object> entry : updateFields.entrySet()) {
            contentBuilder.field(entry.getKey(), entry.getValue());
        }
        contentBuilder.endObject();

        return transportClient.prepareUpdate(index, type, id).setDoc(contentBuilder).get();
    }

    public DeleteResponse deleteDocument(String index, String type, String id) {
        return transportClient.prepareDelete(index, type, id).get();
    }

    public void insertCsvData(InputStream inputStream, String index,String type, Map<String, String> collumnMappings) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){
            String line;
            BulkRequest bulkRequest = new BulkRequest();

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                if (fields.length >= collumnMappings.size()) {
                    Map<String, Object> document = new HashMap<>();

                    for(int i=0; i< fields.length; i++) {
                        if(i< collumnMappings.size()) {
                            String columnName = collumnMappings.keySet().toArray()[i].toString().trim();
                            String columnValue = fields[i].trim();
                            document.put(columnName, columnValue);
                        }
                    }
                    IndexRequest indexRequest = new IndexRequest(index, type).source(document, XContentType.JSON);
                    bulkRequest.add(indexRequest);
                }
            }
            transportClient.bulk(bulkRequest).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

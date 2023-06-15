package com.westorres9.springelastic.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.westorres9.springelastic.dto.*;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.range.DateRangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.InternalDateRange;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static co.elastic.clients.elasticsearch._types.mapping.Property.Kind.DateRange;

@Service
public class EventService {

    @Autowired
    private TransportClient transportClient;

    public EventDTO saveEvent(EventDTO dto) {
        IndexRequest indexRequest = new IndexRequest("events_v3", "_doc")
                .source(eventToJson(dto), XContentType.JSON);
        IndexResponse indexResponse = transportClient.index(indexRequest).actionGet();
        return dto;
    }

    public void saveListOfEvents(List<EventDTO> events) {
        BulkRequest bulkRequest = new BulkRequest();
        for(EventDTO event: events) {
            String json = eventToJson(event);
            IndexRequest indexRequest = new IndexRequest("events_v3", "_doc")
                    .source(json, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulkResponse = transportClient.bulk(bulkRequest).actionGet();
        if(bulkResponse.hasFailures()) {
            System.err.println("Falha ao inserir no elastic");
            System.out.println(bulkResponse.hasFailures());
        } else {
            System.out.println("Eventos inseridos com sucesso");
        }
    }

    public EventDTO getEventById(String eventId) {
        GetRequest getRequest = new GetRequest("events_v2", "_doc", eventId);
        GetResponse getResponse = transportClient.get(getRequest).actionGet();
        if(getResponse.isExists()) {
            String jsonSource = getResponse.getSourceAsString();
            EventDTO event = eventFromJson(jsonSource);
            return event;
        }
        return null;
    }

    public List<EventDTO> getListOfEvents() {
        SearchRequest searchRequest = new SearchRequest("events_v3").types("_doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(100);
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(10));
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = transportClient.search(searchRequest).actionGet();

        List<EventDTO> list = new ArrayList<>();

        for (SearchHit searchHit: searchResponse.getHits()) {
            String source = searchHit.getSourceAsString();
            EventDTO dto = eventFromJson(source);
            if(dto != null) {
                list.add(dto);
            }
        }

        return  list;
    }

    public List<EventAggregation> eventsPerCategoryAndPerUser() {
        List<EventAggregation> eventAggregations = new ArrayList<>();

        SearchRequest searchRequest = new SearchRequest("events_v3");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        AggregationBuilder aggregationBuilder = AggregationBuilders
                .terms("users")
                .field("userName.keyword")
                .size(10)
                .subAggregation(AggregationBuilders
                        .terms("categories")
                        .field("category")
                        .size(10)
                        .subAggregation(AggregationBuilders
                                .sum("sum_duration")
                                .field("duration")
                        ));
        searchSourceBuilder.aggregation(aggregationBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = transportClient.search(searchRequest).actionGet();

        Terms userTerms = searchResponse.getAggregations().get("users");
        for (Terms.Bucket userBucket : userTerms.getBuckets()) {
            String userName = userBucket.getKeyAsString();

            Terms categoryTerms = userBucket.getAggregations().get("categories");
            List<SumPerCategory> sumPerCategoryList = new ArrayList<>();
            for (Terms.Bucket categoryBucket : categoryTerms.getBuckets()) {
                String category = categoryBucket.getKeyAsString();

                Sum sumDuration = categoryBucket.getAggregations().get("sum_duration");
                double duration = sumDuration.getValue();
                SumPerCategory sumPerCategory = new SumPerCategory();
                sumPerCategory.setCategory(category);
                sumPerCategory.setDuration(duration);

                sumPerCategoryList.add(sumPerCategory);
            }
            EventAggregation eventAggregation = new EventAggregation();
            eventAggregation.setUserName(userName);
            eventAggregation.setSumPerCategory(sumPerCategoryList);

            eventAggregations.add(eventAggregation);

        }

        return eventAggregations;
    }

    public List<EventAppAggregation> eventAppAggregations() {
        List<EventAppAggregation> eventAppAggregations = new ArrayList<>();

        SearchRequest searchRequest = new SearchRequest("events_v3");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        AggregationBuilder aggregationBuilder = AggregationBuilders
                .terms("categories")
                .field("category")
                .size(10)
                .subAggregation(AggregationBuilders
                        .terms("apps")
                        .field("app.keyword")
                        .size(10)
                        .subAggregation(AggregationBuilders
                                .sum("sum_duration")
                                .field("duration")
                        ));
        searchSourceBuilder.aggregation(aggregationBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = transportClient.search(searchRequest).actionGet();

        Terms categoryTerms = searchResponse.getAggregations().get("categories");
        for (Terms.Bucket categoryBucket : categoryTerms.getBuckets()) {
            String category = categoryBucket.getKeyAsString();

            Terms appTerms = categoryBucket.getAggregations().get("apps");
            List<SumPerApp> sumPerApps = new ArrayList<>();
            for (Terms.Bucket appBucket : appTerms.getBuckets()) {
                String app = appBucket.getKeyAsString();

                Sum sumDuration = appBucket.getAggregations().get("sum_duration");
                double duration = sumDuration.getValue();
                SumPerApp sumPerApp = new SumPerApp();
                sumPerApp.setApp(app);
                sumPerApp.setDuration(duration);

                sumPerApps.add(sumPerApp);
            }
            EventAppAggregation eventAppAggregation = new EventAppAggregation();
            eventAppAggregation.setCategory(category);
            eventAppAggregation.setSumPerApp(sumPerApps);

            eventAppAggregations.add(eventAppAggregation);

        }

        return eventAppAggregations;

    }

    public List<RangeResult> rangeAggregation() {
        RangeAggregationBuilder aggregation = AggregationBuilders.range("range_aggregation")
                .field("duration")
                .addUnboundedTo(100000)
                .addRange(100000, 500000)
                .addRange(500000, 1000000)
                .addUnboundedFrom(1000000);

        SearchResponse response = transportClient.prepareSearch("events_v3")
                .setQuery(QueryBuilders.matchAllQuery())
                .addAggregation(aggregation)
                .setSize(0)
                .get();

        Range rangeAggregation = response.getAggregations().get("range_aggregation");
        List<RangeResult> results = new ArrayList<>();
        for(Range.Bucket bucket : rangeAggregation.getBuckets()) {
            String key = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();

            RangeResult rangeResult = new RangeResult();
            rangeResult.setRange(key);
            rangeResult.setQuantity(docCount);
            results.add(rangeResult);
        }
        return  results;
    }

    public List<DateAggregationResult> aggregateByDate() {
        SearchRequest searchRequest = new SearchRequest("events_v3");
        searchRequest.source().size(0)
                .query(QueryBuilders.matchAllQuery())
                .aggregation(AggregationBuilders.dateHistogram("date_histogram")
                        .field("startDate").calendarInterval(DateHistogramInterval.DAY)
                        .subAggregation(AggregationBuilders.sum("total_duration").field("duration"))
                        .subAggregation(AggregationBuilders.avg("avg_duration").field("duration")));

        SearchResponse searchResponse = transportClient.search(searchRequest).actionGet();
        Histogram dateHistogram = searchResponse.getAggregations().get("date_histogram");

        List<DateAggregationResult> results = new ArrayList<>();
        for(Histogram.Bucket bucket : dateHistogram.getBuckets()) {
            String date = bucket.getKeyAsString();
            long count = bucket.getDocCount();
            Sum totalDuration = bucket.getAggregations().get("total_duration");
            Avg avgDuration = bucket.getAggregations().get("avg_duration");

            double totalDurationValue = totalDuration.getValue();
            double avgDurationValue = avgDuration.getValue();

            DateAggregationResult dateAggregationResult = new DateAggregationResult();
            dateAggregationResult.setDate(date);
            dateAggregationResult.setCount(count);
            dateAggregationResult.setTotalDuration(totalDurationValue);
            dateAggregationResult.setAvgDuration(avgDurationValue);
            results.add(dateAggregationResult);
        }
        return  results;
    }

    private EventDTO eventFromJson(String jsonSource) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.readValue(jsonSource, EventDTO.class);
        }catch(JsonProcessingException e) {
            throw new RuntimeException("Json processing exception");
        }
    }

    private String eventToJson(EventDTO event) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private long parseDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return instant.getEpochSecond();
    }
}

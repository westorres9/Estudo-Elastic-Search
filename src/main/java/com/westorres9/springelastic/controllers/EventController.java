package com.westorres9.springelastic.controllers;

import com.westorres9.springelastic.dto.*;
import com.westorres9.springelastic.services.EventService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<String> saveEntity(@RequestBody EventDTO dto) {
        try {
            eventService.saveEvent(dto);
            return ResponseEntity.ok().body("Evento salvo com sucesso");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("erro");
        }
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable String eventId) {
        try {
            EventDTO event = eventService.getEventById(eventId);
            if(event != null) {
                return ResponseEntity.ok(event);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/list")
    public ResponseEntity<String> saveEvents(@RequestBody List<EventDTO> events) {
        try {
            eventService.saveListOfEvents(events);
            return ResponseEntity.ok("Eventos salvos com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<EventDTO>> getListOfEvents() {
        List<EventDTO> list = eventService.getListOfEvents();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/aggregations/eventsDurationPerCategoryAndPerUser")
    public ResponseEntity<List<EventAggregation>> getAggregations() {
        List<EventAggregation> list = eventService.eventsPerCategoryAndPerUser();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/aggregations/eventsDurationPerAppAndPerCategory")
    public ResponseEntity<List<EventAppAggregation>> getAppAggregations(
            ) {
        List<EventAppAggregation> list = eventService.eventAppAggregations();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/intervals")
    public ResponseEntity<List<RangeResult>> getResultAggregationRange() {
        List<RangeResult> result = eventService.rangeAggregation();
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/aggregate-by-date")
    public ResponseEntity<List<DateAggregationResult>> getResultAggregationByDate() {
        List<DateAggregationResult> result = eventService.aggregateByDate();
        return ResponseEntity.ok().body(result);
    }
}

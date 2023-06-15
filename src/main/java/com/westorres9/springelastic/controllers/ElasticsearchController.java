package com.westorres9.springelastic.controllers;

import com.westorres9.springelastic.services.ElasticsearchService;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/documents")
public class ElasticsearchController {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @PostMapping("/{index}/{type}/{id}")
    public ResponseEntity<String> createDocument(
            @PathVariable String index,
            @PathVariable String type,
            @PathVariable String id,
            @RequestBody Map<String, Object> document)  {
        IndexResponse response = elasticsearchService.createDocument(index, type, id, document);
        return new ResponseEntity<>(response.getId(), HttpStatus.CREATED);
    }

    @GetMapping("/{index}/{type}/{id}")
    public ResponseEntity<Map<String, Object>> getDocument(
            @PathVariable String index,
            @PathVariable String type,
            @PathVariable String id
    ) {
        GetResponse response = elasticsearchService.getDocument(index, type, id);
        if(response.isExists()) {
            Map<String, Object> source = response.getSourceAsMap();
            return new ResponseEntity<>(source, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/{index}/{type}/{id}")
    public ResponseEntity<String> updateDocument(
            @PathVariable String index,
            @PathVariable String type,
            @PathVariable String id,
            @RequestBody Map<String, Object> updateDocument
    ) throws IOException {
        UpdateResponse response = elasticsearchService.updateDocument(index,type, id, updateDocument);
        return new ResponseEntity<>(response.getId(), HttpStatus.OK);
    }

    @DeleteMapping("/{index}/{type}/{id}")
    public ResponseEntity<String> deleteDocument(
            @PathVariable String index,
            @PathVariable String type,
            @PathVariable String id
    ) {
        DeleteResponse response = elasticsearchService.deleteDocument(index, type, id);
        return new ResponseEntity<>(response.getId(), HttpStatus.OK);
    }

    @PostMapping("/csv/upload")
    public ResponseEntity<String> uploadCsv(
            @RequestParam("file")MultipartFile file,
            @RequestParam("index") String index,
            @RequestParam("type") String type,
            @RequestParam Map<String, String> collumnMappings
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Arquivo esta vazio");
        }
        try {
            elasticsearchService.insertCsvData(file.getInputStream(), index, type, collumnMappings);
            return ResponseEntity.ok("Dados inseridos com sucesso");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("erro ao processar o arquivo");
        }
    }
}

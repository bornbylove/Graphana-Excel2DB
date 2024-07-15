package com.etranzact.graphana.controller;

import com.etranzact.graphana.Entity.Graphana;
import com.etranzact.graphana.dto.GraphanaRequest;
import com.etranzact.graphana.service.GraphanaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api",
        method = RequestMethod.POST,
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@Slf4j
public class GraphanaController {

    @Autowired
    private GraphanaService graphanaService;

//    @Autowired
//    private GraphanaService graphanaService;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file){

        if (file.isEmpty()) {
            return "Failed to upload, file is empty";
        }
        try{
            graphanaService.saveGraphanaData(file.getInputStream());

            return "File uploaded and data saved successfully";

        }catch (Exception e){
            log.error("Failed to upload and save data");

            return "Failed to upload and save data";
        }

    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> updateRecord(@PathVariable Integer id, @RequestBody GraphanaRequest graphanaRequest) {

        if (graphanaRequest == null || graphanaRequest.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update, provided data is empty");
        }

        try {
            graphanaService.updateGraphanaRecord(graphanaRequest);
            return ResponseEntity.ok("Record updated successfully");

        } catch (Exception e) {
            log.error("Failed to update record", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update record");
        }
    }
}

package com.etranzact.graphana.service;

import com.etranzact.graphana.Entity.Graphana;
import com.etranzact.graphana.dto.GraphanaRequest;
import com.etranzact.graphana.repository.GraphanaRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class GraphanaService {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private String comment;
    private Date startDate;
    private Date endDate;
    private String status;
    private String team;
    private String projectName;
    private String projectManager;

    @Autowired
    private GraphanaRepository graphanaRepository;
    private final Path rootLocation = Paths.get("file");

    public GraphanaService(){
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveGraphanaData(InputStream inputStream){

        log.info("Inside Save Graphana Data Method");

        try{
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            List<Graphana> graphanas = new ArrayList<>();

            while (rows.hasNext()){
                Row row = rows.next();

                if (row.getRowNum() == 0){
                    continue;
                }
                try {
                    Graphana graphana = new Graphana();

                    comment  = getCellValueAsString(row.getCell(5));
                    log.info("The element in cell 5" + comment);
                    //Date createdOn = row.getCell(1).getDateCellValue();
                    //log.info("The element in cell 1" + "" + createdOn);
                    startDate = getCellValueAsDate(row.getCell(1));
                    log.info("The element in cell 1" + startDate);
                    endDate = getCellValueAsDate(row.getCell(2));
                    log.info("The element in cell 2" + endDate);
                    status = getCellValueAsString(row.getCell(6));
                    log.info("The element in cell 6" + status);
                    //Long projectId = Long.valueOf(row.getCell(5).getStringCellValue());
                    //log.info("The project ID" + projectId);
                    team = getCellValueAsString(row.getCell(4));
                    log.info("The element in cell 4" + team);
                    projectName = getCellValueAsString(row.getCell(0));
                    log.info("The element in cell 0" + projectName);
                    //String description = row.getCell(8).getStringCellValue();
                    projectManager = getCellValueAsString(row.getCell(3));
                    log.info("The element in cell 3" + projectManager);

                    if(startDate == null || endDate == null){

                        log.error("Start or End date is null");
                       // return;
                    }

                    Timer timer = getTimer(startDate, endDate);

                    graphana.setComment(comment);
                    // graphana.setCreatedOn(createdOn);
                    graphana.setStartDate(startDate);
                    graphana.setEndDate(endDate);
                    //graphana.setProjectId(projectId);
                    graphana.setStatus(status);
                    graphana.setProjectName(projectName);
                    //  graphana.setDescription(description);
                    graphana.setTeam(team);
                    graphana.setProjectManager(projectManager);

                    graphanas.add(graphana);

                } catch (Exception e) {
                    log.error("Error processing row " + row.getRowNum(), e);
                    continue;
                }
                log.info("Graphana List data" + graphanas.toString());

                //graphanaRepository.saveAll(graphanas);
            }
            //graphanaRepository.saveAll(graphanas);
            if (!graphanas.isEmpty()) {
                try {
                    graphanaRepository.saveAll(graphanas);
                    log.info("All records saved successfully");
                } catch (Exception e) {
                    log.error("Error saving Graphana data to repository", e);
                    throw e;
                }
            }

        } catch (IOException e) {
            log.error("Error reading the excel file", e);

        }
    }

    private static Timer getTimer(Date startDate, Date endDate) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Instant now = Instant.now();
                if (now.isBefore(startDate.toInstant())){
                    log.info("Countdown has not started yet");
                } else if (now.isAfter(endDate.toInstant())) {
                    log.info("Countdown finished");
                    timer.cancel();
                }else {
                    Duration remaining = Duration.between(now, endDate.toInstant());
                    log.info("Duration left" + remaining);
                    long hours = remaining.toHours();
                    long minutes = remaining.toMinutes() % 60;
                    long seconds = remaining.getSeconds() % 60;

                    log.info(String.format("Time remaining: %02d:%02d:%02d", hours, minutes, seconds));
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
        return timer;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING, _NONE, BLANK:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private Date getCellValueAsDate(Cell cell){
        if (cell == null){
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)){
            return cell.getDateCellValue();

        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return new SimpleDateFormat("MM/dd/yyyy").parse(cell.getStringCellValue());
            } catch (ParseException e) {
                log.error("Error parsing date string: " + cell.getStringCellValue(), e);
                return null;
            }
           // log.info("The String cell value is" + cell.getDateCellValue());
            //return cell.getDateCellValue();
        }
        else {
            log.info("The logged cell" + cell.getCellType());
            log.error("Cell is not of date type");
            return null;
        }
    }

    public void updateGraphanaRecord(GraphanaRequest graphanaRequest) {

        try {
            Integer id = graphanaRequest.getId();
            Optional<Graphana> optionalGraphana = graphanaRepository.findById(id);

            if (optionalGraphana.isPresent()) {
                Graphana graphana = optionalGraphana.get();

                // Update the fields of the existing Graphana record with data from the updatedGraphana object
                graphana.setComment(graphanaRequest.getComment());
                graphana.setStartDate(graphanaRequest.getStartDate());
                graphana.setEndDate(graphanaRequest.getEndDate());
                graphana.setStatus(graphanaRequest.getStatus());
                graphana.setProjectName(graphanaRequest.getProjectName());
                graphana.setTeam(graphanaRequest.getTeam());
                graphana.setProjectManager(graphanaRequest.getProjectManager());

                // Save the updated Graphana record back to the repository
                graphanaRepository.save(graphana);
            } else {
                log.warn("Graphana record with ID " + id + " not found.");
            }

        } catch (Exception e) {
            log.error("Error updating the Graphana record with ID " + graphanaRequest.getId(), e);
        }
    }
}

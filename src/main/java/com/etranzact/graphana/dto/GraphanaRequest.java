package com.etranzact.graphana.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
@Data
@RequiredArgsConstructor
@AllArgsConstructor

public class GraphanaRequest {
    private Integer id;
    private String comment;
    private Date startDate;
    private Date endDate;
    private String status;
    private String projectName;
    private String team;
    private String projectManager;

}

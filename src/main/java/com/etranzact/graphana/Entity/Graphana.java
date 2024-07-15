package com.etranzact.graphana.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Graphana {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;

   // private Date createdOn;
    //private Long projectId;
    private String projectName;
    //private String description;
    private Date startDate;
    private Date endDate;
    private String status;
    private String comment;
    private String team;
    private String projectManager;
}

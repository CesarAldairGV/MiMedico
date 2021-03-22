package com.example.mimedico.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SymptomsPetition {
    private String id;
    private String userId;
    private String title;
    private String description;
    private String petitionDate;
    private String medicId;
    private boolean petitionAccepted;
    private boolean hasImage;
}

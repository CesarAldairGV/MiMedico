package com.example.mimedico.model;

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
    private User user;
    private String medicId;
    private String title;
    private String description;
    private String petitionDate;
    private String imageUri;
    private boolean petitionAccepted;
    private boolean image;
}

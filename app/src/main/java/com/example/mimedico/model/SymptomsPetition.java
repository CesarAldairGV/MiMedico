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
    private String userEmail;
    private String description;
    private Date petitionDate;
    private boolean petitionAccepted;
}

package com.example.mimedico.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Consult {
    private String id;
    private User user;
    private User medic;
    private SymptomsPetition symptomsPetition;
    private String date;
}

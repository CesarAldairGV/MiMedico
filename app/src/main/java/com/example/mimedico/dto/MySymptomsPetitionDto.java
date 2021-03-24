package com.example.mimedico.dto;

import android.net.Uri;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MySymptomsPetitionDto implements Comparable<MySymptomsPetitionDto>{
    private String id;
    private String userId;
    private String title;
    private String description;
    private String petitionDate;
    private String medicId;
    private Uri imageUri;
    private boolean petitionAccepted;
    private boolean hasImage;

    @Override
    public int compareTo(MySymptomsPetitionDto o) {
        Date date1 = new Date(this.petitionDate);
        Date date2 = new Date(o.petitionDate);
        return date1.compareTo(date2);
    }
}

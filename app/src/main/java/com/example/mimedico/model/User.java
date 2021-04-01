package com.example.mimedico.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;
    private String birthDate;
    private String role;
    private String medicProofUrl;
    private String userPhotoUrl;
    private String institution;
    private String yearsOfExperience;
    private boolean medicSignUpPetition;
}

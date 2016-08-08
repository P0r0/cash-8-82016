package com.example;

import com.google.gson.annotations.*;

public class Name {

    @SerializedName("first")
    private String firstName;

    @SerializedName("last")
    private String lastName;


    public Name() {
    }


    public Name(String firstName,
                String lastName) {
        this.firstName = firstName;
        this.lastName  = lastName;
    }


    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }
}

package com.example;

import com.google.gson.annotations.*;

public class Address {

    @SerializedName("number")
    private int number;

    @SerializedName("street")
    private String street;

    @SerializedName("city")
    private String city;

    @SerializedName("zip")
    private String zipCode;

    @SerializedName("country")
    private String country;


    public Address() {
    }


    public Address(int    number,
                   String street,
                   String city,
                   String zipCode,
                   String country) {
        this.number  = number;
        this.street  = street;
        this.city    = city;
        this.zipCode = zipCode;
        this.country = country;
    }


    public int getNumber() {
        return number;
    }


    public String getStreet() {
        return street;
    }


    public String getCity() {
        return city;
    }


    public String getZipCode() {
        return zipCode;
    }


    public String getCountry() {
        return country;
    }
}

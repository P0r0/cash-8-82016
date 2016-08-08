package com.example;

import com.google.gson.annotations.*;

public class Person {

    @Expose
    private Name    name;

    @Expose
    private Sex     sex;

    @Expose
    private Address address;



    public Person() {
    }


    public Person(Name    name,
                  Sex     sex,
                  Address address) {
        this.name    = name;
        this.sex     = sex;
        this.address = address;
    }


    public Name getName() {
        return name;
    }


    public Sex getSex() {
        return sex;
    }


    public Address getAddress() {
        return address;
    }
}

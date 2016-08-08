package com.example;

import com.google.gson.annotations.*;

public enum Sex {
    @SerializedName("male")   MALE,
    @SerializedName("female") FEMALE;
}

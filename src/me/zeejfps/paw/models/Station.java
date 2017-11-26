package me.zeejfps.paw.models;

import com.google.gson.annotations.SerializedName;

public class Station {

    @SerializedName("stationId")
    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}

package com.example.nckujavafinalproject;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(tableName = "restaurant_table")
public class Restaurant {
    @PrimaryKey
    @NonNull
    private String name;
    @NonNull
    private String labels; // strings separated with "`" ex:  快速`便宜`近

    public Restaurant(@NonNull String name, @NonNull String labels) {
        this.name = name;
        this.labels=labels;
    }

    public String getName(){return this.name;}

    public void setName(String name){this.name=name;}

    public String getLabels(){
        return this.labels;
    }

    public void setLabels(String labels){
        this.labels=labels;
    }
}

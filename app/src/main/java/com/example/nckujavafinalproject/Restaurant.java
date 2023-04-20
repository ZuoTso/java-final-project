package com.example.nckujavafinalproject;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "restaurant_table")
public class Restaurant {
    @PrimaryKey
    @NonNull
    private String name;

    public Restaurant(@NonNull String name) {this.name = name;}

    public String getName(){return this.name;}

    public void setName(String name){this.name=name;}
}

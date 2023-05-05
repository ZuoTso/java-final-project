package com.example.nckujavafinalproject;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(tableName = "restaurant_table")
public class Restaurant implements Parcelable {
    // implements Parcelable so it can be passed between activities
    @PrimaryKey
    @NonNull
    private String name;
    @NonNull
    private String labels; // strings separated with "`" ex:  快速`便宜`近

    public Restaurant(@NonNull String name, @NonNull String labels) {
        this.name = name;
        this.labels=labels;
    }

    protected Restaurant(Parcel in) {
        name = in.readString();
        labels = in.readString();
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public String getName(){return this.name;}

    public void setName(String name){this.name=name;}

    public String getLabels(){
        return this.labels;
    }

    public void setLabels(String labels){
        this.labels=labels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
    }
}

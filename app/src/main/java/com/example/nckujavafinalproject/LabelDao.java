package com.example.nckujavafinalproject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LabelDao {
    // ignore inserting if already exists
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Label label);

    @Query("DELETE from label_table")
    void deleteAll();

    // sorted alphabetically
    @Query("SELECT * FROM label_table ORDER by name ASC")
    LiveData<List<Label>> getAlphaSortedLabel();

    @Delete
    void deleteLabel(Label label);
}

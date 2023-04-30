package com.example.nckujavafinalproject;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class LabelRepository {
    private LabelDao mLabelDao;
    private LiveData<List<Label>> mAllLabels;

    LabelRepository(Application application) {
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        mLabelDao = db.labelDao();
        mAllLabels = mLabelDao.getAlphaSortedLabel();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Label>> getAllLabels() {
        return mAllLabels;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Label label) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> {
            mLabelDao.insert(label);
        });
    }
}
package com.example.nckujavafinalproject;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class LabelViewModel extends AndroidViewModel {

    private LabelRepository mRepository;

    private final LiveData<List<Label>> mAllLabels;

    public LabelViewModel(Application application) {
        super(application);
        mRepository = new LabelRepository(application);
        mAllLabels = mRepository.getAllLabels();
    }

    LiveData<List<Label>> getAllLabels() {
        return mAllLabels;
    }

    public void insert(Label label) {
        mRepository.insert(label);
    }
}
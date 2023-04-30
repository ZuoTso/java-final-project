package com.example.nckujavafinalproject;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.util.List;

public class LabelListAdapter extends ListAdapter<Label, LabelViewHolder> {
    public LabelListAdapter(@NonNull DiffUtil.ItemCallback<Label> diffCallback) {
        super(diffCallback);
    }
    private List<Label>mLabels; // Cached copy of words

    @Override
    public LabelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return LabelViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(LabelViewHolder holder, int position) {
        Label current = getItem(position);
        holder.bind(current.getName());
    }

    static class LabelDiff extends DiffUtil.ItemCallback<Label> {

        @Override
        public boolean areItemsTheSame(@NonNull Label oldItem, @NonNull Label newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Label oldItem, @NonNull Label newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    }

    void setLabels(List<Label> labels) {
        mLabels = labels;
        notifyDataSetChanged();
    }

    // swipe to delete label
     public Label getLabelAtPosition (int position) {
        Label label=mLabels.get(position);
        return mLabels.get(position);
    }
}
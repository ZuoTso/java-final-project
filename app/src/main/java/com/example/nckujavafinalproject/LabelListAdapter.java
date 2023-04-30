package com.example.nckujavafinalproject;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class LabelListAdapter extends ListAdapter<Label, LabelViewHolder> {
    public LabelListAdapter(@NonNull DiffUtil.ItemCallback<Label> diffCallback) {
        super(diffCallback);
    }
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
}
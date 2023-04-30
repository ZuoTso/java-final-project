package com.example.nckujavafinalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class LabelViewHolder extends RecyclerView.ViewHolder {
    private final TextView labelItemView;
    private LabelViewHolder(View itemView) {
        super(itemView);
        labelItemView = itemView.findViewById(R.id.textView);
    }

    public void bind(String text) {
        labelItemView.setText(text);
    }

    static LabelViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new LabelViewHolder(view);
    }
}
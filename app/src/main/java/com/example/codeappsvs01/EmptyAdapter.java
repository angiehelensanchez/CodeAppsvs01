package com.example.codeappsvs01;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.rxjava3.annotations.NonNull;

public class EmptyAdapter extends RecyclerView.Adapter<EmptyAdapter.EmptyViewHolder> {

    @NonNull
    @Override
    public EmptyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty, parent, false);
        return new EmptyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmptyViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 1; // Mostrar solo un elemento
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

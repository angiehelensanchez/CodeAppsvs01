package com.example.codeappsvs01;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private List<PlayerResult> rankingList;

    public RankingAdapter(List<PlayerResult> rankingList) {
        this.rankingList = rankingList;
    }

    @Override
    public RankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_item, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RankingViewHolder holder, int position) {
        PlayerResult result = rankingList.get(position);
        holder.playerNameTextView.setText(result.getPlayerName());
        holder.playerDateTextView.setText(String.valueOf(result.getDate()));
        holder.playerResultTextView.setText(String.valueOf(result.getResult()));
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }

    static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView playerNameTextView;
        TextView playerResultTextView;
        TextView playerDateTextView;

        RankingViewHolder(View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.playerNameTextView);
            playerDateTextView = itemView.findViewById(R.id.playerDateTextView);
            playerResultTextView = itemView.findViewById(R.id.playerResultTextView);
        }
    }
}

package com.pxr.golf.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pxr.golf.R;
import com.pxr.golf.models.Hole;

import java.util.List;

public class HoleAdapter extends RecyclerView.Adapter<HoleAdapter.HoleViewHolder> {
    private final List<Hole> holes;

    public HoleAdapter(List<Hole> holes) {
        this.holes = holes;
    }

    @NonNull
    @Override
    public HoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View item = inflater.inflate(R.layout.hole_item, parent, false);
        return new HoleViewHolder(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HoleViewHolder holder, int position) {
        Hole hole = holes.get(position);
        holder.holeNumber.setText("HOLE " + hole.getNumber());
        holder.holePar.setText("PAR " + hole.getPar());
        holder.holeScore.setText(String.valueOf(hole.getScore()));
        holder.holeNote.setText(hole.getNote());
        holder.holeScore.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                int score = Integer.parseInt(holder.holeScore.getText().toString());
                hole.setScore(score);
                holes.set(position, hole);
            }
        });
        holder.holeNote.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hole.setNote(holder.holeNote.getText().toString());
                holes.set(position, hole);
            }
        });
    }

    @Override
    public int getItemCount() {
        return holes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<Hole> getHoles() {
        return holes;
    }

    public static class HoleViewHolder extends RecyclerView.ViewHolder {
        TextView holeNumber, holePar;
        EditText holeScore, holeNote;

        public HoleViewHolder(View itemView) {
            super(itemView);
            holeNumber = itemView.findViewById(R.id.holeItemNumberText);
            holePar = itemView.findViewById(R.id.homeItemParText);
            holeScore = itemView.findViewById(R.id.holeItemScoreInput);
            holeNote = itemView.findViewById(R.id.holeItemNoteInput);
        }
    }
}

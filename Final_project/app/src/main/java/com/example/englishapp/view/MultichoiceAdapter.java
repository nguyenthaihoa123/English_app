package com.example.englishapp.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.folderTopic;

import java.util.ArrayList;
import java.util.List;

public class MultichoiceAdapter extends RecyclerView.Adapter<MultichoiceAdapter.MultichoiceViewHolder> {

    private List<String> choicesList; // List of choices
    private SparseBooleanArray selectedItems; // To store selected items
    private Context context;

    public MultichoiceAdapter(Context context, List<String> choicesList) {
        this.context = context;
        this.choicesList = choicesList;
        selectedItems = new SparseBooleanArray();
    }
    public class MultichoiceViewHolder extends RecyclerView.ViewHolder{
        TextView txt_choice;
        LinearLayout answer;

        public MultichoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_choice = itemView.findViewById(R.id.txt_choice);
            answer = itemView.findViewById(R.id.answer);
        }
    }

    @NonNull
    @Override
    public MultichoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choice_item, parent, false); // Replace with your item layout XML
        return new MultichoiceViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MultichoiceViewHolder holder, int position) {
        String choice = choicesList.get(position);
        holder.txt_choice.setText(choice);
//        holder.answer.setClickable(selectedItems.get(position));
        holder.answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = ContextCompat.getDrawable(view.getContext(), R.drawable.border_stroke_tn_clicked);
                view.setBackground(drawable);
//                Toast.makeText(view.getContext(), "Thành công"+drawable, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return choicesList.size();
    }

//    public void AnswerOnClick()
//    {
//
//    }
    // Toggle selection
    private void toggleSelection(int position) {
        boolean isSelected = selectedItems.get(position, false);
        selectedItems.put(position, !isSelected);
    }

    // Get selected items count
    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    // Get selected items
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            if (selectedItems.valueAt(i)) {
                items.add(selectedItems.keyAt(i));
            }
        }
        return items;
    }
}




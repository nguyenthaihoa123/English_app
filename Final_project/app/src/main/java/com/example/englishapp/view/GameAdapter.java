package com.example.englishapp.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.TracNghiem;
import com.example.englishapp.flashcard;
import com.example.englishapp.model.Topic;
import com.example.englishapp.model.Vocab;
import com.example.englishapp.topic_details;
import com.example.englishapp.word_fill;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder>  {
    private Context context;
    private ArrayList<Topic> topicList;
    public GameAdapter(Context context,ArrayList<Topic> topicList) {
        this.context = context;
        this.topicList = topicList;
    }
    @androidx.annotation.NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_topic_list, parent, false); // Replace with your item layout XML
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull GameViewHolder holder, int position) {
        Topic topic =topicList.get(position);
        if(topic==null)
        {
            return;
        }
        holder.txt_name.setText(topic.getName());
        holder.txt_quantity.setVisibility(View.GONE);
        loadAndDisplayImageFromFirebaseStorage(topic.getImage(),holder.img_topic);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(topic.getGame().equals("quiz"))
                {
                    OnClickGoToQuiz(topic);
                } else if (topic.getGame().equals("flashCard")) {
                    OnClickGoToFlashCard(topic);
                } else if (topic.getGame().equals("fillWord")) {
                    OnClickGoToFillWord(topic);
                }else {

                }

            }
        });
    }
    private void OnClickGoToQuiz(Topic topic)
    {
        Intent intent = new Intent(context, TracNghiem.class);

        Bundle bundle = new Bundle();

        bundle.putString("idTopic",topic.getId());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
    private void OnClickGoToFlashCard(Topic topic)
    {
        List<Vocab> vocabList = topic.getVocabList();
        String jsonVocabList = new Gson().toJson(vocabList);
        // Tạo Intent để mở hoạt động FlashcardActivity
        Intent intent = new Intent(context, flashcard.class);
        intent.putExtra("topicID",topic.getId());
        intent.putExtra("idUser",topic.getIdOwner());
        // Đưa chuỗi JSON vào Intent
        intent.putExtra("jsonVocabList", jsonVocabList);

        // Khởi động hoạt động FlashcardActivity
        context.startActivity(intent);
    }
    private void OnClickGoToFillWord(Topic topic)
    {
        List<Vocab> vocabList = topic.getVocabList();
        String jsonVocabList = new Gson().toJson(vocabList);
        // Tạo Intent để mở hoạt động FlashcardActivity
        Intent intent = new Intent(context, word_fill.class);
        intent.putExtra("topicID",topic.getId());
        intent.putExtra("idUser",topic.getIdOwner());
        // Đưa chuỗi JSON vào Intent
        intent.putExtra("jsonVocabList", jsonVocabList);
        // Khởi động hoạt động FlashcardActivity
        context.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public class GameViewHolder extends RecyclerView.ViewHolder{
        TextView txt_name;
        TextView txt_quantity;
        ImageView img_topic;
        CardView cardView;
        public GameViewHolder(@NonNull View itemview)
        {
            super(itemview);
            txt_name = itemview.findViewById(R.id.txt_name_topic);
            txt_quantity= itemview.findViewById(R.id.txt_quantity_vocab);
            img_topic =itemview.findViewById(R.id.img_topic);
            cardView = itemview.findViewById(R.id.item_topic);
        }
    }
    private void loadAndDisplayImageFromFirebaseStorage(String storagePath, ImageView imageView) {
        // Lấy một tham chiếu đến Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(storagePath);

        // Lấy URL download và hiển thị hình ảnh vào ImageView sử dụng Picasso
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Xử lý lỗi nếu cần
                exception.printStackTrace();
            }
        });
    }
    public void filterList(ArrayList<Topic> topicList1) {
        topicList = topicList1;
        notifyDataSetChanged();
    }
}

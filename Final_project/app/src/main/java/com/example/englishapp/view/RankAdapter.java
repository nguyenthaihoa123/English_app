package com.example.englishapp.view;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.model.Topic;
import com.example.englishapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankViewHolder> {
    private Context context;
    private ArrayList<User> userList;
    public RankAdapter(Context context,ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
    }
    @androidx.annotation.NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_item, parent, false); // Replace with your item layout XML
        return new RankAdapter.RankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull RankViewHolder holder, int position) {
        User user = userList.get(position);
        if(user==null)
        {
            return;
        }
        holder.txt_name.setText(user.getUsername());
        holder.txt_number.setText(String.valueOf(user.getStt()));
        holder.score.setText(String.valueOf(user.getScore()));
        loadAndDisplayImageFromFirebaseStorage(user.getImg(),holder.img_avatar);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class RankViewHolder extends RecyclerView.ViewHolder{
        TextView txt_name;
        TextView txt_number;
        ImageView img_avatar;
        TextView score;
        public RankViewHolder(@NonNull View itemview)
        {
            super(itemview);
            txt_name = itemview.findViewById(R.id.tvName);
            txt_number = itemview.findViewById(R.id.tvNumber);
            img_avatar = itemview.findViewById(R.id.img_avatar);
            score = itemview.findViewById(R.id.tvScore);

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
}

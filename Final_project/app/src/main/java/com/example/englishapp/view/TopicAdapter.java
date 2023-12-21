package com.example.englishapp.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.model.Folder;
import com.example.englishapp.model.Topic;
import com.example.englishapp.topicList;
import com.example.englishapp.viewmodel.TopicVM;
import com.example.englishapp.viewmodel.VocabVM;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {
    private ArrayList<Topic> topicList;
    private Context context;
    private String idFolder, idUser;

    public TopicAdapter(Context context, ArrayList<Topic> topicList,String idFolder,String idUser) {
        this.context = context;
        this.topicList = topicList;
        this.idFolder = idFolder;
        this.idUser = idUser;
    }
    @androidx.annotation.NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_topic_list, parent, false); // Replace with your item layout XML
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull TopicViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Topic topic =topicList.get(position);
        if(topic==null)
        {
            return;
        }
        holder.txt_name.setText(topic.getName());
        holder.txt_quantity.setText(topic.getCount() + " Vocab");
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickGoToTopic(topic);
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xác nhận xóa topic");
                builder.setMessage("Bạn có chắc muốn xóa topic ra khỏi folder này?");

                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Thực hiện xóa từ vựng
                        TopicVM deleteVM = new TopicVM();
                        deleteVM.deleteTopicInFolder(idUser,idFolder,topic.getId());
                        if (onTopicDeleteListener != null) {
                            onTopicDeleteListener.onTopicDeleted(topic.getId());
                        }
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
        loadAndDisplayImageFromFirebaseStorage(topic.getImage(),holder.img_topic);

    }
    private void OnClickGoToTopic(Topic topic)
    {
        Intent intent = new Intent(context, com.example.englishapp.topic_details.class);

        Bundle bundle = new Bundle();

//        if (folder != null && folder.getId_topic() != null && !folder.getId_topic().isEmpty()) {
//            bundle.putStringArrayList("id_Topic", new ArrayList<>(folder.getId_topic()));
//        } else {
//            bundle.putStringArrayList("id_Topic", new ArrayList<>());
//        }
        bundle.putString("idOwner", topic.getIdOwner());
        bundle.putString("idTopic",topic.getId());
        bundle.putBoolean("access",topic.isAccess());
        intent.putExtras(bundle);

        context.startActivity(intent);
    }
    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder{
        TextView txt_name;
        TextView txt_quantity;
        ImageView img_topic;
        CardView cardView;
        public TopicViewHolder(@NonNull View itemview)
        {
            super(itemview);
            txt_name = itemview.findViewById(R.id.txt_name_topic);
            txt_quantity= itemview.findViewById(R.id.txt_quantity_vocab);
            img_topic =itemview.findViewById(R.id.img_topic);
            cardView = itemview.findViewById(R.id.item_topic);
        }
    }
    public void filterList(ArrayList<Topic> topicList1) {
        topicList = topicList1;
        notifyDataSetChanged();
    }
//===============================================================================
    public interface OnTopicDeleteListener {
        void onTopicDeleted(String idTopic);
    }
    private OnTopicDeleteListener onTopicDeleteListener;

    public void setOnTopicDeleteListener(OnTopicDeleteListener listener) {
        this.onTopicDeleteListener = listener;
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

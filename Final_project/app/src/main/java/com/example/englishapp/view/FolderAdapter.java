package com.example.englishapp.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.R;
import com.example.englishapp.model.Folder;
import com.example.englishapp.topicList;
import com.example.englishapp.viewmodel.FolderVM;
import com.example.englishapp.viewmodel.VocabVM;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private List<Folder> folderList;
    private Context context;
    private List<Folder> originalList;
    private String idUser;

    public FolderAdapter(Context context, List<Folder> folderList, String idUser) {
        this.context = context;
        this.folderList = folderList;
        this.originalList = new ArrayList<>(folderList);
        this.idUser = idUser;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_folder_list, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {

        Folder folder = folderList.get(position);
        holder.txtName.setText(folder.getName());
        holder.txtCount.setText(folder.getId_topic().size() + " Topics");

        Date currentTime = new Date();
        Date timestampFromFirestore = folder.getTime();

        long timeDifferenceMillis = currentTime.getTime() - timestampFromFirestore.getTime();
        long timeDifferenceMinutes = timeDifferenceMillis / (60 * 1000); // Chuyển từ mili giây sang phút
        long timeDifferenceHours = timeDifferenceMinutes / 60; // Chuyển từ phút sang giờ

        String lastEditedTime;
        if (timeDifferenceMinutes < 60) {
            lastEditedTime = timeDifferenceMinutes + " minutes";
        } else if (timeDifferenceHours < 24) {
            lastEditedTime = timeDifferenceHours + " hours";
        } else {
            long timeDifferenceDays = timeDifferenceHours / 24;
            lastEditedTime = timeDifferenceDays + " days";
        }

        holder.txtTime.setText(lastEditedTime);

        ///event onclick cardview item
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickGoToTopic(folder);
//                Toast.makeText(context, new ArrayList<>(folder.getId_topic())+"",Toast.LENGTH_SHORT).show();
            }
        });
        holder.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xác nhận xóa folder");
                builder.setMessage("Bạn có chắc muốn xóa folder này?");

                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Thực hiện xóa từ vựng
                        FolderVM deleteVM = new FolderVM();
                        deleteVM.deleteFolder(idUser,folder.getId());
                        // Cập nhật RecyclerView
                        if (onFolderDeleteListener != null) {
                            onFolderDeleteListener.onFolderDeleted(folder.getId());
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
    }

    private void OnClickGoToTopic(Folder folder)
    {
        Intent intent = new Intent(context, topicList.class);

        Bundle bundle = new Bundle();
        if (folder.getId() != null && !folder.getId().isEmpty()) {
            bundle.putString("id_Folder", folder.getId());
        }
        if (folder != null && folder.getId_topic() != null && !folder.getId_topic().isEmpty()) {
            bundle.putStringArrayList("id_Topic", new ArrayList<>(folder.getId_topic()));
        } else {
            bundle.putStringArrayList("id_Topic", new ArrayList<>());
        }

        intent.putExtras(bundle);

        context.startActivity(intent);
    }
    @Override
    public int getItemCount() {
        return folderList.size();
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtCount, txtTime;
        CardView cardview ;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name_folder);
            txtCount = itemView.findViewById(R.id.txt_count_folder);
            txtTime = itemView.findViewById(R.id.txt_time_folder);
            cardview = itemView.findViewById(R.id.cardview_item);
        }
    }
    public void filterList(List<Folder> filteredList) {
        folderList = filteredList;
        notifyDataSetChanged();
    }
    public interface OnFolderDeleteListener {
        void onFolderDeleted(String idFolder);
    }
    private OnFolderDeleteListener onFolderDeleteListener;

    public void setOnFolderDeleteListener(OnFolderDeleteListener listener) {
        this.onFolderDeleteListener = listener;
    }
}

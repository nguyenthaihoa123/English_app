package com.example.englishapp;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.model.Folder;
import com.example.englishapp.model.Topic;
import com.example.englishapp.view.FolderAdapter;
import com.example.englishapp.view.TopicAdapter;
import com.example.englishapp.viewmodel.FolderVM;
import com.example.englishapp.viewmodel.TopicVM;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class topicList extends AppCompatActivity implements TopicAdapter.OnTopicDeleteListener {
    private TopicAdapter adapter ;
    private ArrayList<Topic> topicList =new ArrayList<>();
    private RecyclerView rcv_Topic;
    private ImageButton btn_back_tp;
    FloatingActionButton btn_Add;
    EditText edt_search;
    private String m_Text = "";
    String idUser = "user_2";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_topic);
        rcv_Topic = findViewById(R.id.rcv_topic);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Toast.makeText(this, "Lỗi", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String documentID = sharedPreferences.getString("idUser", "user_2");
        idUser = documentID;

        ArrayList<String> idTopic = bundle.getStringArrayList("id_Topic");
        String id_Folder = bundle.getString("id_Folder");
//        Toast.makeText(this, "ID List:"+idTopic, Toast.LENGTH_SHORT).show();
        if (idTopic == null || idTopic.isEmpty()) {
            // Chuỗi rỗng hoặc null, không cần cập nhật RecyclerView
            Toast.makeText(this, "Chuỗi rỗng hoặc null", Toast.LENGTH_SHORT).show();
        } else {
            // Chuỗi không rỗng, tiến hành cập nhật RecyclerView
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            rcv_Topic.setLayoutManager(gridLayoutManager);
            adapter = new TopicAdapter(this, topicList,id_Folder,idUser);
            adapter.setOnTopicDeleteListener(this);
            rcv_Topic.setAdapter(adapter);

            TopicVM topicVM = new TopicVM();
            topicVM.getTopicsForUser(idTopic, idUser, new TopicVM.OnTopicsFetchedListener() {
                @Override
                public void onTopicFetched(Topic topic) {
                    int existingIndex = findTopicIndexByName(topic.getName());

                    if (existingIndex != -1) {
                        // Nếu đã tồn tại, thay thế phần tử tại vị trí đó
                        topicList.set(existingIndex, topic);
                    } else {
                        // Nếu chưa tồn tại, thêm vào topicList
                        topicList.add(topic);
                    }

                    // Cập nhật giao diện người dùng (nếu cần)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged(); // Gọi notifyDataSetChanged từ adapter
                            // Hiển thị thông báo thành công
//                            Toast.makeText(topicList.this, "Thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFetchError(Exception e) {
                    Toast.makeText(topicList.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        btn_Add = findViewById(R.id.btn_add_topic);
        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(topicList.this);
                dialog.setContentView(R.layout.custom_dialog); // Sử dụng custom layout
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background); // Sử dụng drawable với góc bo tròn

                Button okButton = dialog.findViewById(R.id.ok_button);
                Button cancelButton = dialog.findViewById(R.id.cancel_button);

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Xử lý khi nút OK được nhấn
                        EditText titleEditText = dialog.findViewById(R.id.edt_fill_ttFolder);
                        String title = titleEditText.getText().toString();

                        // Kiểm tra xem title có giá trị không rỗng trước khi thêm
                        if (!title.isEmpty()) {

                            // Gọi hàm thêm Topic
//                            FolderVM folderViewModel = new FolderVM();
                            TopicVM topicVM = new TopicVM();
                            topicVM.addTopicForUser(id_Folder,idUser, title, new TopicVM.OnTopicAddedListener() {

                                @Override
                                public void onTopicAdded(Topic topic) {
//                                    Toast.makeText(topicList.this, "Thêm topic thành công: "+topic.getName(), Toast.LENGTH_SHORT).show();
//                                    Topic topic = new Topic(topicID,title,0,"user_2");
                                    boolean topicExists = false;

                                    // Iterate through existing topics in topicList
                                    for (int i = 0; i < topicList.size(); i++) {
                                        Topic existingTopic = topicList.get(i);

                                        // Check if the current topic has the same ID or other identifying property
                                        if (existingTopic.getId().equals(topic.getId())) {
                                            // Topic already exists, update it
                                            topicList.set(i, topic);
                                            topicExists = true;
                                            break;  // No need to continue checking
                                        }
                                    }

                                    // If the topic doesn't exist, add it to the list
                                    if (!topicExists) {
                                        topicList.add(topic);
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(topicList.this, "Thành them topic công", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onAddError(Exception e) {
                                    Toast.makeText(topicList.this, "Thêm thất bại!!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onQuizzAdded(String quizzID) {
                                    Toast.makeText(topicList.this, "Thêm quizz thanh cong!!", Toast.LENGTH_SHORT).show();
                                }

                            });
                        }
                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Xử lý khi nút Cancel được nhấn
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        btn_back_tp = findViewById(R.id.btn_back_topic);
        btn_back_tp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//====================================Tìm kiếm========================================
        edt_search = findViewById(R.id.edt_searchTopic);
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                m_Text = charSequence.toString();
                // Sau đó, áp dụng bộ lọc dựa trên m_Text
                filter(m_Text);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private int findTopicIndexByName(String topicName) {
        for (int i = 0; i < topicList.size(); i++) {
            Topic existingTopic = topicList.get(i);
            if (existingTopic.getName().equals(topicName)) {
                return i; // Trả về index nếu topic đã tồn tại
            }
        }
        return -1; // Trả về -1 nếu topic chưa tồn tại
    }
    private void filter(String text) {
        // Tạo một danh sách mới để lưu trữ kết quả lọc
        ArrayList<Topic> filter_topicList = new ArrayList<>();

        // Duyệt qua tất cả thư mục trong danh sách gốc
        for (Topic item : topicList) {
            // Kiểm tra xem tên thư mục chứa văn bản tìm kiếm không, không phân biệt hoa thường
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                // Nếu tìm thấy, thêm vào danh sách đã lọc
                filter_topicList.add(item);
            }
        }

        // Kiểm tra xem danh sách đã lọc có dữ liệu hay không
        if (filter_topicList.isEmpty()) {
            // Hiển thị thông báo nếu không có kết quả tìm kiếm
            Toast.makeText(topicList.this, "Không tìm thấy dữ liệu.", Toast.LENGTH_SHORT).show();
        }

        // Cập nhật danh sách trong Adapter với danh sách đã lọc
        adapter.filterList(filter_topicList);
    }
    @Override
    public void onTopicDeleted(String idTopic) {
        // Cập nhật lại RecyclerView hoặc thực hiện các hành động khác với idTopic
        Iterator<Topic> iterator = topicList.iterator();
        while (iterator.hasNext()) {
            Topic topic = iterator.next();
            if (topic.getId().equals(idTopic)) {
                iterator.remove();
                break; // Đã tìm thấy và xóa phần tử, không cần kiểm tra các phần tử khác
            }
        }

        // Cập nhật RecyclerView với danh sách mới
        adapter.notifyDataSetChanged();
    }
}

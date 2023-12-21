package com.example.englishapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.englishapp.model.Folder;
import com.example.englishapp.model.Topic;
import com.example.englishapp.model.Vocab;
import com.example.englishapp.view.FolderAdapter;
import com.example.englishapp.view.TopicAllAdapter;
import com.example.englishapp.viewmodel.FolderVM;
import com.example.englishapp.viewmodel.SwipeTouchListener;
import com.example.englishapp.viewmodel.TopicVM;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class folderTopic extends AppCompatActivity  implements FolderAdapter.OnFolderDeleteListener{
    private List<Folder> folderList = new ArrayList<>();
    private ArrayList<Topic> topicList =new ArrayList<>();

    private FolderAdapter adapter; // Khai báo adapter ở cấp độ lớp
    private TopicAllAdapter adapterTP;
    private RadioGroup toggle;
    private RadioButton folder, topic;
    private FrameLayout layoutOfFolderTopic;
    private String m_Text = "";
    FloatingActionButton btnAdd_folder;
    EditText edt_search;
    String idUser;
    FolderVM folderViewModel;
    Boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folder_topic);
        FirebaseApp.initializeApp(this);

        Intent intent = getIntent();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String documentID = sharedPreferences.getString("idUser", "user_2");
        idUser = documentID;

        RecyclerView recyclerView = findViewById(R.id.rcv_folder);

//===================================================BottomBar========================================
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonnavFolder);
        bottomNavigationView.getMenu().getItem(0).setChecked(false);
        bottomNavigationView.getMenu().getItem(3).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.proPage) {
                    startActivity(new Intent(getApplicationContext(), Profile.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.homePage) {
                    // Thêm các xử lý cho trang chủ nếu cần
                    Intent intent = new Intent(getApplicationContext(), home.class);
                    intent.putExtra("documentId", documentID);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.folderPage) {
                    return true;
                } else if (item.getItemId() == R.id.mywordPage) {
                    // Thêm các xử lý cho trang chủ nếu cần
                    startActivity(new Intent(getApplicationContext(), my_word.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.gamePage) {
                    startActivity(new Intent(getApplicationContext(), gamePlay.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }

                return false;
            }
        });
//========================================Lấy folder==============================================

        folderViewModel = new FolderVM();
        folderViewModel.getFoldersFromFirebase(idUser, new FolderVM.OnFoldersFetchedListener() {
            @Override
            public void onFoldersFetched(Folder folder) {
                if (!isFolderExists(folder)) {
                    // Nếu chưa tồn tại, thêm vào vocabList
                    folderList.add(folder);

//                     Cập nhật giao diện người dùng (nếu cần)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged(); // Gọi notifyDataSetChanged từ adapter

                        }
                    });
                }
            }

            @Override
            public void onFetchError(Exception e) {
                // Handle the fetch error here

            }


        });


        adapter = new FolderAdapter(this, folderList,idUser); // Khởi tạo adapter
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter.setOnFolderDeleteListener(this);
        recyclerView.setAdapter(adapter);


//===============================================Lấy topic==============================================
        TopicVM topicVM = new TopicVM();
        topicVM.getTopicsAllForUser(idUser, new TopicVM.OnTopicsFetchedListener() {
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
                        adapterTP.notifyDataSetChanged(); // Gọi notifyDataSetChanged từ adapter

                    }
                });
            }

            @Override
            public void onFetchError(Exception e) {
                Toast.makeText(folderTopic.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        adapterTP = new TopicAllAdapter(this, idUser, topicList,folderList); // Khởi tạo adapter
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        recyclerView.setAdapter(adapterTP);
//========================================================================================================
        btnAdd_folder = findViewById(R.id.btn_add_folder);
        btnAdd_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag == true){
                    final Dialog dialog = new Dialog(folderTopic.this);
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
                                // Gọi hàm thêm Folder
                                FolderVM folderViewModel = new FolderVM();
                                folderViewModel.addFolderForUser(documentID, title, new FolderVM.OnFolderAddedListener() {
                                    @Override
                                    public void onFolderAdded(Folder folder) {
                                        // Xử lý khi Folder được thêm thành công (nếu cần)
                                        Toast.makeText(folderTopic.this, "Thêm folder thành công: "+folder.getId(), Toast.LENGTH_SHORT).show();
//                                    Folder newFolder = new Folder(folderId, title, new ArrayList<String>(), new Date());
                                        folderList.add(folder);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onAddError(Exception e) {
                                        // Xử lý lỗi khi thêm Folder (nếu cần)
                                        Toast.makeText(folderTopic.this, "Thêm folder thất bại!!", Toast.LENGTH_SHORT).show();
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
                else {
                    final Dialog dialog = new Dialog(folderTopic.this);
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
                                topicVM.addTopicToAllForUser(idUser, title, new TopicVM.OnTopicAddedListener() {

                                    @Override
                                    public void onTopicAdded(Topic topic) {
//                                    Toast.makeText(topicList.this, "Thêm topic thành công: "+topic.getName(), Toast.LENGTH_SHORT).show();
//                                    Topic topic = new Topic(topicID,title,0,"user_2");
                                        topicList.add(topic);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapterTP.notifyDataSetChanged();
                                                Toast.makeText(folderTopic.this, "Thành them topic công", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onAddError(Exception e) {
                                        Toast.makeText(folderTopic.this, "Thêm thất bại!!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onQuizzAdded(String quizzID) {
                                        Toast.makeText(folderTopic.this, "Thêm quizz thanh cong!!", Toast.LENGTH_SHORT).show();
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
            }
        });


        edt_search = findViewById(R.id.edt_search_folder);
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                m_Text = charSequence.toString();
                // Sau đó, áp dụng bộ lọc dựa trên m_Text
                if(flag == true){
                    filter(m_Text);
                }
                else {
                    filterTopic(m_Text);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//======================================================================
        toggle = findViewById(R.id.toggle);
        folder = findViewById(R.id.folder);
        topic = findViewById(R.id.topic);


        toggle.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.folder) {
                flag = true;
                // Chuyển đến nút Search
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            } else if (checkedId == R.id.topic) {
                // Chuyển đến nút Offers
                flag = false;
                recyclerView.setAdapter(adapterTP);
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Hoặc sử dụng layout manager khác cho Offer
            }
        });
//======================================bắt sự kiện vuốt========================

        SwipeTouchListener.OnSwipeListener onSwipeListener = new SwipeTouchListener.OnSwipeListener() {
            @Override
            public void onSwipeLeft() {
                // Xử lý khi vuốt sang trái
                topic.setChecked(true);
                folder.setChecked(false);
                flag = false;
            }

            @Override
            public void onSwipeRight() {
                // Xử lý khi vuốt sang phải
                topic.setChecked(false);
                folder.setChecked(true);
                flag = true;
            }
        };
        recyclerView.addOnItemTouchListener(new SwipeTouchListener(this, onSwipeListener));

    }
    private void animateRadioButton(RadioButton radioButton, float translationX) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(radioButton, "translationX", translationX);
        animator.setDuration(500); // Đặt thời gian animation
        animator.start();
    }
    private void filter(String text) {


        ArrayList<Folder> filteredList = new ArrayList<>();

        // Duyệt qua tất cả thư mục trong danh sách gốc
        for (Folder item : folderList) {
            // Kiểm tra xem tên thư mục chứa văn bản tìm kiếm không, không phân biệt hoa thường
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                // Nếu tìm thấy, thêm vào danh sách đã lọc
                filteredList.add(item);
            }
        }

        // Kiểm tra xem danh sách đã lọc có dữ liệu hay không
        if (filteredList.isEmpty()) {
            // Hiển thị thông báo nếu không có kết quả tìm kiếm
            Toast.makeText(folderTopic.this, "Không tìm thấy dữ liệu.", Toast.LENGTH_SHORT).show();
        }

        // Cập nhật danh sách trong Adapter với danh sách đã lọc
        adapter.filterList(filteredList);
    }
    private void filterTopic(String text) {
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
            Toast.makeText(folderTopic.this, "Không tìm thấy dữ liệu.", Toast.LENGTH_SHORT).show();
        }

        // Cập nhật danh sách trong Adapter với danh sách đã lọc
        adapterTP.filterList(filter_topicList);
    }

    @Override
    public void onFolderDeleted(String idFolder) {
        // Tìm và xóa phần tử trong folderList dựa trên idFolder
        Iterator<Folder> iterator = folderList.iterator();
        while (iterator.hasNext()) {
            Folder folder = iterator.next();
            if (folder.getId().equals(idFolder)) {
                iterator.remove();
                break; // Đã tìm thấy và xóa phần tử, không cần kiểm tra các phần tử khác
            }
        }

        // Cập nhật RecyclerView với danh sách mới
        adapter.notifyDataSetChanged();
    }
    private int findFolderIndexById(String folderId) {
        for (int i = 0; i < folderList.size(); i++) {
            if (folderList.get(i).getId().equals(folderId)) {
                // Nếu folder đã tồn tại, trả về chỉ số của nó trong danh sách
                return i;
            }
        }
        // Nếu folder không tồn tại, trả về -1
        return -1;
    }
    private boolean isFolderExists(Folder folder) {
        for (Folder existingFolder : folderList) {
            // Ở đây, bạn cần xác định cách so sánh giữa các phần tử Vocab, ví dụ, so sánh theo id, word, trans, etc.
            // Dưới đây là một ví dụ sơ bộ kiểm tra theo id
            if (existingFolder.getId().equals(folder.getId())) {
                return true; // vocab đã tồn tại trong vocabList
            }
        }
        return false; // vocab chưa tồn tại trong vocabList
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
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận thoát");
        builder.setMessage("Bạn có chắc chắn muốn thoát ứng dụng?");

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();  // Đóng ứng dụng nếu người dùng chọn "Có"
            }
        });

        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Không làm gì cả nếu người dùng chọn "Không"
                dialog.dismiss();
            }
        });

        builder.show();
    }
}

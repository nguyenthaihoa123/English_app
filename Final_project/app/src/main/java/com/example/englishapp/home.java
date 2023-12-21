package com.example.englishapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.model.Folder;
import com.example.englishapp.model.Topic;
import com.example.englishapp.model.User;
import com.example.englishapp.view.FolderAdapter;
import com.example.englishapp.view.TopicAdapter;
import com.example.englishapp.view.TopicHomeAdapter;
import com.example.englishapp.viewmodel.EditProfileVM;
import com.example.englishapp.viewmodel.FolderVM;
import com.example.englishapp.viewmodel.TopicVM;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {
    RecyclerView rcv_topic ;
    TopicVM topicVM = new TopicVM();
    EditProfileVM editProfileVM = new EditProfileVM();
    List<String> idTopic = new ArrayList<>();
    private ArrayList<Topic> topicList = new ArrayList<>();
    private TopicHomeAdapter adapter; // Khai báo adapter ở cấp độ lớp
    private Button btn_Topic;
    ImageButton btn_flashCard,btn_quiz,btn_fillWord;
    ImageView img_avatar;
    AppCompatButton btn_rank;
    TextView txt_username;
    String idUser,username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupUI();
//        String documentID = getIntent().getStringExtra("documentId");
//        String documentID = idUser;
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("idUser", "user_2");

        adapter = new TopicHomeAdapter(this, topicList); // Khởi tạo adapter
        rcv_topic.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        rcv_topic.setAdapter(adapter);

//============================================================BottomBar===========================================
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonnavHome);
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
                    return true;
                } else if (item.getItemId() == R.id.folderPage) {
//                    startActivity(new Intent(getApplicationContext(), folderTopic.class));
                    Intent intent = new Intent(getApplicationContext(), folderTopic.class);
                    intent.putExtra("documentId", idUser);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.mywordPage) {
                    // Thêm các xử lý cho trang chủ nếu cần
                    startActivity(new Intent(getApplicationContext(), my_word.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }else if (item.getItemId() == R.id.gamePage) {
                    startActivity(new Intent(getApplicationContext(), gamePlay.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }

                return false;
            }
        });
//================================================================================================================
        buttonOnClick(idUser);
        FetchData();
        GetUser();



    }
    private  void FetchData()
    {
        topicVM.getTopicCommunityForHome(idUser, new TopicVM.OnTopicsHomeFetchedListener() {
            @Override
            public void onTopicHomeFetched(Topic topic) {
                topicList.add(topic);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged(); // Gọi notifyDataSetChanged từ adapter
                    }
                });
            }

            @Override
            public void onTopicHomeIDFetched(List<String> idLists) {
                idTopic = idLists;
            }

            @Override
            public void onFetchError(Exception e) {
            }
        });
    }
    private void setupUI()
    {
        btn_Topic = findViewById(R.id.btn_to_topic);
        rcv_topic = findViewById(R.id.rcv_topic_home);
        btn_quiz = findViewById(R.id.btnPlay2);
        btn_flashCard = findViewById(R.id.btnPlay1);
        btn_fillWord = findViewById(R.id.btnPlay3);
        btn_rank = findViewById(R.id.btn_rank);
        txt_username = findViewById(R.id.txt_username);
        img_avatar = findViewById(R.id.img_avatar);
    }
    private void buttonOnClick(String documentID){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        btn_Topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home.this, topicList.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("id_Topic", (ArrayList<String>) idTopic);
                intent.putExtras(bundle);
                startActivity(intent);
//                overridePendingTransition(0, 0);
            }
        });
        btn_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home.this, leaderboard.class);
                startActivity(intent);
            }
        });
        btn_flashCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home.this, gameTopic.class);
                editor.putString("game", "flashCard");
                editor.putString("mode", "Default");
                editor.apply();
                startActivity(intent);
            }
        });
        btn_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home.this, gameTopic.class);
                editor.putString("game", "quiz");
                editor.putString("mode", "Default");
                editor.apply();
                startActivity(intent);
            }
        });
        btn_fillWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home.this, gameTopic.class);
                editor.putString("game", "fillWord");
                editor.putString("mode", "default");
                editor.apply();
                startActivity(intent);
            }
        });
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
    private void GetUser()
    {
        editProfileVM.getInfoUser(idUser, new EditProfileVM.Callback() {
            @Override
            public void onUserReceived(User user) {
                txt_username.setText(user.getUsername());
                loadAndDisplayImageFromFirebaseStorage(user.getImg(),img_avatar);
                SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", user.getUsername());
                editor.apply();
            }

            @Override
            public void onUserNotFound() {

            }

            @Override
            public void onUserError(Exception e) {

            }
        });
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

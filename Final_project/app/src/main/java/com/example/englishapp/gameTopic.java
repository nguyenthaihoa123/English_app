package com.example.englishapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.model.Topic;
import com.example.englishapp.view.GameAdapter;
import com.example.englishapp.view.TopicAdapter;
import com.example.englishapp.view.TopicHomeAdapter;
import com.example.englishapp.viewmodel.TopicVM;

import java.util.ArrayList;
import java.util.List;

public class gameTopic extends AppCompatActivity {
    ImageButton btn_Back;
    EditText edt_search;
    TextView title;
    private RecyclerView rcv_Topic;
    private TopicHomeAdapter adapter ;
    private ArrayList<Topic> topicList = new ArrayList<Topic>();
    TopicVM topicVM = new TopicVM();
    String idUser;
    String game;
    String mode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gameplay);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("idUser", "user_2");
        game = sharedPreferences.getString("game","quiz");
        mode = sharedPreferences.getString("mode","Default");

        setupUI();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rcv_Topic.setLayoutManager(gridLayoutManager);
        adapter = new TopicHomeAdapter(this, topicList);
        rcv_Topic.setAdapter(adapter);


        buttonOnClick();
        FetchData();


    }

    private void FetchData(){
        topicVM.getTopicForCommunity(idUser,game,mode,new TopicVM.OnTopicsFetchedListener() {
            @Override
            public void onTopicFetched(Topic topic) {
                topicList.add(topic);
//                if(game.equals("quiz")&&topic.getVocabList().size()>=4){
//                    topicList.add(topic);
//                } else if ((game.equals("flashCard") || game.equals("fillWord")) &&topic.getVocabList().size()>=1) {
//                    topicList.add(topic);
//                }
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
                Toast.makeText(gameTopic.this,"Cập nhật dữ liệu thất bại ",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private  void setupUI(){
        btn_Back = findViewById(R.id.btn_back);
        rcv_Topic = findViewById(R.id.rcv_topic);
        title = findViewById(R.id.title);
        edt_search = findViewById(R.id.edt_searchTopic);
        if(game.equals("quiz")){
            title.setText("Quiz Game");
        } else if (game.equals("flashCard")) {
            title.setText("FlashCard");
        } else if (game.equals("fillWord")) {
            title.setText("Fill Word Game");
        }
    }
    private void buttonOnClick(){
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(gameTopic.this, gamePlay.class);
//                startActivity(intent);
                finish();
            }
        });
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String m_Text = charSequence.toString();
                // Sau đó, áp dụng bộ lọc dựa trên m_Text
                filter(m_Text);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
            Toast.makeText(gameTopic.this, "Không tìm thấy dữ liệu.", Toast.LENGTH_SHORT).show();
        }

        // Cập nhật danh sách trong Adapter với danh sách đã lọc
        adapter.filterList(filter_topicList);
    }
}

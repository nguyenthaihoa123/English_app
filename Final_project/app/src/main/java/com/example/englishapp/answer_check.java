package com.example.englishapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.model.Vocab;
import com.example.englishapp.view.VocabAdapter;
import com.example.englishapp.viewmodel.MultichoiceVM;
import com.example.englishapp.viewmodel.VocabVM;

import java.util.ArrayList;
import java.util.List;

public class answer_check extends AppCompatActivity {
    private VocabVM vocabViewModel;
    TextView title, pointCircle, txt_correct, txt_wrong;
    ImageButton btn_back;
    RecyclerView recyclerView;
    VocabVM vocabVM = new VocabVM();
    private VocabAdapter vocabAdapter;
    private  ArrayList<String> P_correct = new ArrayList<>();
    private  ArrayList<String> P_wrong = new ArrayList<>();
    private String idTopic,idUser,game;
    private List<Vocab> vocabList= new ArrayList<>();
    private ArrayList<Vocab> vocabWrong= new ArrayList<>();
    private ArrayList<Vocab> vocabRight= new ArrayList<>();

//    private Integer P_correct = 0,P_wrong = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_check);
        AnhXa();
//        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // Lấy giá trị của P_correct và P_wrong từ Intent or Bundle
//            int P_correct = intent.getIntExtra("P_correct", 0);
//            int P_wrong = intent.getIntExtra("P_wrong", 0);
            P_correct = bundle.getStringArrayList("P_correct");
            P_wrong = bundle.getStringArrayList("P_wrong");
            idTopic = bundle.getString("idTopic");
//            game = bundle.getString("game");
            SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            idUser = sharedPreferences.getString("idUser", "user_2");
            game = sharedPreferences.getString("game","quiz");

            // Bây giờ bạn có thể sử dụng P_correct và P_wrong trong activity answer_check
            title.setText("You get +"+ P_correct.size()*10+" point");
            pointCircle.setText(P_correct.size() * 10 +"");
            txt_correct.setText(P_correct.size()+"");
            txt_wrong.setText(P_wrong.size()+"");
        }
        vocabAdapter = new VocabAdapter(answer_check.this, vocabList);
        recyclerView.setLayoutManager(new LinearLayoutManager(answer_check.this));
        recyclerView.setAdapter(vocabAdapter);
        getListAnswer();


        btn_back = findViewById(R.id.btn_back_ansCheck);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(game.equals("quiz"))
                {
//                    Intent intent = new Intent(answer_check.this, gameTopic.class);
//                    Bundle bundle = new Bundle();
//                    intent.putExtras(bundle);
//
//                    startActivity(intent);
                    // Đóng Activity hiện tại
                    finish();
                } else if (game.equals("flashCard")) {
                    finish();
                } else if (game.equals("fillWord")) {
                    Intent intent = new Intent(answer_check.this, gameTopic.class);
                    startActivity(intent);
                }else {
//                    Intent intent = new Intent(answer_check.this, com.example.englishapp.topic_details.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("idOwner", idUser);
//                    bundle.putString("idTopic",idTopic);
//                    intent.putExtras(bundle);
//
//                    startActivity(intent);
                    // Đóng Activity hiện tại
                    finish();
                }

            }
        });

    }
    public void AnhXa(){
        title = findViewById(R.id.txt_title_point);
        pointCircle = findViewById(R.id.txt_pointInCircle);
        txt_correct = findViewById(R.id.txt_quantity_correct);
        txt_wrong = findViewById(R.id.txt_quantity_wrong);
        recyclerView = findViewById(R.id.rcv_listAnswer);
    }
    public void getListAnswer(){
        vocabVM.getVocabBasesList(P_correct,idTopic, new VocabVM.OnCheckAnswer() {
            @Override
            public void onVocabFetch(ArrayList<Vocab> listAnswer) {
                vocabRight = listAnswer;
//                Toast.makeText(answer_check.this, "Thành công"+listAnswer.size()+""+P_correct, Toast.LENGTH_SHORT).show();
                vocabList.clear(); // Clear existing data
                vocabList.addAll(listAnswer); // Add fetched data
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vocabAdapter.notifyDataSetChanged(); // Notify adapter of changes
                    }
                });
            }
            @Override
            public void onFetchError(Exception e) {

            }
        });
    }
}

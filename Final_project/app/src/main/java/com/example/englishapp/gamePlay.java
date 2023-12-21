package com.example.englishapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.model.Topic;
import com.example.englishapp.viewmodel.RankVM;
import com.example.englishapp.viewmodel.TopicVM;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.checkerframework.checker.nullness.qual.NonNull;

public class gamePlay extends AppCompatActivity {
    TopicVM topicVM = new TopicVM();
    RankVM rankVM = new RankVM();
    String idUser,username;
    TextView number_quiz,number_flashcard,number_fillWord,intro;
    ImageView txt_username;
    ImageButton btn_defaultQuiz,btn_defaultFlashcard,btn_defaultfillWord,btn_randomQuiz,btn_randomFlashcard,btn_randomfillWord,btn_leaderBoard;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);

        idUser = sharedPreferences.getString("idUser", "user_2");
        username = sharedPreferences.getString("username", "Elephant47");

        //===================================================BottomBar========================================
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottombarGame);
        bottomNavigationView.getMenu().getItem(0).setChecked(false);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
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
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.folderPage) {
                    startActivity(new Intent(getApplicationContext(), folderTopic.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.mywordPage) {
                    // Thêm các xử lý cho trang chủ nếu cần
                    startActivity(new Intent(getApplicationContext(), my_word.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.gamePage) {
                    return true;
                }

                return false;
            }
        });
        //===================================================================================================//
        setupUI();
        getCountTopic();
        addNewuserOnRank();
        buttonOnClick();
        intro.setText(username);

    }
    private  void setupUI()
    {
        btn_defaultfillWord = findViewById(R.id.btnDeafault_fillWord);
        btn_defaultFlashcard = findViewById(R.id.btnDeafault_flashCard);
        btn_defaultQuiz = findViewById(R.id.btnDeafault_Quizz);
        btn_randomfillWord = findViewById(R.id.btnRandom_fillWord);
        btn_randomFlashcard = findViewById(R.id.btnRandom_flashCard);
        btn_randomQuiz = findViewById(R.id.btnRandom_Quizz);
        btn_leaderBoard = findViewById(R.id.btnLeaderBoard);
        intro = findViewById(R.id.intro);
        number_quiz = findViewById(R.id.numberOfQuizz);
        number_flashcard = findViewById(R.id.numberOfFlashCard);
        number_fillWord = findViewById(R.id.numberOfFillWord);
    }
    private void getCountTopic()
    {
        topicVM.getCountTopicForGame(idUser,"quiz", new TopicVM.OnTopicCountListener() {
            @Override
            public void onTopicGameFetched(Integer b) {
                number_quiz.setText(String.format("%02d",b)+"/"+String.format("%02d",b));
            }

            @Override
            public void onFetchError(Exception e) {
                Toast.makeText(gamePlay.this, "Cập nhật dữ liệu thất bại"+e, Toast.LENGTH_SHORT).show();
            }
        });
        topicVM.getCountTopicForGame(idUser,"flashCard", new TopicVM.OnTopicCountListener() {
            @Override
            public void onTopicGameFetched(Integer b) {
                number_fillWord.setText(String.format("%02d",b)+"/"+String.format("%02d",b));
                number_flashcard.setText(String.format("%02d",b)+"/"+String.format("%02d",b));
            }

            @Override
            public void onFetchError(Exception e) {
                Toast.makeText(gamePlay.this, "Cập nhật dữ liệu thất bại"+e, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addNewuserOnRank()
    {
        rankVM.NewUser(idUser, new RankVM.onNewUser() {
            @Override
            public void onCompleteUser() {
            }

            @Override
            public void onFetchError(Exception e) {
            }
        });
    }
    private void buttonOnClick()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        btn_defaultQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(gamePlay.this, gameTopic.class);
                editor.putString("game", "quiz");
                editor.putString("mode", "Default");
                editor.apply();
                startActivity(intent);
            }
        });
        btn_randomQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(gamePlay.this, gameTopic.class);
                editor.putString("game", "quiz");
                editor.putString("mode", "Random");
                editor.apply();
                startActivity(intent);
            }
        });
        btn_defaultfillWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(gamePlay.this, gameTopic.class);
                editor.putString("game", "fillWord");
                editor.putString("mode", "Default");
                editor.apply();
                startActivity(intent);
            }
        });
        btn_randomfillWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(gamePlay.this, gameTopic.class);
                editor.putString("game", "fillWord");
                editor.putString("mode", "Random");
                editor.apply();
                startActivity(intent);
            }
        });
        btn_defaultFlashcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(gamePlay.this, gameTopic.class);
                editor.putString("game", "flashCard");
                editor.putString("mode", "Default");
                editor.apply();
                startActivity(intent);
            }
        });
        btn_randomFlashcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(gamePlay.this, gameTopic.class);
                editor.putString("game", "flashCard");
                editor.putString("mode", "Random");
                editor.apply();
                startActivity(intent);
            }
        });
        btn_leaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(gamePlay.this, leaderboard.class);
                startActivity(intent);
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

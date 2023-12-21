package com.example.englishapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.model.User;
import com.example.englishapp.view.GameAdapter;
import com.example.englishapp.view.RankAdapter;
import com.example.englishapp.viewmodel.RankVM;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class leaderboard extends AppCompatActivity {
    RankVM rankVM = new RankVM();
    RecyclerView rcv_rank;
    RankAdapter adapter;
    ArrayList<User> userList = new ArrayList<>();
    ShapeableImageView img_Rank1,img_Rank2,img_Rank3;
    TextView score_1,score_2,score_3,name_1,name_2,name_3;
    RadioButton btn_Today,btn_Week,btn_Month;
    RadioGroup toggle;
    ImageButton btn_Back;
    String idUser;
    Date startTime,endTime;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        setupUI();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("idUser", "user_2");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1);
        rcv_rank.setLayoutManager(gridLayoutManager);
        adapter = new RankAdapter(this, userList);
        rcv_rank.setAdapter(adapter);

        GetCurrentTime("Month");
        buttonOnClick();
        FetchData();

    }
    private  void FetchData()
    {
        rankVM.GetUserForTime(startTime, endTime, new RankVM.onScoreForTime() {
            @Override
            public void onScoreFetch(Map<User, Integer> userScore) {
                int i = 1;
                for (Map.Entry<User, Integer> entry : userScore.entrySet()) {
                    User user = entry.getKey();  // Retrieve the User object (key)
                    user.setStt(i++);
                    user.setScore(entry.getValue());
                    if(user.getStt() == 1) {
                        name_1.setText(user.getUsername());
                        score_1.setText(String.valueOf(user.getScore()));
                        loadAndDisplayImageFromFirebaseStorage(user.getImg(),img_Rank1);
                    } else if (user.getStt() == 2) {
                        name_2.setText(user.getUsername());
                        score_2.setText(String.valueOf(user.getScore()));
                        loadAndDisplayImageFromFirebaseStorage(user.getImg(),img_Rank2);
                    } else if (user.getStt() == 3) {
                        name_3.setText(user.getUsername());
                        score_3.setText(String.valueOf(user.getScore()));
                        loadAndDisplayImageFromFirebaseStorage(user.getImg(),img_Rank3);
                    }
                    userList.add(user);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged(); // Gọi notifyDataSetChanged từ adapter
                            // Hiển thị thông báo thành công
//                            Toast.makeText(topicList.this, "Thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFetchError(Exception e) {
                Toast.makeText(leaderboard.this, "Cập nhật dữ liệu thất bại"+"\n"+e, Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void setupUI()
    {
        img_Rank1 = findViewById(R.id.rank_1);
        img_Rank2 = findViewById(R.id.rank_2);
        img_Rank3 = findViewById(R.id.rank_3);
        score_1 = findViewById(R.id.score_1);
        score_2 = findViewById(R.id.score_2);
        score_3 = findViewById(R.id.score_3);
        name_1 = findViewById(R.id.name_1);
        name_2 = findViewById(R.id.name_2);
        name_3 = findViewById(R.id.name_3);
        toggle = findViewById(R.id.toggle);
        btn_Month = findViewById(R.id.btn_Month);
        btn_Today = findViewById(R.id.btn_Today);
        btn_Week = findViewById(R.id.btn_Week);
        btn_Back = findViewById(R.id.btn_back);
        rcv_rank = findViewById(R.id.rcv_leaderboard);

    }

    private void GetCurrentTime(String time)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-2;
        if(dayOfWeek == -1)
        {
            dayOfWeek = 6;
        }
        if(time.equals("Today"))
        {
            startTime = calendar.getTime();
            //=====set endTime =============//
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            ////////////==================/////
            endTime = calendar.getTime();
        } else if (time.equals("Week")){
            calendar.add(Calendar.DAY_OF_MONTH,-dayOfWeek);
            startTime = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH,6);
            //=====set endTime =============//
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            //====================///
            endTime =  calendar.getTime();
        }else {
            calendar.add(Calendar.DAY_OF_MONTH,-calendar.get(Calendar.DAY_OF_MONTH)+1);
            startTime = calendar.getTime();
            calendar.add(Calendar.MONTH,1);
            calendar.add(Calendar.DATE,-1);
            //=====set endTime =============//
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            //====================///
            endTime =  calendar.getTime();
        }
    }
    private void buttonOnClick()
    {

        toggle.setOnCheckedChangeListener((group, checkedId) -> {
            userList.clear();
            adapter = new RankAdapter(this, userList);
            rcv_rank.setAdapter(adapter);
            rcv_rank.setLayoutManager(new GridLayoutManager(this,1));
            if (checkedId == R.id.btn_Today) {
                GetCurrentTime("Today");
                FetchData();
            } else if (checkedId == R.id.btn_Week) {
                // Chuyển đến nút Offers
                GetCurrentTime("Week");
                FetchData();
            }else if (checkedId == R.id.btn_Month){
                GetCurrentTime("Month");
                FetchData();
            }
        });
//        btn_Today.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GetCurrentTime("Today");
//                FetchData();
//            }
//        });
//        btn_Week.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GetCurrentTime("Week");
//                FetchData();
//            }
//        });
//        btn_Month.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GetCurrentTime("Month");
//                FetchData();
//            }
//        });
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void loadAndDisplayImageFromFirebaseStorage(String storagePath, ShapeableImageView imageView) {
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

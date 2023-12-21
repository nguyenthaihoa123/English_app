package com.example.englishapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.englishapp.model.User;
import com.example.englishapp.viewmodel.EditProfileVM;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Profile extends AppCompatActivity {
    private TextView username, email;
    private ImageButton btnSwEdit;
    Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String documentID = sharedPreferences.getString("idUser", "user_2");
//==================================================bottombar================================================================
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonnavProfile);
        bottomNavigationView.getMenu().getItem(0).setChecked(false);
        bottomNavigationView.getMenu().getItem(4).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.proPage) {
                    return true;
                } else if (item.getItemId() == R.id.homePage) {
                    // Thêm các xử lý cho trang chủ nếu cần
                    startActivity(new Intent(getApplicationContext(), home.class));
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
                    startActivity(new Intent(getApplicationContext(), gamePlay.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }

                return false;
            }
        });
//============================================================================================================
        EditProfileVM editData = new EditProfileVM();
        ThamChieu();

        editData.getInfoUser(documentID, new EditProfileVM.Callback() {
            @Override
            public void onUserReceived(User user) {
                // Xử lý thông tin người dùng ở đây sau khi nhận được dữ liệu
                String usernameGet = user.getUsername();
                String emailGet = user.getEmail();
                username.setText(usernameGet);
                email.setText(emailGet);
            }

            @Override
            public void onUserNotFound() {
                // Xử lý trường hợp không tìm thấy người dùng
            }

            @Override
            public void onUserError(Exception e) {
                // Xử lý lỗi khi truy vấn Firebase
            }
        });
        btnSwEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), edit_profile.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), login.class));
                finish();
            }
        });
    }
    public void ThamChieu(){
        username = findViewById(R.id.txtUsername_Profile);
        email = findViewById(R.id.txtEmailUser_Profile);
        btnSwEdit = findViewById(R.id.btnSwitch_toEdit);
        logout = findViewById(R.id.idBtnlogOut);
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
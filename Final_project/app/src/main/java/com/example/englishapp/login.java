package com.example.englishapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.model.Folder;
import com.example.englishapp.model.User;
import com.example.englishapp.viewmodel.LoginVM;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class login extends AppCompatActivity {

    private EditText username, password;
    ImageButton btnshowPwd;
    Button btnSignIn;

    TextView txt_signUp, btnForgot;

    private FirebaseFirestore db;
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private FirebaseAuth mAuth;
    LoginVM newLogin;
    private boolean confirm = false;
    String enteredUsername,enteredPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        txt_signUp = findViewById(R.id.txt_SignUp);
        btnForgot = findViewById(R.id.btnForgot_pass);
        btnshowPwd = findViewById(R.id.btn_show_pwd);
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        btnSignIn = findViewById(R.id.btnLogin);
        newLogin = new LoginVM();
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredUsername = username.getText().toString();
                enteredPassword = password.getText().toString();
                loginUser(enteredUsername,enteredPassword);

//                if(newLogin.isPasswordValid(password.getText().toString()) && confirm == true){
//
//                    newLogin.getAccountAu(enteredUsername, new LoginVM.AccountCallback() {
//                        @Override
//                        public void onAccountReceived(String documentId, String username, String password) {
//                            // Xử lý tài khoản đã được tìm thấy
//                            // documentId là Document ID của tài khoản
////                            Intent intent = new Intent(login.this, MainActivity.class);
//                            SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString("idUser", documentId);
//                            editor.apply();
//
//                            Intent intent = new Intent(login.this, home.class);
//                            intent.putExtra("documentId", documentId);
//                            startActivity(intent);
//                            if(intent.hasExtra("documentId")){
//                                Toast.makeText(login.this,"Document ID: " + documentId,Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//                        @Override
//                        public void onAccountNotFound() {
//                            // Xử lý khi không tìm thấy tài khoản
//                            Toast.makeText(login.this,"Not found user",Toast.LENGTH_LONG).show();
//
//                        }
//
//                        @Override
//                        public void onAccountError(Exception e) {
//                            // Xử lý khi có lỗi
//                            Toast.makeText(login.this,"Error",Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//                // Khi nút đăng nhập được nhấn
//                else {
//                    Toast.makeText(login.this, "Failure", Toast.LENGTH_LONG).show();
//                }
            }
        });

        txt_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, register.class);
                startActivity(intent);
            }
        });
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, VerifyOTP.class);
                startActivity(intent);
            }
        });
        final boolean[] isPasswordVisible = {false};
        btnshowPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPasswordVisible[0] = !isPasswordVisible[0];

                // Thay đổi kiểu của EditText dựa trên trạng thái của biến cờ
                if (isPasswordVisible[0]) {
                    // Nếu đang hiển thị mật khẩu, đặt kiểu về text để hiển thị
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    // Nếu không hiển thị mật khẩu, đặt kiểu về textPassword để ẩn
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                // Di chuyển con trỏ đến cuối văn bản
                password.setSelection(password.getText().length());
            }
        });
    }
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công
//                            Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                            // Thực hiện các bước tiếp theo sau khi đăng nhập thành công
                            // Ví dụ: chuyển đến màn hình chính
                            handleLoginSuccess();
                        } else {
                            // Đăng nhập thất bại
                            Toast.makeText(login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            handleLoginFailure();
                        }
                    }
                });
    }
    private void handleLoginSuccess() {
        // Thực hiện các bước tiếp theo sau khi đăng nhập thành công
        confirm = true;

        // Kiểm tra điều kiện trước khi thực hiện logic tiếp theo
        if (newLogin.isPasswordValid(password.getText().toString()) && confirm) {
            newLogin.getAccountAu(enteredUsername, new LoginVM.AccountCallback() {
                @Override
                public void onAccountReceived(String documentId, String username, String password) {
                    // Xử lý tài khoản đã được tìm thấy
                    // documentId là Document ID của tài khoản
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("idUser", documentId);
                    editor.apply();

                    Intent intent = new Intent(login.this, MainActivity.class);
                    intent.putExtra("documentId", documentId);
                    startActivity(intent);
                    finish();
                    if (intent.hasExtra("documentId")) {
//                        Toast.makeText(login.this, "Document ID: " + documentId, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onAccountNotFound() {
                    // Xử lý khi không tìm thấy tài khoản
                    Toast.makeText(login.this, "Not found user", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onAccountError(Exception e) {
                    // Xử lý khi có lỗi
                    Toast.makeText(login.this, "Error", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(login.this, "Failure", Toast.LENGTH_LONG).show();
        }
    }

    private void handleLoginFailure() {
        // Xử lý khi đăng nhập thất bại
        confirm = false;
        // Các thao tác khác khi đăng nhập thất bại
    }
}
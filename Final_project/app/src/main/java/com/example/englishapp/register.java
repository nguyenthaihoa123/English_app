package com.example.englishapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.englishapp.viewmodel.LoginVM;
import com.example.englishapp.viewmodel.RegisterVM;

public class register extends AppCompatActivity {
    private EditText username,password,email,confirmPass;
    private Button btn_register;
    private CheckBox btn_check;
    private TextView txt_signIn;
    private ImageView btn_show, btn_show_Confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        confirmPass =findViewById(R.id.confirmPassword);
        btn_register =findViewById(R.id.btn_Register);
        txt_signIn = findViewById(R.id.txt_SignIn);
        btn_check =findViewById(R.id.btn_check);
        btn_show = findViewById(R.id.btn_show_pwdR);
        btn_show_Confirm = findViewById(R.id.btn_show_pwdRC);

        RegisterVM newRegister = new RegisterVM();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String IP_email = email.getText().toString();
                String IP_username = username.getText().toString();
                String IP_password = password.getText().toString();
                String IP_confirmPass = confirmPass.getText().toString();
                boolean acceptConditions =  btn_check.isChecked();
                if (IP_username.isEmpty() || IP_password.isEmpty() || IP_email.isEmpty() || IP_confirmPass.isEmpty()) {
                    // Check if any of the fields are empty
                    Toast.makeText(register.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                } else if (IP_password.length() < 8||!IP_password.matches(".*[A-Z].*") || !IP_password.matches(".*[a-z].*")||!IP_password.matches(".*\\d.*")||!IP_password.matches(".*[^a-zA-Z0-9].*")) {
                    // Check if the password is too short (you can adjust the length as needed)
                    Toast.makeText(register.this, "Invalid Password", Toast.LENGTH_LONG).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(IP_email).matches()) {
                    // Check if the email address is in a valid format
                    Toast.makeText(register.this, "Invalid email address", Toast.LENGTH_LONG).show();
                } else if (!IP_password.equals(IP_confirmPass)) {
                    // Check if password and confirm password do not match
                    Toast.makeText(register.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }else if (!acceptConditions) {
                    Toast.makeText(register.this, "Please accept the terms and conditions", Toast.LENGTH_LONG).show();
                } else {
                    newRegister.addAccount(IP_username, IP_password, IP_email, IP_confirmPass, new RegisterVM.AccountCallback() {
                        @Override
                        public void onAccountReceived(String documentId, String username, String password, String email) {

                            Toast.makeText(register.this, "Registration successful", Toast.LENGTH_LONG).show();
                            ///////////////////
                            Intent intent = new Intent(register.this, login.class);
                            startActivity(intent);

                        }

                        @Override
                        public void onAccountFound() {
                            // Xử lý khi không tìm thấy tài khoản
                            Toast.makeText(register.this, " Username already exists", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onAccountError(Exception e) {
                            // Xử lý khi có lỗi
                            Toast.makeText(register.this, "Registration error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        txt_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(register.this, login.class);
//                startActivity(intent);
                finish();
            }
        });
        final boolean[] isPasswordVisible = {false};
        btn_show.setOnClickListener(new View.OnClickListener() {
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
        final boolean[] isConfirmPasswordVisible = {false};
        btn_show_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isConfirmPasswordVisible[0] = !isConfirmPasswordVisible[0];

                // Thay đổi kiểu của EditText dựa trên trạng thái của biến cờ
                if (isConfirmPasswordVisible[0]) {
                    // Nếu đang hiển thị mật khẩu, đặt kiểu về text để hiển thị
                    confirmPass.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    // Nếu không hiển thị mật khẩu, đặt kiểu về textPassword để ẩn
                    confirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                // Di chuyển con trỏ đến cuối văn bản
                confirmPass.setSelection(confirmPass.getText().length());
            }
        });

    }
}
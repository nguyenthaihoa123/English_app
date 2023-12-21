package com.example.englishapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.englishapp.model.User;
import com.example.englishapp.viewmodel.EditProfileVM;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;

public class edit_profile extends AppCompatActivity {
    TextInputEditText usernameEdt, pwdEdt, emailEdt, numberEdt, addressEdt;
    ShapeableImageView imgAvt;
    String id = "user_2", img_fb;
    Uri filePath;
    Button btnSubmit,btnChange;
    ImageButton btnSelectPicture, btnBack;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String documentID = sharedPreferences.getString("idUser", "user_2");
        EditProfileVM editData = new EditProfileVM();
        ThamChieu();
        editData.getInfoUser(documentID, new EditProfileVM.Callback() {
            @Override
            public void onUserReceived(User user) {
                // Xử lý thông tin người dùng ở đây sau khi nhận được dữ liệu
                String username = user.getUsername();
                String password = user.getPwd();
                String number = user.getNumber();
                String email = user.getEmail();
                String address = user.getAddress();
                img_fb = user.getImg();
                id = user.getID();
                // Và các thông tin khác của người dùng
                usernameEdt.setText(username);
//                pwdEdt.setText(password);
                numberEdt.setText(number);
                emailEdt.setText(email);
                addressEdt.setText(address);

                loadAndDisplayImageFromFirebaseStorage(user.getImg(),imgAvt);
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
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEdt.getText().toString();
                String password = pwdEdt.getText().toString();
                String email = emailEdt.getText().toString();
                String number = numberEdt.getText().toString();
                String address = addressEdt.getText().toString();
                User user = new User(username, password, id, email, address, number,img_fb);

                EditProfileVM editData = new EditProfileVM();
                editData.updateUserInfo(user,id,edit_profile.this,filePath, new EditProfileVM.CallbackUpdate() {
                    @Override
                    public void onUserUpdated() {
                        // Xử lý khi cập nhật thành công
                        // Ví dụ: Hiển thị thông báo cập nhật thành công cho người dùng
                        Toast.makeText(edit_profile.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUserUpdateError(Exception e) {
                        // Xử lý khi có lỗi xảy ra trong quá trình cập nhật
                        // Ví dụ: Hiển thị thông báo lỗi cho người dùng
                        Toast.makeText(edit_profile.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        btnSelectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnBack = findViewById(R.id.btn_back_editPro);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResetPasswordEmail();
            }
        });
    }
    public void ThamChieu(){
        usernameEdt = findViewById(R.id.txtUserNameEditPro);
        pwdEdt = findViewById(R.id.txtPwdEditPro);
        emailEdt = findViewById(R.id.txtEmailEditPro);
        numberEdt = findViewById(R.id.txtNumberEditPro);
        addressEdt = findViewById(R.id.txtAddressEditPro);
        btnSubmit = findViewById(R.id.btnSubmitEditPro);
        imgAvt = findViewById(R.id.imgAvt);
        btnSelectPicture = findViewById(R.id.btnSelectPicture);
        btnChange= findViewById(R.id.btn_change_pwd);
    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgAvt.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
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
    private void sendResetPasswordEmail() {
        String email = emailEdt.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(edit_profile.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(edit_profile.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
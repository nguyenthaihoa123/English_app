package com.example.englishapp;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishapp.viewmodel.VocabVM;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;

public class add_vocab extends AppCompatActivity {
    EditText edtWord, edtTrans;
    Button submitVocab, btn_add_img,btn_import;
    String topicId = "topic_1"; // Thay thế bằng id của chủ đề bạn muốn thêm từ vựng vào
    VocabVM vocabViewModel;
    private ImageView imageView;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_vocab);
        FirebaseApp.initializeApp(this);

        Intent intent = getIntent();
        topicId = intent.getStringExtra("idTopic");

        edtWord = findViewById(R.id.edt_word);
        edtTrans = findViewById(R.id.edt_trans);
        imageView = (ImageView) findViewById(R.id.img_Vocab);
        btn_add_img = findViewById(R.id.btn_upload_imgVC);
        btn_import = findViewById(R.id.btn_import_vocab);




        submitVocab = findViewById(R.id.btn_insert_vocab);
        vocabViewModel = new VocabVM(); // Khởi tạo ViewModel
        btn_add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        submitVocab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = edtWord.getText().toString();
                String trans = edtTrans.getText().toString();

                // Kiểm tra xem từ và dịch có giá trị không trống
                if (!TextUtils.isEmpty(word) && !TextUtils.isEmpty(trans)) {
                    addVocab(word, trans);
                } else {
                    // Hiển thị thông báo lỗi nếu cần
                    Toast.makeText(add_vocab.this, "Vui lòng nhập cả từ và dịch", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_back = findViewById(R.id.btn_back_accVocab);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(add_vocab.this, import_data.class);

                // Truyền biến topicid qua Activity mới
                intent.putExtra("topicid", topicId);

                // Bắt đầu Activity mới
                startActivity(intent);
            }
        });
    }
    private void addVocab(String word, String trans) {
        vocabViewModel.addVocabForUser(topicId, word, trans,add_vocab.this,filePath, new VocabVM.OnVocabAddedListener() {
            @Override
            public void onVocabAdded(String vocabId) {
                // Xử lý khi từ vựng được thêm thành công
                Toast.makeText(add_vocab.this, "Thêm từ vựng thành công", Toast.LENGTH_SHORT).show();
                edtWord.setText("");
                edtTrans.setText("");
                filePath = null;
                imageView.setImageDrawable(null);
                // Có thể thực hiện bất kỳ hành động nào khác ở đây sau khi thêm từ vựng thành công
            }

            @Override
            public void onAddError(Exception e) {
                // Xử lý khi có lỗi khi thêm từ vựng
                Toast.makeText(add_vocab.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}

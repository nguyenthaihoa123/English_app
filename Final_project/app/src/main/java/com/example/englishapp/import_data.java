package com.example.englishapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.model.Vocab;
import com.example.englishapp.view.VocabAdapter;
import com.example.englishapp.viewmodel.VocabVM;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class import_data  extends AppCompatActivity {
    Button btn_select, btn_submit;
    ImageButton btn_back;
    List<Vocab> vocabList = new ArrayList<>();
    private VocabAdapter vocabAdapter;
    VocabVM vocabViewModel;
    private String topicId = "topic_1";

    private static final int PICK_CSV_FILE = 1; // Mã yêu cầu để nhận biết khi chọn tệp tin
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_data);
        btn_select = findViewById(R.id.btn_import_csv);
        btn_back= findViewById(R.id.btn_back_importVC);
        RecyclerView recyclerView = findViewById(R.id.rcv_data_csv);

        Intent intent = getIntent();
        if (intent != null) {
            topicId = intent.getStringExtra("topicid");
            // Bây giờ bạn có thể sử dụng giá trị của topicId trong Activity mới
        }
        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/csv"); // Chấp nhận mọi loại tệp tin

                startActivityForResult(intent, PICK_CSV_FILE);
            }
        });
        btn_submit = findViewById(R.id.btn_submit_csv);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vocabList.size() > 0) {
                    addVocabListToFirestore(vocabList, topicId);
                } else {
                    Toast.makeText(import_data.this, "Không có dữ liệu để thêm", Toast.LENGTH_SHORT).show();
                }
            }
        });

        vocabAdapter = new VocabAdapter(this,vocabList);
        recyclerView.setLayoutManager(new LinearLayoutManager(import_data.this));
        recyclerView.setAdapter(vocabAdapter);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CSV_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        if (inputStream != null) {
                            // Sử dụng OpenCSV để đọc dữ liệu từ tệp CSV
                            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
                            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(inputStream))
                                    .withCSVParser(csvParser)
                                    .build();

                            // Đọc dòng đầu tiên (bao gồm cả tiêu đề)
                            String[] header = csvReader.readNext();
                            String word = header[0];
                            String trans = header[1];
                            Vocab vocab = new Vocab(word, trans);
                            vocabList.add(vocab);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    vocabAdapter.notifyDataSetChanged(); // Gọi notifyDataSetChanged từ adapter
                                }
                            });
                            // Kiểm tra header và đọc dữ liệu
                            if (header != null && header.length == 2) {
                                String[] record;
                                do {
                                    record = csvReader.readNext();
                                    if (record != null) {
                                        word = record[0];
                                        trans = record[1];

                                        // Tạo đối tượng Vocab và thêm vào mảng
                                        vocab = new Vocab(word, trans);
                                        vocabList.add(vocab);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                vocabAdapter.notifyDataSetChanged(); // Gọi notifyDataSetChanged từ adapter
                                            }
                                        });
                                    }
                                } while (record != null);
                            } else {
                                // Xử lý header không hợp lệ
                                // ...
                            }

                            // Đóng CSVReader và InputStream khi không cần thiết
                            csvReader.close();
                            inputStream.close();

                            if (vocabList.size() > 0) {
                                Toast.makeText(import_data.this, "Dữ liệu đã được nhận từ file CSV", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(import_data.this, "Không có dữ liệu trong file CSV", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void addVocabListToFirestore(List<Vocab> vocabList, String topicId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference topicDocRef = db.collection("Topic").document(topicId);
        CollectionReference vocabCollection = topicDocRef.collection("Vocab");

        // Lấy số lượng tài liệu trong bộ sưu tập
        vocabCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int documentCount = queryDocumentSnapshots.size();

                    // Thêm từ vựng vào Firestore
                    for (Vocab vocab : vocabList) {
                        documentCount++; // Tăng số lượng tài liệu

                        // Tạo mới ID dựa trên số lượng tài liệu
                        String newVocabId = "vocab_" + documentCount;

                        Map<String, Object> vocabData = new HashMap<>();
                        vocabData.put("id", newVocabId);
                        vocabData.put("word", vocab.getWord());
                        vocabData.put("trans", vocab.getTrans());
                        vocabData.put("image", "images/csv.png");
                        vocabData.put("process", "0");
                        vocabData.put("favourite", "");

                        vocabCollection.document(newVocabId)
                                .set(vocabData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Gọi phương thức trả kết quả (listener) để thông báo thành công
                                        Toast.makeText(import_data.this, "Thêm từ vựng thành công", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        // Xử lý lỗi nếu cần
                                        Toast.makeText(import_data.this, "Lỗi khi thêm từ vựng", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi có lỗi khi truy vấn số lượng tài liệu
                    Toast.makeText(import_data.this, "Lỗi khi lấy số lượng tài liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }





}

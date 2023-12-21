package com.example.englishapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.model.Vocab;
import com.example.englishapp.view.VocabAdapter;
import com.example.englishapp.viewmodel.TopicVM;
import com.example.englishapp.viewmodel.VocabVM;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.Manifest;


public class topic_details extends AppCompatActivity implements VocabAdapter.OnVocabDeleteListener {
    private String m_Text = "";
    private List<Vocab> vocabList= new ArrayList<>();
    private VocabVM vocabViewModel;
    private VocabAdapter vocabAdapter;
    EditText search;
    ImageButton btn_back,btn_flashcard, btn_wordfill, btn_quizz;

    FloatingActionButton add_vocab, btn_export;
    Switch swtState;

    String idTopic, idOwner;
    private boolean state;
    private static final int CREATE_FILE_REQUEST_CODE = 123;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_details);

        vocabViewModel = new VocabVM();
        Bundle bundle = getIntent().getExtras();
        idOwner =  bundle.getString("idOwner");
        idTopic = bundle.getString("idTopic");
        state = bundle.getBoolean("access");

        swtState = findViewById(R.id.btn_state_access);
        swtState.setChecked(state);
        TopicVM topicVM = new TopicVM();
        swtState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                topicVM.updateTopicAccess(idTopic, isChecked, new TopicVM.OnTopicUpdatedListener() {
                    @Override
                    public void onTopicUpdated() {
                        // Xử lý khi cập nhật thành công
                        Toast.makeText(topic_details.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUpdateError(Exception e) {
                        // Xử lý khi cập nhật thất bại
                        Toast.makeText(topic_details.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        getWordsForUserAndTopic(idOwner, idTopic);
        Handler handler = new Handler();


        vocabAdapter = new VocabAdapter(topic_details.this, vocabList);
        RecyclerView recyclerView = findViewById(R.id.rcv_vocab_detailTP);
        recyclerView.setLayoutManager(new LinearLayoutManager(topic_details.this));
        vocabAdapter.setOnVocabDeleteListener(this);
        recyclerView.setAdapter(vocabAdapter);

        search = findViewById(R.id.edt_search_vovab_detailTP);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                m_Text = charSequence.toString();
                // Sau đó, áp dụng bộ lọc dựa trên m_Text
                filter(m_Text);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_back = findViewById(R.id.btn_back_word);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//============================Thêm từ vựng======================================
        add_vocab = findViewById(R.id.btn_add_vocab_detailTP);
        add_vocab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(topic_details.this, com.example.englishapp.add_vocab.class);
                intent.putExtra("idTopic",idTopic);
                startActivity(intent);
            }
        });
//============================ôn tập flashcard====================================
        btn_flashcard = findViewById(R.id.btn_flashcard);
        btn_flashcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (vocabList.size() > 0){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(topic_details.this);
                    builder1.setTitle("Xáo Trộn");
                    builder1.setMessage("Bạn có muốn xáo trộn danh sách không?");

                    builder1.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Collections.shuffle(vocabList);
                            String jsonVocabList = new Gson().toJson(vocabList);

                            // Tạo Intent để mở hoạt động FlashcardActivity
                            Intent intent = new Intent(topic_details.this, flashcard.class);
                            intent.putExtra("topicID",idTopic);

                            // Đưa chuỗi JSON vào Intent
                            intent.putExtra("jsonVocabList", jsonVocabList);

                            // Khởi động hoạt động FlashcardActivity
                            startActivity(intent);
                        }
                    });

                    builder1.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Không làm gì cả
                            String jsonVocabList = new Gson().toJson(vocabList);

                            // Tạo Intent để mở hoạt động FlashcardActivity
                            Intent intent = new Intent(topic_details.this, flashcard.class);
                            intent.putExtra("topicID",idTopic);

                            // Đưa chuỗi JSON vào Intent
                            intent.putExtra("jsonVocabList", jsonVocabList);

                            // Khởi động hoạt động FlashcardActivity
                            startActivity(intent);
                        }
                    });

                    AlertDialog dialog = builder1.create();
                    dialog.show();


                } else {
                    // Nếu kích thước nhỏ hơn 4, hiển thị dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(topic_details.this);
                    builder.setTitle("Cảnh báo");
                    builder.setMessage("Bạn không đủ số lượng từ để ôn lật thẻ.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Đóng dialog khi bấm OK
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }

            }
        });
//============================ôn tập điền từ ====================================
        btn_wordfill = findViewById(R.id.btn_wordfill);
        btn_wordfill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vocabList.size() > 0){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(topic_details.this);
                    builder1.setTitle("Xáo Trộn");
                    builder1.setMessage("Bạn có muốn xáo trộn danh sách không?");

                    builder1.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Collections.shuffle(vocabList);

                            String jsonVocabList = new Gson().toJson(vocabList);

                            // Tạo Intent để mở hoạt động FlashcardActivity
                            Intent intent = new Intent(topic_details.this, word_fill.class);

                            intent.putExtra("topicID",idTopic);
                            intent.putExtra("idUser",idOwner);
                            // Đưa chuỗi JSON vào Intent
                            intent.putExtra("jsonVocabList", jsonVocabList);

                            // Khởi động hoạt động FlashcardActivity
                            startActivity(intent);
                        }
                    });

                    builder1.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Không làm gì cả
                            String jsonVocabList = new Gson().toJson(vocabList);

                            // Tạo Intent để mở hoạt động FlashcardActivity
                            Intent intent = new Intent(topic_details.this, word_fill.class);

                            intent.putExtra("topicID",idTopic);
                            intent.putExtra("idUser",idOwner);
                            // Đưa chuỗi JSON vào Intent
                            intent.putExtra("jsonVocabList", jsonVocabList);

                            // Khởi động hoạt động FlashcardActivity
                            startActivity(intent);
                        }
                    });

                    AlertDialog dialog = builder1.create();
                    dialog.show();


                } else {
                    // Nếu kích thước nhỏ hơn 4, hiển thị dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(topic_details.this);
                    builder.setTitle("Cảnh báo");
                    builder.setMessage("Bạn không đủ số lượng từ để ôn điền từ.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Đóng dialog khi bấm OK
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });
//=============================ôn tập trắc nghiệm=============================
        btn_quizz = findViewById(R.id.btn_quizz);
        btn_quizz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vocabList.size() >= 4) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(topic_details.this);
                    builder1.setTitle("Xáo Trộn");
                    builder1.setMessage("Bạn có muốn xáo trộn danh sách không?");

                    builder1.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Nếu kích thước lớn hơn hoặc bằng 4, chuyển đến Activity mới
                            Intent intent = new Intent(topic_details.this, com.example.englishapp.TracNghiem.class);
                            intent.putExtra("idTopic", idTopic);
                            intent.putExtra("idUser",idOwner);
                            startActivity(intent);
                        }
                    });

                    builder1.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Không làm gì cả
                            // Nếu kích thước lớn hơn hoặc bằng 4, chuyển đến Activity mới
                            Intent intent = new Intent(topic_details.this, com.example.englishapp.TracNghiem.class);
                            intent.putExtra("idTopic", idTopic);
                            intent.putExtra("idUser",idOwner);
                            startActivity(intent);
                        }
                    });

                    AlertDialog dialog = builder1.create();
                    dialog.show();


                } else {
                    // Nếu kích thước nhỏ hơn 4, hiển thị dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(topic_details.this);
                    builder.setTitle("Cảnh báo");
                    builder.setMessage("Bạn không đủ số lượng từ để ôn tập trắc nghiệm.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Đóng dialog khi bấm OK
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });
//=============================Xuất từ vựng===========================
        btn_export = findViewById(R.id.btn_export_vocab);
        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performExportToCSV();
            }
        });
    }
    private void updateRecyclerView() {
        // Gọi hàm để lấy từ vựng và xử lý kết quả
//        getWordsForUserAndTopic(idOwner, idTopic);
        // Cập nhật dữ liệu mới cho adapter và thông báo thay đổi
        vocabAdapter.update();
    }
    private void getWordsForUserAndTopic(String userId, String topicId) {
        vocabViewModel.getTopicsForUser(userId, topicId, new VocabVM.OnTopicsFetchedListener() {
            @Override
            public void onTopicFetched(String topicName, String topicId) {
                // Xử lý khi topic được lấy thành công (nếu cần)
                // Bạn có thể thêm mã xử lý ở đây để hiển thị thông tin topic
//                Toast.makeText(my_word.this,"success",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVocabListFetched(Vocab vocab) {
                // Chờ 3 giây trước khi gán giá trị cho vocabList

                if (!isVocabExists(vocab)) {
                    // Nếu chưa tồn tại, thêm vào vocabList
                    vocabList.add(vocab);

                    // Cập nhật giao diện người dùng (nếu cần)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            vocabAdapter.notifyDataSetChanged(); // Gọi notifyDataSetChanged từ adapter
                            // Hiển thị thông báo thành công
                            // Toast.makeText(my_word.this, "Thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onVocabAllFetched(ArrayList<ArrayList<Vocab>> allVocabLists) {
//                Toast.makeText(flashcard.this, vocab.getTrans(),Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFetchError(Exception e) {
                // Xử lý khi có lỗi trong quá trình lấy dữ liệu
                // Bạn có thể thêm mã xử lý ở đây để thông báo về lỗi
                Toast.makeText(topic_details.this,"error",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean isVocabExists(Vocab vocab) {
        for (Vocab existingVocab : vocabList) {
            // Ở đây, bạn cần xác định cách so sánh giữa các phần tử Vocab, ví dụ, so sánh theo id, word, trans, etc.
            // Dưới đây là một ví dụ sơ bộ kiểm tra theo id
            if (existingVocab.getId().equals(vocab.getId())) {
                return true; // vocab đã tồn tại trong vocabList
            }
        }
        return false; // vocab chưa tồn tại trong vocabList
    }
    private void filter(String text) {
        // Tạo một danh sách mới để lưu trữ kết quả lọc
        ArrayList<Vocab> filteredList = new ArrayList<>();

        // Duyệt qua tất cả thư mục trong danh sách gốc
        for (Vocab item : vocabList) {
            // Kiểm tra xem tên thư mục chứa văn bản tìm kiếm không, không phân biệt hoa thường
            if (item.getWord().toLowerCase().contains(text.toLowerCase())) {
                // Nếu tìm thấy, thêm vào danh sách đã lọc
                filteredList.add(item);
            }
        }

        // Kiểm tra xem danh sách đã lọc có dữ liệu hay không
        if (filteredList.isEmpty()) {
            // Hiển thị thông báo nếu không có kết quả tìm kiếm
            Toast.makeText(topic_details.this, "Không tìm thấy dữ liệu.", Toast.LENGTH_SHORT).show();
        }

        // Cập nhật danh sách trong Adapter với danh sách đã lọc
        vocabAdapter.filterList(filteredList);
    }
    @Override
    public void onVocabDeleted(int position) {
        // Xóa mục từ khỏi vocabList ở vị trí position
        vocabList.remove(position);

        // Cập nhật RecyclerView
        vocabAdapter.notifyItemRemoved(position);
        vocabAdapter.notifyItemRangeChanged(position, vocabList.size());
    }
    private void performExportToCSV() {
        // Mở hộp thoại chọn vị trí lưu trữ
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "vocab_export.csv");
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                // Lấy đường dẫn đã chọn
                Uri uri = data.getData();

                try {
                    // Mở OutputStream để ghi dữ liệu vào tệp đã chọn
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);

                    // Thực hiện xuất CSV như trước, nhưng với outputStream mới
                    CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream));

                    // Thêm header (nếu cần)

                    // Ghi dữ liệu từ vocabList vào CSV
                    for (Vocab vocab : vocabList) {
                        String[] csvData = {vocab.getWord(), vocab.getTrans()};
                        writer.writeNext(csvData);
                    }

                    writer.close();
                    outputStream.close();

                    Toast.makeText(this, "Xuất file CSV thành công", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

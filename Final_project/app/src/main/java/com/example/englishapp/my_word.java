package com.example.englishapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishapp.model.Folder;
import com.example.englishapp.model.Vocab;
import com.example.englishapp.view.VocabAdapter;
import com.example.englishapp.viewmodel.FolderVM;
import com.example.englishapp.viewmodel.VocabVM;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class my_word extends AppCompatActivity {
    private String m_Text = "";
    private List<Vocab> vocabList= new ArrayList<>();
    private List<Vocab> vocabListFav= new ArrayList<>();
    private VocabVM vocabViewModel;
    private VocabAdapter vocabAdapter;
    EditText search;
    FloatingActionButton add_vocab, play_fav;
    String idTopic, idOwner;
    ArrayList<ArrayList<Vocab>> allVocabListstest;
    private Button pc1, pc2, pc3, btn_fav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_word);

        // Khởi tạo VocabVM
        vocabViewModel = new VocabVM();
        Bundle bundle = getIntent().getExtras();
//        idOwner =  bundle.getString("idOwner");
//        idTopic = bundle.getString("idTopic");
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        idOwner = sharedPreferences.getString("idUser", "user_2");

        getWordsForUserAndTopicInBackground(idOwner);
//==================================================bottombar================================================================
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottonnavMyword);
        bottomNavigationView.getMenu().getItem(0).setChecked(false);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);
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
                    return true;
                }else if (item.getItemId() == R.id.gamePage) {
                    startActivity(new Intent(getApplicationContext(), gamePlay.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }

                return false;
            }
        });
//====================================set data cho recycle view===========================

        vocabAdapter = new VocabAdapter(my_word.this, vocabList);
        RecyclerView recyclerView = findViewById(R.id.rcv_Vocab);
        recyclerView.setLayoutManager(new LinearLayoutManager(my_word.this));
        recyclerView.setAdapter(vocabAdapter);
//================================chức năng tìm kiếm================================
        search = findViewById(R.id.edt_search_vovab);
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


        add_vocab = findViewById(R.id.btn_add_vocab);
//        add_vocab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(my_word.this, com.example.englishapp.add_vocab.class);
//                intent.putExtra("idTopic",idTopic);
//                startActivity(intent);
//            }
//        });
//===================================== chức năng lọc theo mức độ từ======================================
        pc1 = findViewById(R.id.btn_process_1);
        pc2 = findViewById(R.id.btn_process_2);
        pc3 = findViewById(R.id.btn_process_3);
        pc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterProcess(1);
            }
        });
        pc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterProcess(2);
            }
        });
        pc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterProcess(3);
            }
        });
//=================================== chức năng show từ yêu thích ========================================
        btn_fav = findViewById(R.id.btn_show_fav);
        btn_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterFavorite();
            }
        });
//=================================== chức năng ôn flashcard từ yêu thích ==================================
        play_fav = findViewById(R.id.btn_play_fav);

        play_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getListFavorite();
                String jsonVocabList = new Gson().toJson(vocabListFav);

                // Tạo Intent để mở hoạt động FlashcardActivity
                Intent intent = new Intent(my_word.this, flashcard.class);

                // Đưa chuỗi JSON vào Intent
                intent.putExtra("jsonVocabList", jsonVocabList);

                // Khởi động hoạt động FlashcardActivity
                startActivity(intent);
            }
        });
    }
    private void updateRecyclerView() {
        // Gọi hàm để lấy từ vựng và xử lý kết quả
//        getWordsForUserAndTopic(idOwner, idTopic);
        // Cập nhật dữ liệu mới cho adapter và thông báo thay đổi
        vocabAdapter.update();
    }
    private void getWordsForUserAndTopic(String userId) {
        vocabViewModel.getAllVocabForUser(userId, new VocabVM.OnTopicsFetchedListener() {
            @Override
            public void onTopicFetched(String topicName, String topicId) {
                // Xử lý khi topic được lấy thành công (nếu cần)
                // Bạn có thể thêm mã xử lý ở đây để hiển thị thông tin topic
//                Toast.makeText(my_word.this,"success",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVocabListFetched(Vocab vocab) {
                // Chờ 3 giây trước khi gán giá trị cho vocabList

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
            @Override
            public void onVocabAllFetched(ArrayList<ArrayList<Vocab>> allVocabLists) {
//                allVocabListstest = allVocabLists;
//                Toast.makeText(my_word.this, allVocabLists.size()+"",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFetchError(Exception e) {
                // Xử lý khi có lỗi trong quá trình lấy dữ liệu
                // Bạn có thể thêm mã xử lý ở đây để thông báo về lỗi
                Toast.makeText(my_word.this,"error",Toast.LENGTH_SHORT).show();
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
            Toast.makeText(my_word.this, "Không tìm thấy dữ liệu.", Toast.LENGTH_SHORT).show();
        }

        // Cập nhật danh sách trong Adapter với danh sách đã lọc
        vocabAdapter.filterList(filteredList);
    }
    private class FetchVocabTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... userIds) {
            String userId = userIds[0];

            vocabViewModel.getAllVocabForUser(userId, new VocabVM.OnTopicsFetchedListener() {
                @Override
                public void onTopicFetched(String topicName, String topicId) {
                    // Xử lý khi topic được lấy thành công (nếu cần)
                }

                @Override
                public void onVocabListFetched(Vocab vocab) {
                    vocabList.add(vocab);

                    // Cập nhật giao diện người dùng (nếu cần)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            vocabAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onVocabAllFetched(ArrayList<ArrayList<Vocab>> allVocabLists) {
                    // Xử lý khi tất cả vocab đã được lấy (nếu cần)
                }

                @Override
                public void onFetchError(Exception e) {
                    // Xử lý khi có lỗi trong quá trình lấy dữ liệu
                }
            });

            return null;
        }
    }
    private void getWordsForUserAndTopicInBackground(String userId) {
        new FetchVocabTask().execute(userId);
    }
    private void filterProcess(int processLevel) {
        // Tạo một danh sách mới để lưu trữ kết quả lọc
        ArrayList<Vocab> filteredList = new ArrayList<>();

        // Duyệt qua tất cả các mục trong danh sách gốc
        for (Vocab item : vocabList) {
            // Kiểm tra xem mức xử lý nằm trong khoảng không
            if (isProcessLevelInRange(item, processLevel)) {
                // Nếu mức xử lý nằm trong khoảng, thêm vào danh sách đã lọc
                filteredList.add(item);
            }
        }

        // Kiểm tra xem danh sách đã lọc có dữ liệu hay không
        if (filteredList.isEmpty()) {
            // Hiển thị thông báo nếu không có kết quả lọc
            Toast.makeText(my_word.this, "Không tìm thấy dữ liệu.", Toast.LENGTH_SHORT).show();
        }

        // Cập nhật danh sách trong Adapter với danh sách đã lọc
        vocabAdapter.filterList(filteredList);
    }
    private boolean isProcessLevelInRange(Vocab item, int processLevel) {
        // Lấy giá trị process từ đối tượng Vocab
        int processValue = Integer.parseInt(item.getProcess());

        // Xác định khoảng giá trị process cho mỗi mức
        int lowerBound;
        int upperBound;

        if (processLevel == 1) {
            lowerBound = 0;
            upperBound = 3;
        } else if (processLevel == 2) {
            lowerBound = 4;
            upperBound = 7;
        } else if (processLevel == 3) {
            lowerBound = 8;
            upperBound = 10;
        } else {
            // Nếu mức không hợp lệ, trả về false
            return false;
        }

        // Kiểm tra xem giá trị process có nằm trong khoảng không
        return processValue >= lowerBound && processValue <= upperBound;
    }
    private void filterFavorite() {
        // Tạo một danh sách mới để lưu trữ kết quả lọc
        ArrayList<Vocab> filteredList = new ArrayList<>();

        // Duyệt qua tất cả các mục trong danh sách gốc
        for (Vocab item : vocabList) {
            // Kiểm tra xem trường favorite có giá trị "true" không
            if (item.getFav() != null && item.getFav().equalsIgnoreCase("true")) {
                // Nếu favorite là "true", thêm vào danh sách đã lọc
                vocabListFav.add(item);
                filteredList.add(item);
            }
        }

        // Kiểm tra xem danh sách đã lọc có dữ liệu hay không


        // Cập nhật danh sách trong Adapter với danh sách đã lọc
        vocabAdapter.filterList(filteredList);
    }
    private void getListFavorite() {
        // Tạo một danh sách mới để lưu trữ kết quả lọc
        ArrayList<Vocab> filteredList = new ArrayList<>();
        // Duyệt qua tất cả các mục trong danh sách gốc
        for (Vocab item : vocabList) {
            // Kiểm tra xem trường favorite có giá trị "true" không
            if (item.getFav() != null && item.getFav().equalsIgnoreCase("true")) {
                // Nếu favorite là "true", thêm vào danh sách đã lọc
                vocabListFav.add(item);
            }
        }
        // Kiểm tra xem danh sách đã lọc có dữ liệu hay không

    }
    @Override
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

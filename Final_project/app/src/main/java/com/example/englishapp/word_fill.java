package com.example.englishapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.englishapp.model.Vocab;
import com.example.englishapp.viewmodel.VocabVM;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class word_fill extends AppCompatActivity {
    private VocabVM vocabViewModel;
    private ProgressBar progressBar;
    private List<Vocab> vocabList= new ArrayList<>();
    private Button submit;
    private AlertDialog customDialog;
    private int currentVocabIndex = 0;
    private Vocab currentVocab;
    TextToSpeech t1;
    String topicId = "topic_1";
    String idUser,mode;

    ImageButton btn_back;

    private int P_correct =0,P_wrong =0;
    private List<String> wrongAnswer = new ArrayList<>();
    private List<String> rightAnswer = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_fill);
        submit = findViewById(R.id.btn_Submit);
        progressBar = findViewById(R.id.viewProgressFW);
        vocabViewModel = new VocabVM();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        mode = sharedPreferences.getString("mode","Default");
        topicId = getIntent().getStringExtra("topicID");
        idUser = getIntent().getStringExtra("idUser");
        String jsonVocabList = getIntent().getStringExtra("jsonVocabList");

        // Chuyển chuỗi JSON thành danh sách
        vocabList = new Gson().fromJson(jsonVocabList, new TypeToken<List<Vocab>>(){}.getType());
        ///check conditions
        if(mode.equals("Random"))
        {
            vocabList = getRandomVocab(vocabList);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent để mở MainActivity
                showFlashcardAtIndex(0);
//                finish();
            }
        }, 2000);
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(word_fill.this, currentVocabIndex+"",Toast.LENGTH_SHORT).show();
                if (currentVocabIndex == vocabList.size()-1) {
                    Intent intent = new Intent(word_fill.this, answer_check.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("P_correct", (ArrayList<String>) rightAnswer);
                    bundle.putStringArrayList("P_wrong", (ArrayList<String>) wrongAnswer);
                    bundle.putString("idTopic",topicId);
                    bundle.putString("idUser",idUser);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
//                    Toast.makeText()
                } else {
                    // Không phải từ cuối cùng, thực hiện kiểm tra và cập nhật thông tin như bình thường
                    // ...
                    EditText editText = findViewById(R.id.edt_fill_Word);
                    String userWord = editText.getText().toString();

                    // Lấy từ tiếng Anh đúng tại vị trí hiện tại
                    String correctWord = vocabList.get(currentVocabIndex).getWord();

                    // Kiểm tra từ người dùng có khớp với từ đúng không
                    boolean isCorrect = vocabViewModel.checkWordFill(userWord, vocabList.get(currentVocabIndex).getWord());

                    // Hiển thị kết quả
//                    Toast.makeText(word_fill.this, isCorrect ? "Đúng!" : "Sai!", Toast.LENGTH_SHORT).show();


                    if (isCorrect) {
                        P_correct = P_correct +1;
                        rightAnswer.add(currentVocab.getWord().toString());
                        int currentProcessInt = Integer.parseInt(currentVocab.getProcess());

                        // Kiểm tra nếu giá trị hiện tại là 9 thì không cộng nữa
                        if (currentProcessInt <= 9) {
                            // Cộng lên 1
                            String newProcess = String.valueOf(currentProcessInt + 1);
                            currentVocab.setProcess(newProcess);
                            // Tiếp tục với các bước khác...

                        } else {

                        }
                        vocabViewModel.updateVocabForUser(topicId, currentVocab.getId(),currentVocab.getProcess() , new VocabVM.OnVocabUpdatedListener() {
                            @Override
                            public void onVocabUpdated() {
                                // Xử lý khi cập nhật thành công
                            }

                            @Override
                            public void onUpdateError(Exception e) {
                                // Xử lý khi có lỗi xảy ra trong quá trình cập nhật
                            }
                        });
                        showCustomDialog(R.layout.dialog_correct,"");
                        editText.setText("");
                    } else {
                        P_wrong = P_wrong+1;
                        wrongAnswer.add(currentVocab.getWord().toString());
                        int currentProcessInt = Integer.parseInt(currentVocab.getProcess());

                        // Kiểm tra nếu giá trị hiện tại là 0 thì không trừ nữa
                        if (currentProcessInt > 0) {
                            // Trừ đi 1
                            String newProcess = String.valueOf(currentProcessInt - 1);
                            currentVocab.setProcess(newProcess);
                            // Tiếp tục với các bước khác...

                        } else {
                            // Nếu giá trị là 0, không thực hiện trừ nữa và thực hiện xử lý khác hoặc thông báo
                        }
                        vocabViewModel.updateVocabForUser(topicId, currentVocab.getId(),currentVocab.getProcess() , new VocabVM.OnVocabUpdatedListener() {
                            @Override
                            public void onVocabUpdated() {
                                // Xử lý khi cập nhật thành công
                            }

                            @Override
                            public void onUpdateError(Exception e) {
                                // Xử lý khi có lỗi xảy ra trong quá trình cập nhật
                            }
                        });
//                    TextView key = findViewById(R.id.txtKeyAnsFill);
//                    key.setText(currentVocab.getWord());
                        showCustomDialog(R.layout.dialog_wrong,currentVocab.getWord());
                        editText.setText("");
                    }
                }
                // Lấy nội dung từ ô điền từ người dùng
//                Intent intent = new Intent(word_fill.this, answer_check.class);
//                startActivity(intent);
//                finish();
            }
        });
        btn_back = findViewById(R.id.btn_back_wf);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(word_fill.this);
                builder.setTitle("Xác nhận thoát");
                builder.setMessage("Bạn có muốn thoát khỏi bài làm không?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Thực hiện các hành động khi chọn "Có"
                        finish();
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Đóng dialog khi chọn "Không"
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }
    private void updateProgressBar() {
        // Tính toán giá trị cho ProgressBar dựa trên currentVocabIndex và kích thước của danh sách
        int progress = (int) ((float) currentVocabIndex / (float) vocabList.size() * 100);
        progressBar.setProgress(progress);
    }
    private void showFlashcardAtIndex(int index) {
        // Đảm bảo index nằm trong phạm vi của danh sách từ vựng
        if (index >= 0 && index < vocabList.size()) {
            // Hiển thị từ vựng tại vị trí index
            currentVocabIndex = index;
            showFlashcard();
        } else {
            // Xử lý khi index vượt quá biên
            // Có thể làm gì đó như thông báo hoặc không làm gì cả
        }
    }
    private void showFlashcard() {
        // Lấy từ vựng tại vị trí hiện tại
        currentVocab = vocabList.get(currentVocabIndex);

        // Hiển thị thông tin từ vựng trên flashcard
        // Ví dụ: Nếu bạn có TextView để hiển thị từ và nghĩa

        TextView txtTrans = findViewById(R.id.txtTransFill);
        ImageButton btnSound = findViewById(R.id.btnSoundBackFill);

        txtTrans.setText(currentVocab.getTrans());
        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t1.speak(currentVocab.getWord(), TextToSpeech.QUEUE_FLUSH, null);
//                Toast.makeText(word_fill.this, currentVocab.getWord(), Toast.LENGTH_SHORT).show();
            }
        });
        // Chuyển về mặt trước khi hiển thị

    }
    private void showCustomDialog(int layoutResId, String answerText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(layoutResId, null);
        builder.setView(dialogView);

        if (layoutResId == R.layout.dialog_wrong) {
            // Nếu là layout sai, thêm dòng mã để hiển thị đáp án đúng
            TextView txtKeyAnsFill = dialogView.findViewById(R.id.txtKeyAnsFill);
            txtKeyAnsFill.setText(answerText);
        }

        // Thêm sự kiện cho nút trong dialog
        Button nextButton = dialogView.findViewById(R.id.btnNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang từ tiếp theo sau khi bấm nút trong dialog
                showFlashcardAtIndex(currentVocabIndex + 1);
                updateProgressBar();

                // Tắt dialog
                customDialog.dismiss();
            }
        });

        // Hiển thị dialog
        customDialog = builder.create();
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.show();
    }


    private void getWordsForUserAndTopic(String userId, String topicId) {
        vocabViewModel.getTopicsForUser(userId, topicId, new VocabVM.OnTopicsFetchedListener() {
            @Override
            public void onTopicFetched(String topicName, String topicId) {

            }

            @Override
            public void onVocabListFetched(Vocab vocab) {
                vocabList.add(vocab);
//                Toast.makeText(flashcard.this, vocab.getTrans(),Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onVocabAllFetched(ArrayList<ArrayList<Vocab>> allVocabLists) {
//                Toast.makeText(flashcard.this, vocab.getTrans(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFetchError(Exception e) {
                // Xử lý khi có lỗi trong quá trình lấy dữ liệu
                // Bạn có thể thêm mã xử lý ở đây để thông báo về lỗi
                Toast.makeText(word_fill.this,"error",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public List<Vocab> getRandomVocab(List<Vocab> allVocab){
        List<Vocab> randomVocab = new ArrayList<>();
        Random random = new Random();

        // Select  random question from the list
        while (randomVocab.size() < allVocab.size()) {
            int randomIndex = random.nextInt(allVocab.size());
            Vocab question = allVocab.get(randomIndex);

            if (!randomVocab.contains(question)) {
                randomVocab.add(question);
            }
        }
        return randomVocab;
    }
}
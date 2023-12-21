package com.example.englishapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.example.englishapp.model.Vocab;
import com.example.englishapp.viewmodel.VocabVM;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class flashcard extends AppCompatActivity {
    private EasyFlipView flashCardViewAnimator;
    private ProgressBar progressBar;
    private ImageButton flipButton, btnFav;
    private boolean isFront = true;
    private List<Vocab> vocabList= new ArrayList<>();
    private VocabVM vocabViewModel;
    private ImageButton btnPrevious, btnNext, btnSoundB, btnSoundF, btnBack;
    private int currentVocabIndex = 0;
    TextToSpeech t1;
    String topicID,game,mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);
        flashCardViewAnimator = findViewById(R.id.flashCardViewFlipper);
        flipButton = findViewById(R.id.btnFlip);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnFav = findViewById(R.id.btnFavVocab);
        btnSoundB = findViewById(R.id.btnSoundBack);
        btnSoundF = findViewById(R.id.btnSoundFront);
        progressBar = findViewById(R.id.viewProgressFC);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        game = sharedPreferences.getString("game","quiz");
        mode = sharedPreferences.getString("mode","Default");
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        flipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashCardViewAnimator.flipTheView();
                isFront = !isFront;
            }
        });
        vocabViewModel = new VocabVM();
//        getWordsForUserAndTopic("user_2", "topic_1");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent để mở MainActivity
//                Toast.makeText(flashcard.this,vocabList.size()+"",Toast.LENGTH_SHORT).show();
                showFlashcardAtIndex(0);
                updateProgressBar();
//                finish();
            }
        }, 2000);

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousFlashcard();
            }
        });

        // Sự kiện nút Next
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextFlashcard();
            }
        });
        topicID = getIntent().getStringExtra("topicID");
        // Trong FlashcardActivity
        String jsonVocabList = getIntent().getStringExtra("jsonVocabList");

        // Chuyển chuỗi JSON thành danh sách
        vocabList = new Gson().fromJson(jsonVocabList, new TypeToken<List<Vocab>>(){}.getType());
        if(mode.equals("Random"))
        {
            vocabList = getRandomVocab(vocabList);
        }
//        Toast.makeText(this, vocabList.size()+"", Toast.LENGTH_SHORT).show();
        btnBack = findViewById(R.id.btn_back_flashcard);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(flashcard.this);
                builder.setTitle("Xác nhận thoát");
                builder.setMessage("Bạn có muốn thoát khỏi bài ôn không?");
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
    private void showNextFlashcard() {
        // Tăng vị trí hiện tại lên 1 và hiển thị từ vựng mới
        currentVocabIndex++;
        if (currentVocabIndex >= vocabList.size()) {
            // Nếu vượt quá ranh giới của danh sách, giữ nguyên ở vị trí tối đa
            showCompletionDialog();
            currentVocabIndex = vocabList.size() - 1;
        }
        showFlashcardAtIndex(currentVocabIndex);

        // Cập nhật giá trị ProgressBar
        updateProgressBar();
    }

    private void showPreviousFlashcard() {
        // Giảm vị trí hiện tại đi 1 và hiển thị từ vựng mới
        currentVocabIndex--;
        if (currentVocabIndex < 0) {
            // Nếu vượt quá ranh giới của danh sách, giữ nguyên ở vị trí tối thiểu
            currentVocabIndex = 0;
        }
        showFlashcardAtIndex(currentVocabIndex);

        // Cập nhật giá trị ProgressBar
        updateProgressBar();
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
    private void updateProgressBar() {
        // Tính toán giá trị cho ProgressBar dựa trên currentVocabIndex và kích thước của danh sách
        int progress = (int) ((float) currentVocabIndex / (float) vocabList.size() * 100);
        progressBar.setProgress(progress);
    }
    private void showFlashcard() {
        // Lấy từ vựng tại vị trí hiện tại
        Vocab currentVocab = vocabList.get(currentVocabIndex);

        // Hiển thị thông tin từ vựng trên flashcard
        // Ví dụ: Nếu bạn có TextView để hiển thị từ và nghĩa

        TextView txtWordBack = findViewById(R.id.txtWordBack);
        TextView txtWordFront = findViewById(R.id.txtWordFront);
        TextView txtTrans = findViewById(R.id.txtTrans);
        ImageView imageView = findViewById(R.id.imgFlash);

        // set trạng thái cho nút fav
        if (currentVocab.getFav().equals("true")) {
            // Nếu fav là true, đặt màu nền của btnFav là màu xanh
            btnFav.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EFAFAF")));
            btnFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnFav.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E6E2E2")));
                    currentVocab.setFav("");
                    vocabViewModel.updateVocabFavForUser(topicID,currentVocab.getId(), "",new VocabVM.OnVocabUpdatedListener() {
                        @Override
                        public void onVocabUpdated() {
                            // Xử lý khi cập nhật thành công
                        }

                        @Override
                        public void onUpdateError(Exception e) {
                            // Xử lý khi có lỗi xảy ra trong quá trình cập nhật
                        }
                    });
                }
            });


        }
        else {
            // Nếu fav không có giá trị hoặc là false, giữ màu nền của btnFav nguyên
            // Bạn có thể đặt màu nền mặc định ở đây
            btnFav.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E6E2E2")));
            btnFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnFav.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EFAFAF")));
                    currentVocab.setFav("true");
                    vocabViewModel.updateVocabFavForUser(topicID,currentVocab.getId(), "true",new VocabVM.OnVocabUpdatedListener() {
                        @Override
                        public void onVocabUpdated() {
                            // Xử lý khi cập nhật thành công
                        }

                        @Override
                        public void onUpdateError(Exception e) {
                            // Xử lý khi có lỗi xảy ra trong quá trình cập nhật
                        }
                    });
                }
            });

        }

        txtWordFront.setText(currentVocab.getWord());
        txtWordBack.setText(currentVocab.getWord());
        txtTrans.setText(currentVocab.getTrans());
        loadAndDisplayImageFromFirebaseStorage(currentVocab.getImage(), imageView);

        // Chuyển về mặt trước khi hiển thị
        if (!isFront) {
            flashCardViewAnimator.flipTheView();
            isFront = true;
        }
        btnSoundF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t1.speak(currentVocab.getWord(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        btnSoundB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t1.speak(currentVocab.getWord(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

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
                Toast.makeText(flashcard.this,"error",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showCompletionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hoàn thành");
        builder.setMessage("Bạn đã hoàn thành ôn tập");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Thực hiện hành động sau khi nhấn "OK"
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Người dùng chọn "Cancel", không làm gì cả hoặc có thể thêm mã tương ứng
                dialogInterface.dismiss();
            }
        });

        builder.show();
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
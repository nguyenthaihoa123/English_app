package com.example.englishapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.englishapp.model.Question;
import com.example.englishapp.view.FolderAdapter;
import com.example.englishapp.view.MultichoiceAdapter;
import com.example.englishapp.viewmodel.MultichoiceVM;
import com.example.englishapp.model.Vocab;
import com.example.englishapp.viewmodel.RankVM;
import com.example.englishapp.viewmodel.VocabVM;

import java.util.ArrayList;
import java.util.List;

public class TracNghiem extends AppCompatActivity {
    private boolean isActivityFinished = false;
    VocabVM vocabVM = new VocabVM();
    RankVM rankVM = new RankVM();
    private ProgressBar progressBar,progressLine;
    private long startTime;
    private int totalScore = 0;
    private int correctPoints = 10;
    private int wrongPoints = 0;
    private CountDownTimer countDownTimer;
    private TextView progressText,numQuestion,txt_question;
    private Button btn_submit;
    private ImageButton btn_Back;
    private RadioButton btn_A,btn_B,btn_C,btn_D;

    private RadioGroup radioGroup;
    private List<String> wrongAnswer = new ArrayList<>();
    private List<String> rightAnswer = new ArrayList<>();

    public List<Question> listQuestion = new ArrayList<>();
    public List<RadioButton> listBtn = new ArrayList<>();
    private RadioButton rightButton;
    private int currentIndex=0;
    private  boolean isSubmit = false;
    private  Vocab vocabSelected = new Vocab("","");
    private  Vocab currentVocab;

    private RadioButton buttonSelected ;
    MultichoiceVM multichoiceVM = new MultichoiceVM();
    private MediaPlayer correctChoice;
    private MediaPlayer wrongChoice;
    String idTopic,idUser,mode,game;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trac_nghiem);
        Intent receivedIntent = getIntent();
        idTopic = receivedIntent.getStringExtra("idTopic");
//        mode = receivedIntent.getStringExtra("mode");
//        game = receivedIntent.getStringExtra("game");
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString("idUser", "user_2");
        game = sharedPreferences.getString("game","quiz");
        mode = sharedPreferences.getString("mode","Default");

        setupUI();
        fetchData(idTopic);
        ///start time completely
        startTime = System.currentTimeMillis();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSubmit=true;
                checkAnswer();
            }
        });
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TracNghiem.this);
                builder.setTitle("Xác nhận thoát");
                builder.setMessage("Bạn có muốn thoát khỏi bài ôn không?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Thực hiện các hành động khi chọn "Có"
                        isActivityFinished = true;
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

    public void checkAnswer() {
        /// get selected choice at the current question index {0,1,2...}
        getSelectedChoice(listQuestion.get(currentIndex-1));
        if(buttonSelected!=null)
        {   /// check correct choice
            if(buttonSelected==rightButton) { //can set conditions with vocab
//                         Toast.makeText(TracNghiem.this, "Answer is correct", Toast.LENGTH_SHORT).show();
                setRightButton(buttonSelected);
                ///set sound
                correctChoice.start();
                caculateScore(correctPoints);
                rightAnswer.add(buttonSelected.getText().toString());
                UpdateVocab(currentVocab,"Right");
            } else{
//                       check wrong answer
                setWrongButton(buttonSelected);
                setRightButton(rightButton);
                //set sound
                wrongChoice.start();
                caculateScore(wrongPoints);
                wrongAnswer.add(buttonSelected.getText().toString());
                UpdateVocab(currentVocab,"Wrong");
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                        Toast.makeText(TracNghiem.this,"click",Toast.LENGTH_SHORT).show();

                    if(currentIndex == listQuestion.size())
                    {
                        finishQuizz();
                        // cancel timer event

                    }
                    else
                    {
                        showNextQuestion(currentIndex++);
                        isSubmit= false;
                    }

                }
            }, 2000);
        }else{
            // set condition when submit or un submit butotn
            if(isSubmit){
                Toast.makeText(TracNghiem.this,"Please select an answer",Toast.LENGTH_SHORT).show();
            }else{
                setRightButton(rightButton);
                wrongAnswer.add(rightButton.getText().toString());
                wrongChoice.start();
                UpdateVocab(currentVocab,"Wrong");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(currentIndex == listQuestion.size())
                        {
                            finishQuizz();
                        }
                        else
                        {
                            showNextQuestion(currentIndex++);
                        }
                    }
                }, 1000);
            }
        }
    }
    private void fetchData(String id)
    {
        //========================================//
        multichoiceVM.getQuestionAndAnswer(idTopic,mode, new MultichoiceVM.OnVocabCountFetchedListener() {
            @Override
            public void onVocabCountFetched(ArrayList<Vocab> listAnswer) {
            }
            @Override
            public void onQuestionFetched(List<Question> listquestion) {
                TracNghiem.this.listQuestion=listquestion;
//                Toast.makeText(TracNghiem.this,""+ listQuestion.get(currentIndex).getCorrectAnswer().getWord(),Toast.LENGTH_SHORT).show();
                showMultichoice(currentIndex++);
                startTimer();
            }

            @Override
            public void onFetchError(Exception e) {
                Toast.makeText(TracNghiem.this,"error",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setupUI(){
        progressText = findViewById(R.id.progress_text);
        progressBar = findViewById(R.id.progress_bar);
        progressLine = findViewById(R.id.progress_line);
        numQuestion = findViewById(R.id.txt_numQuestion);
        btn_submit = findViewById(R.id.btn_Submit);
        txt_question = findViewById(R.id.txt_Question);
        radioGroup = findViewById(R.id.choicesGroup);
        btn_Back = findViewById(R.id.btn_back);
        btn_A = findViewById(R.id.choice_A);
        btn_B = findViewById(R.id.choice_B);
        btn_C = findViewById(R.id.choice_C);
        btn_D = findViewById(R.id.choice_D);
        listBtn.add(btn_A);
        listBtn.add(btn_B);
        listBtn.add(btn_C);
        listBtn.add(btn_D);
        //=================//set sound
        correctChoice = MediaPlayer.create(this,R.raw.right);
        wrongChoice = MediaPlayer.create(this,R.raw.wrong);
    }
    private void startTimer() {
        countDownTimer = new CountDownTimer(10000, 1000) { // 60 seconds (60,000 milliseconds) with tick interval of 1 second (1000 milliseconds)
            @Override
            public void onTick(long millisUntilFinished) {
                // Update progress bar with the remaining time
                progressBar.setProgress((int) millisUntilFinished/1000);
                progressText.setText(String.valueOf((int) millisUntilFinished/1000));
            }
//          when the timer finish, show correct answer and change to next question
            @Override
            public void onFinish() {

                if (!isActivityFinished) {
                    if (!isSubmit) {
                        checkAnswer();
                    }
                    progressBar.setProgress(0); // Reset progress to zero when the timer finishes
                }
            }
        };

        countDownTimer.start(); // Start the countdown timer
    }
    private void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            startTimer();
        }
    }

    private  void showMultichoice(int currentIndex) {
        if (listQuestion != null && !listQuestion.isEmpty()) {
//            rightButton=null;
            Question question = listQuestion.get(currentIndex);
            int currentQuestionNumber = currentIndex + 1;
            numQuestion.setText("Question " + currentQuestionNumber + " of " + listQuestion.size());
            /////
            int progress = (int) (((currentQuestionNumber-1) / (float) listQuestion.size()) * 100);
            progressLine.setProgress(progress);
            ////=============//
            vocabVM.getVocabBasedString(question.getCorrectAnswer().getWord(), idTopic, new VocabVM.OnVocabString() {
                @Override
                public void onVocabFetch(Vocab vocab) {
                    currentVocab = vocab;
                }

                @Override
                public void onFetchError(Exception e) {

                }
            });
            txt_question.setText(question.getQuestionText());
            int index = 0;
            for (RadioButton button : listBtn) {
                String choiceText = question.getChoices().get(index).getWord();
                button.setText(choiceText);
                if (choiceText.equals(question.getCorrectAnswer().getWord())) {
                    rightButton = button;
                }
                index++;
            }

        }else{
            Toast.makeText(TracNghiem.this, "No questions available", Toast.LENGTH_SHORT).show();
        }
//
    }
    private void showNextQuestion(int currentIndex)
    {
        resetTimer();
        radioGroup.clearCheck();
        setNormalButton();
//      onDestroy();
        showMultichoice(currentIndex);

    }

    private void finishQuizz(){
//        showMultichoice(listQuestion.size()-1);
        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        // Convert the duration to a  seconds or minutes
        long seconds = durationMillis / 1000;
        long minutes = seconds / 60;
        Toast.makeText(TracNghiem.this,"Score="+totalScore+ "End"+ minutes+"p"+seconds+"s", Toast.LENGTH_SHORT).show();
        ///// update score and time in firestore
//        multichoiceVM.setQuizzResult(seconds, totalScore, new MultichoiceVM.OnResultUpdateListener() {
//            @Override
//            public void onQuizzUpdate() {}
//            @Override
//            public void onUpdateError(Exception e) {}
//        });
        rankVM.SetQuizResult(seconds, totalScore, idUser, new RankVM.onResultQuiz() {
            @Override
            public void onQuizResult() {
                Toast.makeText(TracNghiem.this, "Rank successfully", Toast.LENGTH_SHORT).show();
                navigateToAnswerCheckActivity();
            }

            @Override
            public void onUpdateError(Exception e) {
                Toast.makeText(TracNghiem.this, "Rank failed"+e, Toast.LENGTH_SHORT).show();
                navigateToAnswerCheckActivity();
            }
        });

    }
    private void navigateToAnswerCheckActivity() {
        Intent intent = new Intent(TracNghiem.this, answer_check.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("P_correct", (ArrayList<String>) rightAnswer);
        bundle.putStringArrayList("P_wrong", (ArrayList<String>) wrongAnswer);
        bundle.putString("game", game);
        bundle.putString("idTopic", idTopic);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void getSelectedChoice(Question q)
    {
        buttonSelected=null;
        vocabSelected=null;
        if (btn_A.isChecked()) {
            vocabSelected = q.getChoices().get(0);
            buttonSelected=btn_A;
        } else if (btn_B.isChecked()) {
            vocabSelected = q.getChoices().get(1);
            buttonSelected=btn_B;
        } else if (btn_C.isChecked()) {
            vocabSelected = q.getChoices().get(2);
            buttonSelected=btn_C;
        } else if (btn_D.isChecked()) {
            vocabSelected = q.getChoices().get(3);
            buttonSelected=btn_D;
        } else {
            // None of the RadioButtons are checked
        }
    }

    private void setStateButton(RadioButton rightButton) {
        for (RadioButton btn : listBtn) {
            btn.setButtonDrawable(getResources().getDrawable(R.drawable.wrong_answer));
        }
    }
    private void setNormalButton() {
        for(RadioButton btn : listBtn)
        {
            btn.setButtonDrawable(getResources().getDrawable(R.drawable.radiobutton_event_tn));
            btn.setBackground(getResources().getDrawable(R.drawable.border_stroke_tn));
        }
    }
    private  void setWrongButton(RadioButton btn){
        btn.setButtonDrawable(getResources().getDrawable(R.drawable.wrong_choice));
        btn.setBackground(getResources().getDrawable(R.drawable.background_wrong_choice));

    }
    private  void setRightButton(RadioButton btn){
        btn.setButtonDrawable(getResources().getDrawable(R.drawable.right_choice));
        btn.setBackground(getResources().getDrawable(R.drawable.background_right_choice));

    }

    private void caculateScore(int point)
    {
        totalScore+=point;
        totalScore = (totalScore<0) ? 0: totalScore;
    }


    public void UpdateVocab(Vocab vocab,String type)
    {
            int currentProcessInt = Integer.parseInt(vocab.getProcess());
            if(type.equals("Right"))
            {
                if (currentProcessInt <= 9) {
                    // Cộng lên 1
                    String newProcess = String.valueOf(currentProcessInt + 1);
                    vocab.setProcess(newProcess);
                    // Tiếp tục với các bước khác...
                }
            }
            else
            {
                if (currentProcessInt > 0) {
                    // Trừ đi 1
                    String newProcess = String.valueOf(currentProcessInt - 1);
                    vocab.setProcess(newProcess);
                    // Tiếp tục với các bước khác...

                }
            }
            // Kiểm tra nếu giá trị hiện tại là 9 thì không cộng nữa

            vocabVM.updateVocabForUser(idTopic, vocab.getId(),vocab.getProcess() , new VocabVM.OnVocabUpdatedListener() {
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

}
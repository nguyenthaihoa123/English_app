package com.example.englishapp.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.englishapp.model.Question;
import com.example.englishapp.model.Quizz;
import com.example.englishapp.model.Vocab;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MultichoiceVM {
    List<Question> listQuestion = new ArrayList<Question>();
//    List<Vocab> listVocab = new ArrayList<Vocab>();
    List<Vocab> listChoice = new ArrayList<>();


    public void getQuestionAndAnswer( String id_Topic,String mode, final OnVocabCountFetchedListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Reference to the Vocabulary collection for the specified topic
        CollectionReference vocabCollection = db.collection("Topic").document(id_Topic).collection("Vocab");

        /// get number of question and list of choice  == >
        vocabCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ///get list vocab
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Vocab vocab =new Vocab(document.getString("word"),document.getString("trans"));
//                      /// add vocab to listvocab
                        listChoice.add(vocab);
                    }
                    ///check conditions if equal with "random" or "default"
                    if(mode.equals("Random"))
                    {
                        listChoice = getRandomQuestion(listChoice);
                    }
                    for(int i = 0; i< listChoice.size() ; i++)
                    {
                        Vocab vocab = listChoice.get(i);
                        List<Vocab> choices = new ArrayList<>();
                        String questionText = "Đâu là nghĩa của "+ vocab.getTrans()+ "?";
                        ///// get choices
                        choices = getRandomFourChoices(listChoice,vocab);
                        ///add data question into list question
                        Question question = new Question(questionText,choices,vocab);

                        listQuestion.add(question);
                    }
                    listener.onQuestionFetched(listQuestion);
//
                } else {
                    // Handle an error if needed
                    listener.onFetchError(task.getException());
                }
            }
        });

    }
    // select four answers included 3 wrong and 1 right answer
    public List<Vocab> getRandomFourChoices(List<Vocab> allChoices,Vocab rightChoice) {
        List<Vocab> randomChoices = new ArrayList<>();
        Random random = new Random();

        // Select 3 random choices from the list
        for (int i = 0; i < 3; i++) {
            int randomIndex = random.nextInt(allChoices.size());
            Vocab choice = allChoices.get(randomIndex);

            // Avoid duplicate choices
            if (!randomChoices.contains(choice)&& !choice.equals(rightChoice)) {
                randomChoices.add(choice);
            } else {
                i--; // If a duplicate is found, decrement i to try selecting another choice
            }
        }
        // add right choice to random index
        randomChoices.add(random.nextInt(3), rightChoice);
        return randomChoices;
    }
    /////random question whenever mode = random
    public List<Vocab> getRandomQuestion(List<Vocab> allQuestion){
        List<Vocab> randomQuestion = new ArrayList<>();
        Random random = new Random();

        // Select  random question from the list
        while (randomQuestion.size() < allQuestion.size()) {
            int randomIndex = random.nextInt(allQuestion.size());
            Vocab question = allQuestion.get(randomIndex);

            if (!randomQuestion.contains(question)) {
                randomQuestion.add(question);
            }
        }
        return randomQuestion;
    }


    public void setQuizzResult(long time, int score, final OnResultUpdateListener listener){
        Quizz quizz = new Quizz(score,time);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Reference to the Vocabulary collection for the specified topic
        DocumentReference quizzDocument = db.collection("Quizz").document("quizz_1");

        Map<String, Object> quizzData = new HashMap<>();
        quizzData.put("score", score);
        quizzData.put("time", time);


        // Execute update quizz data
        quizzDocument.update(quizzData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Xử lý cập nhật thành công
                        listener.onQuizzUpdate();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý lỗi khi cập nhật
                        listener.onUpdateError(e);
                    }
                });
    }

    public void getVocabBasesList(ArrayList<String> listvocab,String id_Topic, final OnCheckAnswer listener)
    {
        List<Vocab> listAnswer = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference vocabDocRef = db.collection("Topic").document(id_Topic).collection("Vocab");
        vocabDocRef.whereIn("word", Arrays.asList(listvocab))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle the error
                            listener.onFetchError(error);
                            return;
                        }
                        for (QueryDocumentSnapshot vocabDocument : value) {
                            String Id = vocabDocument.getString("id");
                            String Name = vocabDocument.getString("word");
                            String trans = vocabDocument.getString("trans");
                            String img = vocabDocument.getString("image");
                            String process = vocabDocument.getString("process");
                            String fav = vocabDocument.getString("favourite");
                            Vocab vocabulary = new Vocab(Id, Name, trans, img, process, fav);
                            listAnswer.add(vocabulary);
                        }
                        listener.onVocabFetch((ArrayList<Vocab>) listAnswer);
                    }
                });
    }



    public interface OnVocabCountFetchedListener {
        void onVocabCountFetched(ArrayList<Vocab> listAnswer);
        void onQuestionFetched(List<Question> listQuestion);
        void onFetchError(Exception e);
    }

    public interface  OnResultUpdateListener{
        void onQuizzUpdate();
        void onUpdateError(Exception e);
    }

    public interface  OnCheckAnswer{
        void onVocabFetch(ArrayList<Vocab> listAnswer);
        void onFetchError(Exception e);
    }


}

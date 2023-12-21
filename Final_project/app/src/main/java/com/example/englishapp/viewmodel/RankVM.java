package com.example.englishapp.viewmodel;

import androidx.annotation.NonNull;

import com.example.englishapp.model.Quizz;
import com.example.englishapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RankVM {
    public interface onResultQuiz{
        void onQuizResult();
        void onUpdateError(Exception e);
    }
    public interface  onNewUser{
        void onCompleteUser();
        void onFetchError(Exception e);
    }
    public  interface  onScoreForTime{
        void onScoreFetch(Map<User,Integer> userScore);
        void onFetchError(Exception e);
    }

    public void GetUserForTime(Date startTime, Date endTime, final onScoreForTime listener)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference rankCollection = db.collection("Rank");

        final Map<User, Integer> userScores = new HashMap<>();
        rankCollection.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {

                            for(QueryDocumentSnapshot document : task.getResult())
                            {
                                String userID = document.getId();
                                final int[] totalScore = {0};
                                calculateTotalScoreForTimeFrame(userID, startTime, endTime, new ScoreFetchListener() {
                                    @Override
                                    public void onScoreFetched(int score) {
                                        totalScore[0] = score;
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        listener.onFetchError(e);
                                    }
                                });
                                getUserByID(userID,0,0, new OnUserFetchListener() {
                                    @Override
                                    public void onUserFetched(User user) {
                                        userScores.put(user, totalScore[0]);
                                        if (userScores.size() == task.getResult().size()) {
                                            // Sort userScores by values in descending order
                                            Map<User, Integer> sortedScores = sortByValueDescending(userScores);
                                            listener.onScoreFetch(sortedScores);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        listener.onFetchError(e);
                                    }
                                });



                            }
                        }else {
                            listener.onFetchError(task.getException());
                        }
                    }
                });

    }
    public interface OnUserFetchListener {
        void onUserFetched(User user);
        void onFailure(Exception e);
    }

    public void getUserByID(String userID,int stt,int score, OnUserFetchListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("User");

        usersCollection.whereEqualTo("id", userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.getString("username");
                            String img = document.getString("img");
                            User user = new User(username, userID, img,stt,score);
                            // Pass the retrieved user to the listener
                            listener.onUserFetched(user);
                        }
                    } else {
                        // Handle task failure
                        listener.onFailure(task.getException());
                    }
                });
    }
    public interface ScoreFetchListener {
        void onScoreFetched(int totalScore);
        void onFailure(Exception e);
    }

    public void calculateTotalScoreForTimeFrame(String userId, Date startTime, Date endTime, ScoreFetchListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference rankCollection = db.collection("Rank");

        rankCollection.document(userId)
                .collection("Score")
                .whereGreaterThanOrEqualTo("time", startTime)
                .whereLessThanOrEqualTo("time", endTime)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalScore = 0;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        int score = Integer.parseInt(document.getString("score"));
                        totalScore += score;
                    }
                    // Callback with the total score when fetching is successful
                    listener.onScoreFetched(totalScore);
                })
                .addOnFailureListener(e -> {
                    // Callback to handle any potential errors while fetching scores
                    listener.onFailure(e);
                });
    }
    private Map<User, Integer> sortByValueDescending(Map<User, Integer> map) {
        List<Map.Entry<User, Integer>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
        Map<User, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<User, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;

    }
    public void NewUser(String userID, final onNewUser listener)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference rankCollection = db.collection("Rank");

        rankCollection.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("id", userID);
                        rankCollection.document(userID).set(updates)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Gọi phương thức trả kết quả (listener) để thông báo thành công
                                        listener.onCompleteUser();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        // Xử lý lỗi nếu cần
                                        listener.onFetchError(e);
                                    }
                                });
                    }
                });

    }
    public  void SetQuizResult(long newTime,int newPoint,String userID , final onResultQuiz listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference scoreCollection = db.collection("Rank").document(userID).collection("Score");

        scoreCollection
                .get()

                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String newIdScore = generateUniqueId(task.getResult());
                            Map<String, Object> scoreData = new HashMap<>();
                            scoreData.put("score", String.valueOf(newPoint));
                            scoreData.put("time", FieldValue.serverTimestamp());
                            scoreData.put("id", newIdScore);

                            scoreCollection.document(newIdScore)
                                    .set(scoreData)
                                    .addOnSuccessListener(aVoid1 -> listener.onQuizResult())
                                    .addOnFailureListener(e -> listener.onUpdateError(e));
                        } else {
                            listener.onUpdateError(task.getException());
                        }
                    }
                });

    }



    private String generateUniqueId(QuerySnapshot querySnapshot) {

        String newTopicID = "score_1";
        // get present size of quizz,topic,....
        int topicCount = querySnapshot.size() + 1;
        /// loop for create new id and check if it exists
        do {
            // Create new ID
            newTopicID = "score_"+topicCount;
            topicCount++;
        } while (idExists(querySnapshot, newTopicID));
        return  newTopicID;
    }
    private boolean idExists(QuerySnapshot querySnapshot, String ID) {
        // Kiểm tra xem ID đã tồn tại trong collection hay chưa
        for (QueryDocumentSnapshot document : querySnapshot) {
            if (document.getString("id").equals(ID)) {
                return true;
            }
        }
        return false;
    }
    public String convertSecondsToDuration(long totalSeconds) {
        long days = totalSeconds / (24 * 3600);
        long hours = (totalSeconds % (24 * 3600)) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        String durationString = days + " days, " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds";
        return durationString;
    }
    public long convertDurationToSeconds(String durationString) {
        String[] parts = durationString.split("\\s+");
        long days = 0, hours = 0, minutes = 0, seconds = 0;

        for (int i = 0; i < parts.length; i += 2) {
            int value = Integer.parseInt(parts[i]);
            String unit = parts[i + 1];

            if (unit.contains("day")) {
                days = value;
            } else if (unit.contains("hour")) {
                hours = value;
            } else if (unit.contains("minute")) {
                minutes = value;
            } else if (unit.contains("second")) {
                seconds = value;
            }
        }

        return ((days * 24 + hours) * 60 + minutes) * 60 + seconds;
    }
}

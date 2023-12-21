package com.example.englishapp.viewmodel;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.englishapp.model.Quizz;
import com.example.englishapp.model.Topic;
import com.example.englishapp.model.Vocab;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class TopicVM {
    private Set<Topic> topicSet = new HashSet<>();

//===========================================Lấy topic trong folder=================================
    public void getTopicsForUser(ArrayList<String> id_Topic, String userId, final OnTopicsFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicDocRef = db.collection("Topic");
        String[] idTopicArray = id_Topic.toArray(new String[0]);
        topicDocRef.whereEqualTo("idOwner", userId)
                .whereIn("id", Arrays.asList(idTopicArray))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Create a new list to store topics

                        // Loop through the documents in the query result
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idTopic = document.getString("id");
                            String name = document.getString("name");
                            String idOwner = document.getString("idOwner");
                            String img = document.getString("image");
                            boolean access = document.getBoolean("access");

                            getVocabCountForTopic(document.getId(), new OnVocabCountFetchedListener() {
                                @Override
                                public void onVocabCountFetched(int vocabCount) {
                                    Topic topic = new Topic(idTopic, name, img, idOwner, vocabCount,access);
                                    // Add the topic to the list

                                    // Notify the listener about the fetched topic
                                    listener.onTopicFetched(topic);
                                }

                                @Override
                                public void onFetchError(Exception e) {
                                    // Notify the listener about the fetch error
                                    listener.onFetchError(e);
                                }
                            });
                        }

                        // Notify the listener about the completion of fetching all topics
                    } else {
                        // Handle the error
                        listener.onFetchError(task.getException());
                    }
                });

    }
//=========================================Lấy tất cả topic===========================================
public void getTopicsAllForUser( String userId, final OnTopicsFetchedListener listener) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference topicDocRef = db.collection("Topic");
    topicDocRef.whereEqualTo("idOwner", userId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Create a new list to store topics

                    // Loop through the documents in the query result
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String idTopic = document.getString("id");
                        String name = document.getString("name");
                        String idOwner = document.getString("idOwner");
                        String img = document.getString("image");
                        boolean access = document.getBoolean("access");

                        getVocabCountForTopic(document.getId(), new OnVocabCountFetchedListener() {
                            @Override
                            public void onVocabCountFetched(int vocabCount) {
                                Topic topic = new Topic(idTopic, name, img, idOwner, vocabCount,access);
                                // Add the topic to the list

                                // Notify the listener about the fetched topic
                                listener.onTopicFetched(topic);
                            }

                            @Override
                            public void onFetchError(Exception e) {
                                // Notify the listener about the fetch error
                                listener.onFetchError(e);
                            }
                        });
                    }

                    // Notify the listener about the completion of fetching all topics
                } else {
                    // Handle the error
                    listener.onFetchError(task.getException());
                }
            });

}

    public void getVocabCountForTopic(String topicId, final OnVocabCountFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the Vocabulary collection for the specified topic
        CollectionReference vocabCollection = db.collection("Topic").document(topicId).collection("Vocab");

        vocabCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle an error if needed
                    listener.onFetchError(error);
                    return;
                }

                // Get the updated vocab count
                int vocabCount = value.size();

                // Call the result callback (listener) and pass the updated vocab count
                listener.onVocabCountFetched(vocabCount);
            }
        });
    }

    public  void addTopicForUser(String idFolder,String userId,String topicName,final OnTopicAddedListener listener)
    {
        /// Create new topic,quizz,... based on newTopic
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicDocRef = db.collection("Topic");
        CollectionReference quizzDocRef = db.collection("Quizz");
        DocumentReference folderDocRef = db.collection("User").document(userId).collection("Folder").document(idFolder);
//        topicDocRef.whereEqualTo("idOwner",userId)
        // get new id for topic
        topicDocRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String newTopicID = "topic_1"; // Default for the first topic
                            String newQuizzID = "quizz_1"; //
                            // Kiểm tra xem có tài liệu trước đó không
                            if (!task.getResult().isEmpty()) {
                                // Lấy ID lớn nhất của tài liệu trước đó và tạo một ID mới từ nó
                                String[] newID = generateUniqueId(task.getResult());

                                newTopicID = newID[0];
                                newQuizzID = newID[1];
                            }
                            //add topic
                            String listImg[] = {"topic_1.jpg", "topic_2.png", "topic_3.jpg", "topic_4.jpg", "topic_5.jpg","topic_6.jpg","topic_7.jpg","topic_8.jpg","topic_9.jpg","topic_10.jpg","topic_11.jpg"};
                            String selectedImg = listImg[new Random().nextInt(listImg.length)];
                            Topic newTopic = new Topic(newTopicID,topicName,"topics/"+selectedImg,false,FieldValue.serverTimestamp(),FieldValue.serverTimestamp(),userId);
                            Topic topic = new Topic(newTopicID, topicName, "topics/"+selectedImg, userId,0,false);
//                            newTopic.setCount(0);
                            Map<String, Object> topicData = new HashMap<>();
                            topicData.put("id", newTopic.getId());
                            topicData.put("name", newTopic.getName());
                            topicData.put("image", newTopic.getImage());
                            topicData.put("access", newTopic.isAccess());
                            topicData.put("create_At", newTopic.getCreate_At());
                            topicData.put("last_Update", newTopic.getLast_Update());
                            topicData.put("idOwner", newTopic.getIdOwner());

                            String finalNewTopicID = newTopicID;
                            topicDocRef.document(newTopicID)
                                    .set(topicData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Gọi phương thức trả kết quả (listener) để thông báo thành công
                                            listener.onTopicAdded(topic);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            // Xử lý lỗi nếu cần
                                            listener.onAddError(e);
                                        }
                                    });
                            folderDocRef.update("id_topic",FieldValue.arrayUnion(newTopicID)) /// add new id_topic in to array but only the id don't have already present
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Xử lý cập nhật thành công
//                                            callback.onUserUpdated();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Xử lý lỗi khi cập nhật
                                            listener.onAddError(e);
                                        }
                                    });

                            ///add quizz
                            Quizz quizz = new Quizz(newQuizzID,newTopicID,0,0);
                            String finalNewQuizzID = newQuizzID;
                            quizzDocRef.document(newQuizzID)
                                    .set(quizz)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Gọi phương thức trả kết quả (listener) để thông báo thành công
                                            listener.onQuizzAdded(finalNewQuizzID);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            // Xử lý lỗi nếu cần
                                            listener.onAddError(e);
                                        }
                                    });


                        } else {
                            // Xử lý lỗi nếu cần
                            listener.onAddError(task.getException());
                        }
                    }
                });
    }
//======================================thêm topic vào folder từ trang tất cả topic===================
public void addTopicAllToFolderForUser(String idFolder,String userId,String idTopic)
{
    /// Create new topic,quizz,... based on newTopic
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference folderDocRef = db.collection("User").document(userId).collection("Folder").document(idFolder);
//        topicDocRef.whereEqualTo("idOwner",userId)
    // get new id for topic
    folderDocRef.update("id_topic",FieldValue.arrayUnion(idTopic)) /// add new id_topic in to array but only the id don't have already present
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Xử lý cập nhật thành công
//                                            callback.onUserUpdated();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Xử lý lỗi khi cập nhật
                }
            });
}
//=====================================thêm topic không cần id folder============================
public  void addTopicToAllForUser(String userId,String topicName,final OnTopicAddedListener listener)
{
    /// Create new topic,quizz,... based on newTopic
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference topicDocRef = db.collection("Topic");
    CollectionReference quizzDocRef = db.collection("Quizz");
//        topicDocRef.whereEqualTo("idOwner",userId)
    // get new id for topic
    topicDocRef
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        String newTopicID = "topic_1"; // Default for the first topic
                        String newQuizzID = "quizz_1"; //
                        // Kiểm tra xem có tài liệu trước đó không
                        if (!task.getResult().isEmpty()) {
                            // Lấy ID lớn nhất của tài liệu trước đó và tạo một ID mới từ nó
                            String[] newID = generateUniqueId(task.getResult());

                            newTopicID = newID[0];
                            newQuizzID = newID[1];
                        }
                        //add topic
                        String listImg[] = {"topic_1.jpg", "topic_2.png", "topic_3.jpg", "topic_4.jpg", "topic_5.jpg","topic_6.jpg","topic_7.jpg","topic_8.jpg","topic_9.jpg","topic_10.jpg","topic_11.jpg"};
                        String selectedImg = listImg[new Random().nextInt(listImg.length)];
                        Topic newTopic = new Topic(newTopicID,topicName,"topics/"+selectedImg,false,FieldValue.serverTimestamp(),FieldValue.serverTimestamp(),userId);
                        Topic topic = new Topic(newTopicID, topicName, "topics/"+selectedImg, userId,0,false);
//                            newTopic.setCount(0);
                        Map<String, Object> topicData = new HashMap<>();
                        topicData.put("id", newTopic.getId());
                        topicData.put("name", newTopic.getName());
                        topicData.put("image", newTopic.getImage());
                        topicData.put("access", newTopic.isAccess());
                        topicData.put("create_At", newTopic.getCreate_At());
                        topicData.put("last_Update", newTopic.getLast_Update());
                        topicData.put("idOwner", newTopic.getIdOwner());

                        String finalNewTopicID = newTopicID;
                        topicDocRef.document(newTopicID)
                                .set(topicData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Gọi phương thức trả kết quả (listener) để thông báo thành công
                                        listener.onTopicAdded(topic);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        // Xử lý lỗi nếu cần
                                        listener.onAddError(e);
                                    }
                                });


                        ///add quizz
                        Quizz quizz = new Quizz(newQuizzID,newTopicID,0,0);
                        String finalNewQuizzID = newQuizzID;
                        quizzDocRef.document(newQuizzID)
                                .set(quizz)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Gọi phương thức trả kết quả (listener) để thông báo thành công
                                        listener.onQuizzAdded(finalNewQuizzID);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        // Xử lý lỗi nếu cần
                                        listener.onAddError(e);
                                    }
                                });


                    } else {
                        // Xử lý lỗi nếu cần
                        listener.onAddError(task.getException());
                    }
                }
            });
}

    private String[] generateUniqueId(QuerySnapshot querySnapshot) {
        String newQuizzID = "quizz_1"; // Default for the first id for topic,quizz,....
        String newTopicID = "topic_1";

        // get present size of quizz,topic,....
        int topicCount = querySnapshot.size() + 1;
        int quizzCount = topicCount;

        /// loop for create new id and check if it exists
        do {
            // Create new ID
            newQuizzID = "quizz_" + quizzCount;
            newTopicID = "topic_"+topicCount;

            topicCount++;
        } while (idExists(querySnapshot, newTopicID));

        return new String[]{newTopicID,newQuizzID};
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


    public interface OnVocabCountFetchedListener {
        void onVocabCountFetched(int vocabCount);
        void onFetchError(Exception e);
    }

    public interface OnTopicsFetchedListener {
        void onTopicFetched(Topic topic);
        void onFetchError(Exception e);
    }

    public interface OnTopicAddedListener {
        void onTopicAdded(Topic topic); // Được gọi khi thêm topic thành công

        void onAddError(Exception e); // Được gọi khi gặp lỗi khi thêm
        void onQuizzAdded(String quizzID); // Được gọi khi thêm topic thành công


    }
    public interface OnTopicsHomeFetchedListener {
        void onTopicHomeFetched(Topic topic);
        void onTopicHomeIDFetched(List<String> idList);
        void onFetchError(Exception e);
    }
    public interface OnTopicCountListener {
        void onTopicGameFetched(Integer b);
        void onFetchError(Exception e);
    }

    //============================ Xóa topic==============================================
    public void deleteTopic(String idTopic) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("Topic");

        // Lấy tham chiếu đến document "topic" có id là idTopic
        DocumentReference topicDocument = topicCollection.document(idTopic);

        // Xóa document "topic"
        topicDocument.delete()
                .addOnSuccessListener(aVoid -> {
                    // Document đã được xóa thành công
                    // Thực hiện các hành động khác nếu cần
                })
                .addOnFailureListener(e -> {
                    // Xảy ra lỗi khi xóa document
                    // Xử lý lỗi ở đây nếu cần
                });
    }
    //===============================Xóa topic trong folder=================================
    public void deleteTopicInFolder(String idUser,String idFolder, String idTopic) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tham chiếu đến document user
        DocumentReference userDocument = db.collection("User").document(idUser);

        // Tham chiếu đến collection folder trong user
        CollectionReference folderCollection = userDocument.collection("Folder");

        // Tham chiếu đến document folder
        DocumentReference folderDocument = folderCollection.document(idFolder);

        // Cập nhật mảng trong document folder
        folderDocument.update("id_topic", FieldValue.arrayRemove(idTopic))
                .addOnSuccessListener(aVoid -> {
                    // ID topic đã được xóa thành công khỏi mảng
                })
                .addOnFailureListener(e -> {
                    // Xảy ra lỗi khi cập nhật mảng
                    // Xử lý lỗi ở đây nếu cần
                });
    }
///=============================================Community==================================================================//
    public void getTopicForCommunity( String userId,String game,String mode,final OnTopicsFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicDocRef = db.collection("Topic");
        topicDocRef.whereEqualTo("access",true)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
//                            List<Topic> topics = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult()) {
                            String idTopic = document.getString("id");
                            String name = document.getString("name");
                            String idOwner = document.getString("idOwner");
                            String img = document.getString("image");

                            // Tiếp tục với việc lấy từ vựng trong collection "vocab"
                            getVocabForTopic(idTopic, new OnVocabFetchListener() {
                                @Override
                                public void onVocabFetch(List<Vocab> vocabList) {
                                    Topic topic = new Topic(idTopic, name, img, idOwner,true,game,mode,vocabList);
                                    if(game.equals("quiz")&&topic.getVocabList().size()>=4){
                                        listener.onTopicFetched(topic);
                                    } else if ((game.equals("flashCard") || game.equals("fillWord")) &&topic.getVocabList().size()>=1) {
                                        listener.onTopicFetched(topic);
                                    }
                                }
                                @Override
                                public void onFetchError(Exception e) {
                                    listener.onFetchError(task.getException());
                                }
                            });


                        }
                        topicDocRef.whereEqualTo("access", false)
                                .whereEqualTo("idOwner", userId)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot document : task.getResult()) {
                                                String idTopic = document.getString("id");
                                                String name = document.getString("name");
                                                String idOwner = document.getString("idOwner");
                                                String img = document.getString("image");
                                                getVocabForTopic(idTopic, new OnVocabFetchListener() {
                                                    @Override
                                                    public void onVocabFetch(List<Vocab> vocabList) {
                                                        Topic topic = new Topic(idTopic, name, img, idOwner,false,game,mode,vocabList);
                                                        if(game.equals("quiz")&&topic.getVocabList().size()>=4){
                                                            listener.onTopicFetched(topic);
                                                        } else if ((game.equals("flashCard") || game.equals("fillWord")) &&topic.getVocabList().size()>=1) {
                                                            listener.onTopicFetched(topic);
                                                        }
                                                    }
                                                    @Override
                                                    public void onFetchError(Exception e) {
                                                        listener.onFetchError(task.getException());
                                                    }
                                                });
                                            }
                                        } else {
                                            listener.onFetchError(task.getException());
                                        }
                                    }
                                });
                    } else {
                        listener.onFetchError(task.getException());
                    }
                }
            });
    }
    public void getVocabForTopic(String topicId, final OnVocabFetchListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the Vocabulary collection for the specified topic
        CollectionReference vocabCollection = db.collection("Topic").document(topicId).collection("Vocab");
        List<Vocab> vocabList = new ArrayList<>();
        vocabCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle an error if needed
                    listener.onFetchError(error);
                    return;
                }

                for (QueryDocumentSnapshot vocabDocument : value) {
                    // Xử lý từng từ vựng ở đây
                    String Id = vocabDocument.getString("id");
                    String Name = vocabDocument.getString("word");
                    String trans = vocabDocument.getString("trans");
                    String img = vocabDocument.getString("image");
                    String process = vocabDocument.getString("process");
                    String fav = vocabDocument.getString("favourite");
                    Vocab vocabulary = new Vocab(Id, Name, trans, img, process, fav, topicId);
                    vocabList.add(vocabulary);
                    // Thêm từ vựng vào danh sách
                }
                listener.onVocabFetch(vocabList);
            }
        });
    }

    public void getCountTopicForCommunity( String userId, final OnTopicsCountListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicDocRef = db.collection("Topic");
        topicDocRef.whereEqualTo("access",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final int[] count = {task.getResult().size()};
                            count[0]= task.getResult().size();
                            topicDocRef.whereEqualTo("access", false)
                                    .whereEqualTo("idOwner", userId)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                count[0] += task.getResult().size();
                                                listener.onTopicCountFetched(count[0]);
                                            } else {

                                            }
                                        }
                                    });
                        } else {
                            listener.onCountError(task.getException());
                        }
                    }
                });
    }
    public void getTopicCommunityForHome( String userId,final OnTopicsHomeFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference topicDocRef = db.collection("Topic");
        topicDocRef.whereEqualTo("access",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            List<Topic> topics = new ArrayList<>();
                            List<String> idList = new ArrayList<>() ;
                            for (DocumentSnapshot document : task.getResult()) {
                                String idTopic = document.getString("id");
                                String name = document.getString("name");
                                String idOwner = document.getString("idOwner");
                                String img = document.getString("image");
                                Boolean access = document.getBoolean("access");
                                idList.add(idTopic);
                                // Tiếp tục với việc lấy từ vựng trong collection "vocab"
                                getVocabCountForTopic(idTopic, new OnVocabCountFetchedListener() {
                                    @Override
                                    public void onVocabCountFetched(int vocabCount) {
                                        Topic topic = new Topic(idTopic,name,img,idOwner,vocabCount,access,"");
                                        listener.onTopicHomeFetched(topic);
                                    }

                                    @Override
                                    public void onFetchError(Exception e) {
                                        listener.onFetchError(task.getException());
                                    }
                                });
                            }
                            listener.onTopicHomeIDFetched(idList);
                        } else {
                            listener.onFetchError(task.getException());
                        }
                    }
                });
    }
public void getCountTopicForGame( String userId,String game,final OnTopicCountListener listener) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference topicDocRef = db.collection("Topic");
    final int[] a = {0};

    // Fetch topics where access is true
    topicDocRef
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        String idTopic = document.getString("id");
                        Boolean access = document.getBoolean("access");
                        String idOwner = document.getString("idOwner");
                        getVocabForTopic(idTopic, new OnVocabFetchListener() {
                            @Override
                            public void onVocabFetch(List<Vocab> vocabList) {
                                if(access == true||(access==false && idOwner.equals(userId)))
                                {
                                    if (game.equals("quiz")&& vocabList.size() >= 4) {
                                        a[0]++;

                                    } else if (game.equals("flashCard")&&vocabList.size() >= 1) {
                                        a[0]++;
                                    }
                                }
                                listener.onTopicGameFetched(a[0]);

                                // Notify listener with counts after fetching vocab
                            }
                            @Override
                            public void onFetchError(Exception e) {
                                listener.onFetchError(e); // Pass error to listener
                            }
                        });
                    }
                } else {
                    listener.onFetchError(task.getException()); // Pass error to listener
                }
            });

}
    public interface OnTopicsCountListener {
        void onTopicCountFetched(Integer topic);
        void onCountError(Exception e);
    }
    public interface  OnUserListener{
        void onUserFetch(String userName);
        void onFetchError(Exception e);
    }
    public interface  OnVocabFetchListener{
        void onVocabFetch(List<Vocab> vocabList);
        void onFetchError(Exception e);
    }
//=========================================cập nhật trạng thái public của topic=======================
public void updateTopicAccess(String topicId, boolean access, final OnTopicUpdatedListener listener) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference topicDocRef = db.collection("Topic").document(topicId);

    // Dữ liệu cập nhật
    Map<String, Object> updateData = new HashMap<>();
    updateData.put("access", access);

    // Thực hiện cập nhật
    topicDocRef.update(updateData)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Cập nhật thành công
                    if (listener != null) {
                        listener.onTopicUpdated();
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Xử lý khi cập nhật thất bại
                    if (listener != null) {
                        listener.onUpdateError(e);
                    }
                }
            });
}

    public interface OnTopicUpdatedListener {
        void onTopicUpdated();

        void onUpdateError(Exception e);
    }
}

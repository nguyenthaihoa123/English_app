package com.example.englishapp.viewmodel;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.englishapp.import_data;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;


public class VocabVM {
    private String pathImg;
    public void getTopicsForUser(String userId, String idTopic, final OnTopicsFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Truy vấn chủ đề theo idTopic và idOwner
        db.collection("Topic")
                .whereEqualTo("idOwner", userId)
                .whereEqualTo("id", idTopic)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Xử lý lỗi nếu có
                            listener.onFetchError(error);
                            return;
                        }

                        // Duyệt qua kết quả truy vấn (thường là một chủ đề duy nhất)
                        for (QueryDocumentSnapshot document : value) {
                            // Tạo một đối tượng Topic từ DocumentSnapshot
                            String topicId = document.getString("id");
                            String topicName = document.getString("name");

                            // Gọi phương thức trả kết quả (listener) và truyền đối tượng Topic
                            listener.onTopicFetched(topicName, topicId);

                            // Lấy collection "vocab" từ chủ đề
                            CollectionReference vocabCollection = document.getReference().collection("Vocab");

                            // Tiếp tục với việc lấy từ vựng trong collection "vocab"
                            getVocabInCollection(vocabCollection,idTopic, listener);
                        }
                    }
                });
    }



    private void getVocabInCollection(CollectionReference vocabCollection,String idTopic, final OnTopicsFetchedListener listener) {
        final ArrayList<Vocab> vocabList = new ArrayList<>(); // Tạo một ArrayList để lưu trữ từ vựng

        vocabCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Xử lý lỗi nếu có
                    listener.onFetchError(error);
                    return;
                }

                // Xóa danh sách từ vựng hiện tại

                // Duyệt qua tất cả các tài liệu trong collection "vocab"
                for (QueryDocumentSnapshot vocabDocument : value) {
                    // Xử lý từng từ vựng ở đây
                    String Id = vocabDocument.getString("id");
                    String Name = vocabDocument.getString("word");
                    String trans = vocabDocument.getString("trans");
                    String img = vocabDocument.getString("image");
                    String process = vocabDocument.getString("process");
                    String fav = vocabDocument.getString("favourite");
                    Vocab vocabulary = new Vocab(Id, Name, trans, img, process, fav, idTopic);

                    // Thêm từ vựng vào danh sách
                    listener.onVocabListFetched(vocabulary);
                }
            }
        });
    }
    public void getVocabBasesList(ArrayList<String> listVocab,String id_Topic, final OnCheckAnswer listener)
    {
//        List<Vocab> listAnswer = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference vocabDocRef = db.collection("Topic").document(id_Topic).collection("Vocab");
        vocabDocRef.whereIn("word", listVocab)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Vocab> listAnswer = new ArrayList<>();
                        for (QueryDocumentSnapshot vocabDocument : task.getResult()) {
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
                    } else {
                        // Handle the error
                        if (task.getException() != null) {
                            listener.onFetchError(task.getException());
                        } else {
                            listener.onFetchError(new Exception("Unknown error occurred"));
                        }
                    }
                });
    }
    public void getVocabBasedString(String vocab,String id_Topic, final OnVocabString listener)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference vocabDocRef = db.collection("Topic").document(id_Topic).collection("Vocab");
        vocabDocRef.whereEqualTo("word", vocab).limit(1)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        listener.onFetchError(error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        DocumentSnapshot vocabDocument = value.getDocuments().get(0);
                        String Id = vocabDocument.getString("id");
                        String Name = vocabDocument.getString("word");
                        String trans = vocabDocument.getString("trans");
                        String img = vocabDocument.getString("image");
                        String process = vocabDocument.getString("process");
                        String fav = vocabDocument.getString("favourite");
                        Vocab vocabulary = new Vocab(Id, Name, trans, img, process, fav);
                        listener.onVocabFetch(vocabulary);
                    }
                });
    }
    public interface  OnCheckAnswer{
        void onVocabFetch(ArrayList<Vocab> listAnswer);
        void onFetchError(Exception e);
    }
    public interface  OnVocabString{
        void onVocabFetch(Vocab vocab);
        void onFetchError(Exception e);
    }


    public interface OnTopicsFetchedListener {
        void onTopicFetched(String topicName, String topicId);
        void onVocabListFetched(Vocab vocabList);
        void onVocabAllFetched(ArrayList<ArrayList<Vocab>> allVocabLists);
        void onFetchError(Exception e);
    }
//===============================================Lấy tất cả từ vựng liên quan đến user=========================
public void getAllVocabForUser(String userId, final OnTopicsFetchedListener listener) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Truy vấn tất cả các chủ đề của người dùng có idOwner là userId
    db.collection("Topic")
            .whereEqualTo("idOwner", userId)
            .get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot topicSnapshots) {
                    // Duyệt qua tất cả các chủ đề của người dùng
                    for (QueryDocumentSnapshot topicDocument : topicSnapshots) {
                        String topicId = topicDocument.getString("id");
                        String topicName = topicDocument.getString("name");

                        // Gọi phương thức trả kết quả (listener) và truyền thông tin topic
                        listener.onTopicFetched(topicName, topicId);

                        // Lấy collection "Vocab" từ chủ đề
                        CollectionReference vocabCollection = topicDocument.getReference().collection("Vocab");

                        // Tiếp tục với việc lấy từ vựng trong collection "Vocab"
                        getVocabAllCollection(vocabCollection, listener);
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Xử lý lỗi nếu có
                    listener.onFetchError(e);
                }
            });
}
    private void getVocabAllCollection(CollectionReference vocabCollection, final OnTopicsFetchedListener listener) {
        final ArrayList<Vocab> vocabList = new ArrayList<>(); // Tạo một ArrayList để lưu trữ từ vựng

        vocabCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Xử lý lỗi nếu có
                    listener.onFetchError(error);
                    return;
                }

                // Xóa danh sách từ vựng hiện tại

                // Duyệt qua tất cả các tài liệu trong collection "vocab"
                for (QueryDocumentSnapshot vocabDocument : value) {
                    // Xử lý từng từ vựng ở đây
                    String Id = vocabDocument.getString("id");
                    String Name = vocabDocument.getString("word");
                    String trans = vocabDocument.getString("trans");
                    String img = vocabDocument.getString("image");
                    String process = vocabDocument.getString("process");
                    String fav = vocabDocument.getString("favourite");
                    Vocab vocabulary = new Vocab(Id, Name, trans, img, process, fav);

                    // Thêm từ vựng vào danh sách
                    listener.onVocabListFetched(vocabulary);
                }
            }
        });
    }
//=============================================== thêm từ vựng ===============================================
public void addVocabForUser(String topicId, String word, String translation,Context add, Uri path, final OnVocabAddedListener listener) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference topicDocRef = db.collection("Topic").document(topicId);
    CollectionReference vocabCollection = topicDocRef.collection("Vocab");

    // Lấy ID mới cho từ vựng
    vocabCollection
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        String newVocabId = "vocab_1"; // Default for the first vocab

                        // Kiểm tra xem có tài liệu trước đó không
                        if (!task.getResult().isEmpty()) {
                            // Lấy ID lớn nhất của tài liệu trước đó và tạo một ID mới từ nó
                            newVocabId = generateUniqueVocabId(task.getResult());
                        }
                        String pathFB;
                        if (path == null){
                            pathFB ="images/04c4a8db-f994-4c6a-87db-c7ebb41389d6";
                        }
                        else {
                            pathFB = uploadImage(add,path);
                        }
                        // Tạo một đối tượng Vocab
                        Map<String, Object> vocabData = new HashMap<>();
                        vocabData.put("id", newVocabId);
                        vocabData.put("word", word);
                        vocabData.put("trans", translation);
                        vocabData.put("image", pathFB);
                        vocabData.put("process", "0");
                        vocabData.put("favourite", "");

                        String finalNewVocabId = newVocabId;
                        vocabCollection.document(newVocabId)
                                .set(vocabData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Gọi phương thức trả kết quả (listener) để thông báo thành công
                                        listener.onVocabAdded(finalNewVocabId);
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

    private String generateUniqueVocabId(QuerySnapshot querySnapshot) {
        String newVocabId = "vocab_1"; // Default for the first vocab

        // Lấy số lượng từ vựng hiện có trong collection
        int vocabCount = querySnapshot.size() + 1;

        do {
            // Tạo ID mới
            newVocabId = "vocab_" + vocabCount;

            // Tăng giá trị số lượng từ vựng để chuẩn bị cho lần kiểm tra tiếp theo
            vocabCount++;
        } while (idExists(querySnapshot, newVocabId));

        return newVocabId;
    }

    private boolean idExists(QuerySnapshot querySnapshot, String vocabId) {
        // Kiểm tra xem ID đã tồn tại trong collection hay chưa
        for (QueryDocumentSnapshot document : querySnapshot) {
            if (document.getString("id").equals(vocabId)) {
                return true;
            }
        }
        return false;
    }
    public String incrementDocumentIdVocab(String lastDocumentId) {
        // Parse the last document ID and increment it
        try {
            String[] parts = lastDocumentId.split("_");
            int lastNumber = Integer.parseInt(parts[1]);
            int newNumber = lastNumber + 1;
            return "vocab_" + newNumber;
        } catch (NumberFormatException e) {
            // Handle the case where the document ID is not numeric
            return "vocab_1"; // Default for the first document
        }
    }

    public interface OnVocabAddedListener {
        void onVocabAdded(String vocabId); // Được gọi khi thêm từ vựng thành công
        void onAddError(Exception e); // Được gọi khi gặp lỗi khi thêm từ vựng
    }
//=====================================================UPload Img=====================================
    public String uploadImage(Context add, Uri filePath) {
        FirebaseStorage storage;
        StorageReference storageReference;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        String path ="";
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(add);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            path = "images/"+ UUID.randomUUID().toString();
            StorageReference ref = storageReference.child(path);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
        return path;
    }
//=============================================check_Word_fill========================
    public boolean checkWordFill(String wordFill, String key){
        return wordFill.equalsIgnoreCase(key);
    }
//=============================================update-Process===============================
public void updateVocabForUser(String topicId, String vocabId, String process, final OnVocabUpdatedListener listener) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference topicDocRef = db.collection("Topic").document(topicId);
    CollectionReference vocabCollection = topicDocRef.collection("Vocab");
    DocumentReference vocabDocRef = vocabCollection.document(vocabId);

    // Dữ liệu cập nhật
    Map<String, Object> updateData = new HashMap<>();
    updateData.put("process", process);

    // Thực hiện cập nhật
    vocabDocRef.update(updateData)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Cập nhật thành công
                    if (listener != null) {
                        listener.onVocabUpdated();
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
    public void updateVocabFavForUser(String topicId, String vocabId, String fav, final OnVocabUpdatedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference topicDocRef = db.collection("Topic").document(topicId);
        CollectionReference vocabCollection = topicDocRef.collection("Vocab");
        DocumentReference vocabDocRef = vocabCollection.document(vocabId);

        // Dữ liệu cập nhật
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("favourite", fav);

        // Thực hiện cập nhật
        vocabDocRef.update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Cập nhật thành công
                        if (listener != null) {
                            listener.onVocabUpdated();
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
    public interface OnVocabUpdatedListener {
        void onVocabUpdated();

        void onUpdateError(Exception e);
    }

//==========================================upload vocab without img=======================

public void deleteVocab(String idTopic, String idVocab){
    FirebaseFirestore db = FirebaseFirestore.getInstance();
// Lấy tham chiếu đến collection "topic"
    CollectionReference topicCollection = db.collection("Topic");
// Lấy tham chiếu đến collection "vocab" trong document có id là idTopic
    DocumentReference topicDocument = topicCollection.document(idTopic);
    CollectionReference vocabCollection = topicDocument.collection("Vocab");

// Tìm document trong "vocab" có id là idWord
    vocabCollection.whereEqualTo("id", idVocab).get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Lấy document đầu tiên (nếu có)
                        DocumentReference wordDocument = querySnapshot.getDocuments().get(0).getReference();

                        // Xóa document từ "vocab"
                        if (wordDocument != null) {
                            wordDocument.delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Document đã được xóa thành công
                                        // Thực hiện các hành động khác nếu cần
                                    })
                                    .addOnFailureListener(e -> {
                                        // Xảy ra lỗi khi xóa document
                                        // Xử lý lỗi ở đây nếu cần
                                    });
                        }
                    }
                } else {
                    // Xảy ra lỗi khi truy vấn "vocab"
                    // Xử lý lỗi ở đây nếu cần
                }
            });
}

}



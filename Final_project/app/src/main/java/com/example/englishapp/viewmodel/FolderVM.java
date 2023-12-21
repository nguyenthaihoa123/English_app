package com.example.englishapp.viewmodel;

import androidx.annotation.Nullable;

import com.example.englishapp.model.Folder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolderVM {

    public void getFoldersFromFirebase(String userId, final OnFoldersFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("User").document(userId);
        CollectionReference foldersCollection = userDocRef.collection("Folder");

        // Thay thế get() bằng addSnapshotListener
        foldersCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = document.getString("id");
                    String name = document.getString("name");
                    List<String> idTopicList = (List<String>) document.get("id_topic");
                    Date time = document.getDate("create_At");

                    Folder folder = new Folder(id, name, idTopicList, time);
                    listener.onFoldersFetched(folder);
                }

                // Thông báo người nghe với danh sách các thư mục
            } else {
                // Xử lý lỗi nếu cần
                listener.onFetchError(task.getException());
            }
        });
    }


    // Define the OnFoldersFetchedListener interface
    public interface OnFoldersFetchedListener {
        void onFoldersFetched(Folder folders);

        void onFetchError(Exception e);
    }
    public void addFolderForUser(String userId, String folderName, final OnFolderAddedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("User").document(userId);
        CollectionReference foldersCollection = userDocRef.collection("Folder");

        // Lấy ID mới cho folder
        foldersCollection.orderBy("id", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String newFolderId = "Folder_1"; // Default for the first folder

                            // Kiểm tra xem có tài liệu trước đó không
                            if (!task.getResult().isEmpty()) {
                                // Lấy ID lớn nhất của tài liệu trước đó và tạo một ID mới từ nó
                                QueryDocumentSnapshot lastDocument = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                                String lastDocumentId = lastDocument.getString("id");
                                newFolderId = incrementDocumentId(lastDocumentId);
                            }

                            // Tạo một đối tượng Folder
                            Map<String, Object> folderData = new HashMap<>();
                            folderData.put("id", newFolderId);
                            folderData.put("name", folderName);
                            folderData.put("id_topic", new ArrayList<String>()); // Tạo danh sách trống
                            folderData.put("create_At", FieldValue.serverTimestamp());

                            Folder folderAdd = new Folder(newFolderId, folderName,new ArrayList<String>(),new Date());
                            String finalNewFolderId = newFolderId;
                            foldersCollection.document(newFolderId)
                                    .set(folderData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Gọi phương thức trả kết quả (listener) để thông báo thành công
                                            listener.onFolderAdded(folderAdd);
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

    private String incrementDocumentId(String lastDocumentId) {
        // Parse the last document ID and increment it
        try {
            String[] parts = lastDocumentId.split("_");
            int lastNumber = Integer.parseInt(parts[1]);
            int newNumber = lastNumber + 1;
            return String.valueOf("Folder_" + newNumber);
        } catch (NumberFormatException e) {
            // Handle the case where the document ID is not numeric
            return "Folder_1"; // Default for the first document
        }
    }

    public interface OnFolderAddedListener {
        void onFolderAdded(Folder folder); // Được gọi khi thêm folder thành công

        void onAddError(Exception e); // Được gọi khi gặp lỗi khi thêm folder
    }
//================================xóa folder========================================
public void deleteFolder(String idUser, String idFolder) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Tham chiếu đến document user
    DocumentReference userDocument = db.collection("User").document(idUser);

    // Tham chiếu đến collection folder trong user
    CollectionReference folderCollection = userDocument.collection("Folder");

    // Tham chiếu đến document folder
    DocumentReference folderDocument = folderCollection.document(idFolder);

    // Xóa document folder
    folderDocument.delete()
            .addOnSuccessListener(aVoid -> {
                // Document folder đã được xóa thành công
                // Thực hiện các hành động khác nếu cần
            })
            .addOnFailureListener(e -> {
                // Xảy ra lỗi khi xóa document folder
                // Xử lý lỗi ở đây nếu cần
            });
}

}

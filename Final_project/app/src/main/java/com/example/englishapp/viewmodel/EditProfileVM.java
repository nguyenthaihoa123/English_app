package com.example.englishapp.viewmodel;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.englishapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfileVM {
    public interface CallbackUpdate {
        void onUserUpdated();
        void onUserUpdateError(Exception e);
    }
    public interface Callback {
        void onUserReceived(User user);
        void onUserNotFound();
        void onUserError(Exception e);
    }

    public void getInfoUser(String id, final Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("User").document(id);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    String pwd = documentSnapshot.getString("pwd");
                    String ID = documentSnapshot.getString("id");
                    String address = documentSnapshot.getString("address");
                    String number = documentSnapshot.getString("number");
                    String email = documentSnapshot.getString("email");
                    String img = documentSnapshot.getString("img");

                    User user = new User(username, pwd, id, email, address, number,img);

                    // Ở đây, bạn có thể làm bất kỳ xử lý nào với đối tượng User, ví dụ: gọi callback hoặc trả về giá trị.
                    // Đảm bảo đối tượng User được truyền đúng cách tới nơi bạn muốn sử dụng nó.

                    // Ví dụ gọi callback
                    callback.onUserReceived(user);
                } else {
                    // Xử lý tài khoản không tồn tại
                    callback.onUserNotFound();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Xử lý lỗi khi truy vấn Firebase
                callback.onUserError(e);
            }
        });
    }
    public void updateUserInfo(User user,String id,Context add, Uri path, final CallbackUpdate callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("User").document(id);

        // Tạo một HashMap chứa dữ liệu cần cập nhật
        Map<String, Object> userData = new HashMap<>();
        String pathFB;
        if(path == null){
            pathFB = user.getImg();
        }
        else {
            pathFB = uploadImage(add,path);
        }

        userData.put("username", user.getUsername());
        userData.put("pwd", user.getPwd());
        userData.put("email", user.getEmail());
        userData.put("address", user.getAddress());
        userData.put("number", user.getNumber());
        userData.put("img", pathFB);
        // Thực hiện cập nhật dữ liệu
        docRef.update(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Xử lý cập nhật thành công
                        callback.onUserUpdated();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý lỗi khi cập nhật
                        callback.onUserUpdateError(e);
                    }
                });
    }
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

}
package com.example.englishapp.viewmodel;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.englishapp.login;
import com.example.englishapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.ExecutionException;

public class LoginVM {
    private FirebaseFirestore db;
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "pwd";

    public interface AccountCallback {
        void onAccountReceived(String documentId, String username, String password);
        void onAccountNotFound();
        void onAccountError(Exception e);
    }

    public void getAccount(String enteredUsername, String enteredPassword, final AccountCallback callback) {
        db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("User");
        Query query = usersCollection.whereEqualTo(KEY_USERNAME, enteredUsername);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String storedPassword = document.getString(KEY_PASSWORD);
                        String documentId = document.getId(); // Lấy Document ID

                        // So sánh storedPassword và enteredPassword
                        if (storedPassword.equals(enteredPassword)) {

                            ///update data user into model User
                            User user =new User(enteredUsername,enteredPassword,documentId,"","","");
                            // Password khớp, người dùng đã đăng nhập thành công
                            // Ở đây, bạn có thể sử dụng documentId (Document ID) cho mục đích của mình.
                            callback.onAccountReceived(documentId, enteredUsername, storedPassword);
                            return; // Thoát khỏi vòng lặp khi đã tìm thấy tài khoản khớp
                        }
                    }
                    // Nếu đã kiểm tra tất cả các tài khoản mà không có tài khoản nào khớp.
                    callback.onAccountNotFound();
                } else {
                    // Lỗi khi truy vấn
                    callback.onAccountError(task.getException());
                }
            }
        });
    }
    public void getAccountAu(String enteredUsername, final AccountCallback callback) {
        db = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = db.collection("User");
        Query query = usersCollection.whereEqualTo("email", enteredUsername);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String storedPassword = document.getString(KEY_PASSWORD);
                        String documentId = document.getId(); // Lấy Document ID

                        // So sánh storedPassword và enteredPassword
                            ///update data user into model User
                            User user =new User(enteredUsername,storedPassword,documentId,"","","");
                            // Password khớp, người dùng đã đăng nhập thành công
                            // Ở đây, bạn có thể sử dụng documentId (Document ID) cho mục đích của mình.
                            callback.onAccountReceived(documentId, enteredUsername, storedPassword);
                            return; // Thoát khỏi vòng lặp khi đã tìm thấy tài khoản khớp
                    }
                    // Nếu đã kiểm tra tất cả các tài khoản mà không có tài khoản nào khớp.
                    callback.onAccountNotFound();
                } else {
                    // Lỗi khi truy vấn
                    callback.onAccountError(task.getException());
                }
            }
        });
    }
    public boolean isPasswordValid(String password) {
        // Kiểm tra mật khẩu có ít nhất 8 ký tự
//        if (password.length() < 8||!password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*")||!password.matches(".*\\d.*")||!password.matches(".*[^a-zA-Z0-9].*")) {
//            return false;
//        }
        return true;
    }

    public User getUserData(String id) {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("User").document(id);

        try {
            DocumentSnapshot document = Tasks.await(docRef.get()); // Sử dụng Tasks.await để đợi kết quả

            if (document.exists()) {
//                String username = document.getString(KEY_USERNAME);
//                String password = document.getString(KEY_PASSWORD);
//                String email = document.getString("email"); // Thay "email" bằng tên trường trong tài liệu
//                String phoneNumber = document.getString("phoneNumber"); // Thay "phoneNumber" bằng tên trường trong tài liệu
//                String address = document.getString("address"); // Thay "address" bằng tên trường trong tài liệu
//                String idUser = document.getString("ID");
                return null;
            } else {
                return null; // Hoặc bạn có thể trả về một đối tượng User mặc định khác, hoặc ném một ngoại lệ tùy theo yêu cầu của ứng dụng.
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null; // Hoặc xử lý lỗi theo cách bạn muốn ở đây.
        }
    }



}


package com.example.englishapp.viewmodel;

import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.englishapp.model.User;
import com.example.englishapp.register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class RegisterVM {
    private FirebaseFirestore db;
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";
//    register Register = new register();

    public interface AccountCallback {
        void onAccountReceived(String documentId, String username, String password,String email);
        void onAccountFound();
        void onAccountError(Exception e);
    }

    interface UsernameCallback {
        void onUsernameGenerated(String username);
        void onUsernameError(Exception e);
    }

    public void addAccount(String enteredUsername, String enteredPassword, String enteredEmail,String enteredConfirmPass, final RegisterVM.AccountCallback callback) {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Kiểm tra xem địa chỉ email đã được sử dụng chưa
        auth.fetchSignInMethodsForEmail(enteredEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> results = task.getResult().getSignInMethods();
                        if (results != null && !results.isEmpty()) {
                            // Địa chỉ email đã được sử dụng
                            callback.onAccountError(new Exception("Email already in use"));
                        } else {
                            // Địa chỉ email chưa được sử dụng, tiếp tục quá trình đăng ký
                            generateUniqueUsername(new UsernameCallback() {
                                @Override
                                public void onUsernameGenerated(String username) {
                                    // Tạo người dùng mới với email và mật khẩu
                                    auth.createUserWithEmailAndPassword(enteredEmail, enteredPassword)
                                            .addOnCompleteListener(registerTask -> {
                                                if (registerTask.isSuccessful()) {
                                                    String uid = registerTask.getResult().getUser().getUid();
                                                    // Đăng ký thành công
                                                    User user = new User(enteredUsername, enteredPassword, username, enteredEmail, uid, "", "", "images/01c05d72-e089-4900-922e-dd6e6c933669");

                                                    // Thêm thông tin người dùng vào Firestore
                                                    db.collection("User").document(username)
                                                            .set(user)
                                                            .addOnSuccessListener(aVoid -> {
                                                                callback.onAccountReceived(username, enteredUsername, enteredPassword, enteredEmail);
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                // Xử lý lỗi
                                                                callback.onAccountError(e);
                                                            });
                                                } else {
                                                    // Nếu đăng ký thất bại, hiển thị thông báo lỗi cho người dùng
                                                    callback.onAccountError(registerTask.getException());
                                                }
                                            });
                                }

                                @Override
                                public void onUsernameError(Exception e) {
                                    callback.onAccountError(e);
                                }
                            });
                        }
                    } else {
                        // Xử lý lỗi khi kiểm tra địa chỉ email
                        callback.onAccountError(task.getException());
                    }
                });
    }

    private void generateUniqueUsername(UsernameCallback usernameCallback) {
        // Retrieve the last document ID from the Firestore collection

        db.collection("User")
                .orderBy("id", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("MyApp","I am here");
                    String lastDocumentId = "user_1"; // Default for the first user
                    if (!queryDocumentSnapshots.isEmpty()) {
                        lastDocumentId = queryDocumentSnapshots.getDocuments().get(0).getString("id");
                    }

                    // Increment the last document ID to generate a new unique username
                    String newUsername = incrementDocumentId(lastDocumentId);

                    usernameCallback.onUsernameGenerated(newUsername);
                })
                .addOnFailureListener(e -> {
                    // Handle the error when querying the collection
                    usernameCallback.onUsernameError(e);
                });
    }

    private String incrementDocumentId(String lastDocumentId) {
        // Parse the last document ID and increment it
        try {
            String[] parts = lastDocumentId.split("_");
            int lastNumber = Integer.parseInt(parts[1]);
            int newNumber = lastNumber + 1;
            return String.valueOf("user_"+newNumber);
        } catch (NumberFormatException e) {
            // Handle the case where the document ID is not numeric
            return "user_1"; // Default for the first document
        }
    }

}


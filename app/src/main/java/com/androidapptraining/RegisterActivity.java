package com.androidapptraining;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText email, password, name;
    Button signUp;
    TextView login;
    FirebaseAuth mAuth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        signUp = findViewById(R.id.signUpBtn);
        login = findViewById(R.id.login);


        signUp.setOnClickListener(v -> {
            pd = new ProgressDialog(RegisterActivity.this);
            pd.setMessage("Creating your account");
            pd.show();
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            String nameText = name.getText().toString();
            mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", mAuth.getCurrentUser().getUid());
                            hashMap.put("name", nameText);
                            hashMap.put("email", emailText);
                            hashMap.put("image_url", "https://firebasestorage.googleapis.com/v0/b/android-app-training.appspot.com/o/profile%2Fuser_image.png?alt=media&token=737ac05e-46d5-49a1-aaad-d4565ab7e646");
                            hashMap.put("password", passwordText);
                            reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                                pd.dismiss();
                                if (task1.isSuccessful()) {
                                    Intent intent = new Intent(RegisterActivity.this, ChatActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    });
        });
    }
}
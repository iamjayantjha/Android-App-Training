package com.androidapptraining;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText email, password;
    Button login;
    TextView signUp,forgotPassword;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.signUpBtn);
        signUp = findViewById(R.id.login);
        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            if (TextUtils.isEmpty(emailText)) {
                email.setError("Email is required");
            }else {
                mAuth.sendPasswordResetEmail(emailText).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Dialog dialog = new Dialog(this);
                        dialog.setContentView(R.layout.dialog_layout);
                        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.background));
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(false);
                        TextView message = dialog.findViewById(R.id.message);
                        TextView heading = dialog.findViewById(R.id.heading);
                        Button okayBtn = dialog.findViewById(R.id.okayBtn);
                        message.setText("Password reset link has been sent to your email");
                        heading.setText("Forgot Password");
                        okayBtn.setOnClickListener(v1 -> {
                            dialog.dismiss();
                        });
                        dialog.show();
                    }
                });
            }
        });
        login.setOnClickListener(v -> {
            pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Logging in");
            pd.show();
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        });
        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
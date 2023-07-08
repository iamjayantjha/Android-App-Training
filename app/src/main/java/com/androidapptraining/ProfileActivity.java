package com.androidapptraining;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidapptraining.Modal.Users;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ImageView user_profile_picture;
    EditText name, email,userName, phone, bio;
    MaterialCardView profile_picture;
    Uri imageUri;
    StorageTask uploadTask;
    DatabaseReference reference;
    StorageReference storageReference;
    TextView saveBtn, logOut;
    String imageUrl;
    boolean updateProfile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userName = findViewById(R.id.userName);
        phone = findViewById(R.id.phone);
        bio = findViewById(R.id.bio);
        saveBtn = findViewById(R.id.saveBtn);
        logOut = findViewById(R.id.logOut);
        profile_picture = findViewById(R.id.profile_picture);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        user_profile_picture = findViewById(R.id.user_profile_picture);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        profile_picture.setOnClickListener(v -> {
            choosePicture();
        });
        readUserData();
        saveBtn.setOnClickListener(v -> {
            if(updateProfile) {
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Updating your profile...");
                progressDialog.show();
                updateProfile(progressDialog);
            }else {
                Toast.makeText(this, "No changes made", Toast.LENGTH_SHORT).show();
            }
        });
        logOut.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            ProfileActivity.this.finish();
        });
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Users users = dataSnapshot.getValue(Users.class);
                                assert users != null;
                                if(users.getUserName() != null){
                                    if (users.getUserName().equals(userName.getText().toString()) && !users.getId().equals(currentUser.getUid())){
                                        updateProfile = false;
                                        userName.setError("Username already exists");
                                        break;
                                    }
                                    else {
                                        updateProfile = true;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateProfile(ProgressDialog progressDialog) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("name", name.getText().toString());
        hashMap.put("id", currentUser.getUid());
        hashMap.put("email", email.getText().toString());
        hashMap.put("userName", userName.getText().toString());
        hashMap.put("phone", phone.getText().toString());
        hashMap.put("bio", bio.getText().toString());
        hashMap.put("image_url", imageUrl);
        reference.setValue(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                ProfileActivity.this.finish();
            }
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data.getData() != null) && (requestCode == 1) && (resultCode == RESULT_OK)){
            user_profile_picture.setImageURI(data.getData());
            imageUri = data.getData();
            uploadPicture();
        }
    }

    private void uploadPicture() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        storageReference = FirebaseStorage.getInstance().getReference("profile").child(currentUser.getUid() + ".jpg");
        uploadTask = storageReference.putFile(imageUri);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
        }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String mUri = downloadUri.toString();
                imageUrl = mUri;
                reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
                reference.child("image_url").setValue(mUri);
                progressDialog.dismiss();
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "Error "+task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

        private void readUserData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                name.setText(user.getName());
                email.setText(user.getEmail());
                imageUrl = user.getImage_url();
                userName.setText(user.getUserName());
                phone.setText(user.getPhone());
                bio.setText(user.getBio());
                Glide.with(getApplicationContext()).load(user.getImage_url()).into(user_profile_picture);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void removeProfilePicture(View view) {
        storageReference = FirebaseStorage.getInstance().getReference("profile").child(currentUser.getUid() + ".jpg");
        storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    imageUrl = "https://firebasestorage.googleapis.com/v0/b/android-app-training.appspot.com/o/profile%2Fuser_image.png?alt=media&token=737ac05e-46d5-49a1-aaad-d4565ab7e646";
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("image_url", imageUrl);
                    hashMap.put("id", currentUser.getUid());
                    hashMap.put("name", name.getText().toString());
                    hashMap.put("email", email.getText().toString());
                    hashMap.put("userName", userName.getText().toString());
                    hashMap.put("phone", phone.getText().toString());
                    hashMap.put("bio", bio.getText().toString());
                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ProfileActivity.this, "Profile Picture Removed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
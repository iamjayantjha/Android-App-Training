package com.androidapptraining;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidapptraining.Adapter.MessageAdapter;
import com.androidapptraining.Modal.Users;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    TextView welcomeMessage;
    ImageView user_profile_picture;
    MaterialCardView profile_picture,addChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        welcomeMessage = findViewById(R.id.welcomeMessage);
        profile_picture = findViewById(R.id.profile_picture);
        mAuth = FirebaseAuth.getInstance();
        addChat = findViewById(R.id.addChat);
        currentUser = mAuth.getCurrentUser();
        user_profile_picture = findViewById(R.id.user_profile_picture);
        profile_picture.setOnClickListener(v -> {
            Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(profile_picture, "profile_picture");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
            startActivity(profile, options.toBundle());
        });
        readUserData();
        addChat.setOnClickListener(v -> {
            Intent addChat = new Intent(getApplicationContext(), ChatActivity.class);
            startActivity(addChat);
        });
    }

    private void readUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                welcomeMessage.setText("Welcome, " + user.getName());
                Glide.with(getApplicationContext()).load(user.getImage_url()).into(user_profile_picture);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
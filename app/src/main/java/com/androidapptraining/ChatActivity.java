package com.androidapptraining;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidapptraining.Adapter.MessageAdapter;
import com.androidapptraining.Modal.Message;
import com.androidapptraining.Modal.Users;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    TextView sendBtn,chatTitle;
    RecyclerView recyclerView;
    EditText messageText;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    String url;
    int randomId = (int) (Math.random() * 1000000000);

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(120, TimeUnit.SECONDS)
            .build();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sendBtn = findViewById(R.id.sendBtn);
        messageText = findViewById(R.id.messageText);
        recyclerView = findViewById(R.id.recyclerView);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        chatTitle = findViewById(R.id.chatTitle);
        messageText.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_NEXT ) {
                String messageToSend = messageText.getText().toString();
                if (!messageToSend.isEmpty()) {
                    messageText.setText("");
                    addToChat(messageToSend, Message.SEND_BY_USER);
                    callOpenAILibrary(messageToSend);
                    handled = true;
                }
            }
            return handled;
        });
        FirebaseCrashlytics.getInstance().setUserId(currentUser.getUid());
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        TextView message = dialog.findViewById(R.id.message);
        TextView heading = dialog.findViewById(R.id.heading);
        Button okayBtn = dialog.findViewById(R.id.okayBtn);
        message.setText("Welcome to Android App Training");
        heading.setText("Welcome");
        if (currentUser == null) {
            message.setText("User not authenticated");
            heading.setText("Authentication");
            dialog.show();
            okayBtn.setOnClickListener(v -> {
                dialog.dismiss();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            });

        }else {
            messageList = new ArrayList<>();
            readUserData();
        }

        sendBtn.setOnClickListener(v -> {
            String messageToSend = messageText.getText().toString();
            if (messageToSend.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
            } else {
                messageText.setText("");
                addToChat(messageToSend, Message.SEND_BY_USER);
                callOpenAILibrary(messageToSend);
            }
        });


    }
    void addToChat(String message, String sendBy) {
        runOnUiThread(()->{
        Message messageObject = new Message(message, sendBy);
        messageList.add(messageObject);
        messageAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
        if (!message.equalsIgnoreCase("Thinking...")){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats").child(randomId + "");
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("message", message);
            hashMap.put("sendBy", sendBy);
            databaseReference.push().setValue(hashMap);
        }
    });
}
    void callOpenAILibrary(String message){
        addToChat("Thinking...", Message.SEND_BY_BOT);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-3.5-turbo");
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("role", "user");
            jsonObject1.put("content", message);
            jsonArray.put(jsonObject1);
            jsonObject.put("messages", jsonArray);
        }catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization","Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                messageList.remove(messageList.size()-1);
                addToChat("Sorry can you say that again? "+e.getMessage(), Message.SEND_BY_BOT);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseString = response.body().string();
                    try {
                       JSONObject jsonObject1 = new JSONObject(responseString);
                       String text = jsonObject1.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                        messageList.remove(messageList.size()-1);
                       addToChat(text.trim(), Message.SEND_BY_BOT);
                       if (chatTitle.getText().toString().equalsIgnoreCase("New Chat")){
                           getChatTitle(message);
                       }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    messageList.remove(messageList.size()-1);
                    addToChat("Sorry can you say that again? "+response.body(), Message.SEND_BY_BOT);
                }
            }
        });
    }
    private void readUserData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                url = user.getImage_url();
                messageAdapter = new MessageAdapter(messageList,url, getApplicationContext());
                recyclerView.setAdapter(messageAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getChatTitle(String msg){
        Log.e("TAG", "getChatTitle: "+msg);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-3.5-turbo");
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("role", "user");
            jsonObject1.put("content", "Generate a title for this chat having message: "+msg+" within 4 to 5 words");
            jsonArray.put(jsonObject1);
            jsonObject.put("messages", jsonArray);
        }catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization","Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("TAG", "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseString = response.body().string();
                    try {
                        JSONObject jsonObject1 = new JSONObject(responseString);
                        String text = jsonObject1.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                        runOnUiThread(()->{
                            chatTitle.setText(text.trim());
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Log.e("TAG", "onResponse: "+response.body());
                }
            }
        });
    }
}
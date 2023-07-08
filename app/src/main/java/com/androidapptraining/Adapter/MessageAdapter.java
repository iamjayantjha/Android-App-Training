package com.androidapptraining.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapptraining.Modal.Message;
import com.androidapptraining.R;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    List<Message> messageList;
    String url;
    Context mContext;


    public MessageAdapter(List<Message> messageList, String url, Context mContext) {
        this.messageList = messageList;
        this.url = url;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(chatView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (message.getSendBy().equals(Message.SEND_BY_USER)) {
            holder.receiver.setVisibility(View.GONE);
            holder.sender.setVisibility(View.VISIBLE);
            holder.senderMessage.setText(message.getMessage());
            holder.userImg.setVisibility(View.VISIBLE);
            Glide.with(holder.userImg.getContext()).load(url).into(holder.userImg);
        } else {
            holder.sender.setVisibility(View.GONE);
            holder.receiver.setVisibility(View.VISIBLE);
            holder.receiverMessage.setText(message.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout receiver,sender;
        TextView receiverMessage,senderMessage;
        CircleImageView userImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            receiver = itemView.findViewById(R.id.receiver);
            sender = itemView.findViewById(R.id.sender);
            receiverMessage = itemView.findViewById(R.id.receiverMessage);
            senderMessage = itemView.findViewById(R.id.senderMessage);
            userImg = itemView.findViewById(R.id.userImg);
        }
    }
}

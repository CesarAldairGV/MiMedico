package com.example.mimedico.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mimedico.ChatUser;
import com.example.mimedico.R;
import com.example.mimedico.model.Message;
import com.example.mimedico.model.Roles;

import java.util.List;

import lombok.NoArgsConstructor;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ChatUserHolder> {

    public class ChatUserHolder extends RecyclerView.ViewHolder{

        private TextView messageText;

        public ChatUserHolder(@NonNull View itemView, int type) {
            super(itemView);
            if(type == USER){
                messageText = itemView.findViewById(R.id.chatEndMessage);
            }else{
                messageText = itemView.findViewById(R.id.chatStartMessage);
            }
        }

        public void bindData(Message message){
            messageText.setText(message.getMessage());
        }
    }

    private int USER = 1;
    private int MEDIC = 2;
    private List<Message> messages;
    private Context context;
    private LayoutInflater layoutInflater;

    public ChatUserAdapter(List<Message> messages, Context context){
        this.messages = messages;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ChatUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == USER) {
            view = layoutInflater.inflate(R.layout.chat_message_end, parent, false);
        }else{
            view = layoutInflater.inflate(R.layout.chat_message_start, parent, false);
        }
        return new ChatUserAdapter.ChatUserHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserHolder holder, int position) {
        holder.bindData(messages.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getRole().equals(Roles.USER.toString())){
            return USER;
        }else{
            return MEDIC;
        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }
}

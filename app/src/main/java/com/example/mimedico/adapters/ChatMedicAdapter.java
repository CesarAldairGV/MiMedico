package com.example.mimedico.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mimedico.R;
import com.example.mimedico.model.Message;
import com.example.mimedico.model.Roles;

import java.util.List;

public class ChatMedicAdapter extends RecyclerView.Adapter<ChatMedicAdapter.ChatMedicHolder> {
    public class ChatMedicHolder extends RecyclerView.ViewHolder{

        private TextView messageText;

        public ChatMedicHolder(@NonNull View itemView, int type) {
            super(itemView);
            if(type == MEDIC){
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

    public ChatMedicAdapter(List<Message> messages, Context context){
        this.messages = messages;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ChatMedicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == MEDIC) {
            view = layoutInflater.inflate(R.layout.chat_message_end, parent, false);
        }else{
            view = layoutInflater.inflate(R.layout.chat_message_start, parent, false);
        }
        return new ChatMedicAdapter.ChatMedicHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMedicHolder holder, int position) {
        holder.bindData(messages.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getRole().equals(Roles.MEDIC.toString())){
            return MEDIC;
        }else{
            return USER;
        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

}

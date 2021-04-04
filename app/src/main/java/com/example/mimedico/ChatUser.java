package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mimedico.adapters.ChatUserAdapter;
import com.example.mimedico.model.Consult;
import com.example.mimedico.model.Message;
import com.example.mimedico.model.Roles;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChatUser extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;

    private TextView consult, title, medic;

    private EditText messageField;
    private Button sendButton;

    private String consultId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user);

        consult = findViewById(R.id.chatUserText);
        title = findViewById(R.id.chatUserTitle);
        medic = findViewById(R.id.chatUserMedic);

        messageField = findViewById(R.id.chatUserMessage);
        sendButton = findViewById(R.id.chatUserButton);

        Intent intent = getIntent();
        consultId = intent.getStringExtra("consultId");

        firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseDatabase.getReference("consults")
                .child(consultId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Consult consult = snapshot.getValue(Consult.class);
                        ChatUser.this.consult.append(" " + consult.getId());
                        title.append(" " + consult.getSymptomsPetition().getTitle());
                        medic.append(" " + consult.getMedic().getFirstName() + " " + consult.getMedic().getLastName());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        firebaseDatabase.getReference("consults")
                .child(consultId)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                        List<Message> messages = new ArrayList<>();
                        while(iterator.hasNext()){
                            Message message = iterator.next().getValue(Message.class);
                            messages.add(message);
                        }
                        ChatUserAdapter chatUserAdapter = new ChatUserAdapter(messages, ChatUser.this);
                        RecyclerView recyclerView = findViewById(R.id.chatUserList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ChatUser.this));
                        recyclerView.setAdapter(chatUserAdapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        sendButton.setOnClickListener(this::sendMessage);
    }

    public void sendMessage(View view){
        String message = messageField.getText().toString();
        if(message.trim().isEmpty()){
            messageField.setError("Message must not be empty");
            return;
        }
        Message message1 = Message.builder()
                .message(message)
                .role(Roles.USER.toString())
                .build();
        firebaseDatabase.getReference("consults")
                .child(consultId)
                .child("messages")
                .push()
                .setValue(message1);
    }
}
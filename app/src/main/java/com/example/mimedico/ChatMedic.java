package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mimedico.adapters.ChatMedicAdapter;
import com.example.mimedico.adapters.ChatUserAdapter;
import com.example.mimedico.model.Consult;
import com.example.mimedico.model.Message;
import com.example.mimedico.model.Roles;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChatMedic extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;

    private TextView consult, title, patient;

    private EditText messageField;
    private Button sendButton;

    private String consultId;

    private ChatMedicAdapter chatMedicAdapter;
    private RecyclerView recyclerView;
    private List<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_medic);

        consult = findViewById(R.id.chatMedicText);
        title = findViewById(R.id.chatMedicTitle);
        patient = findViewById(R.id.chatMedicUser);

        messageField = findViewById(R.id.chatMedicMessage);
        sendButton = findViewById(R.id.chatMedicButton);

        Intent intent = getIntent();
        consultId = intent.getStringExtra("consultId");

        firebaseDatabase = FirebaseDatabase.getInstance();

        chatMedicAdapter = new ChatMedicAdapter(messages, ChatMedic.this);
        recyclerView = findViewById(R.id.chatMedicList);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatMedic.this));
        recyclerView.setAdapter(chatMedicAdapter);

        firebaseDatabase.getReference("consults")
                .child(consultId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Consult consult = snapshot.getValue(Consult.class);
                        ChatMedic.this.consult.append(" " + consult.getId());
                        title.append(" " + consult.getSymptomsPetition().getTitle());
                        patient.append(" " + consult.getMedic().getFirstName() + " " + consult.getMedic().getLastName());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        /*firebaseDatabase.getReference("consults")
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
                        ChatMedicAdapter chatMedicAdapter = new ChatMedicAdapter(messages, ChatMedic.this);
                        RecyclerView recyclerView = findViewById(R.id.chatMedicList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ChatMedic.this));
                        recyclerView.setAdapter(chatMedicAdapter);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });*/

        firebaseDatabase.getReference("consults")
                .child(consultId)
                .child("messages")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Message message = snapshot.getValue(Message.class);
                        messages.add(message);
                        chatMedicAdapter.notifyItemChanged(messages.size() - 1);
                        recyclerView.scrollToPosition(messages.size() - 1);
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
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
                .role(Roles.MEDIC.toString())
                .build();
        firebaseDatabase.getReference("consults")
                .child(consultId)
                .child("messages")
                .push()
                .setValue(message1)
                .addOnSuccessListener(a->{
                    messageField.setText("");
                    hideKeyboard(this);
                });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
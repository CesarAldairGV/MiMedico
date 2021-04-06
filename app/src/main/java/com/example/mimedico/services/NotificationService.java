package com.example.mimedico.services;

import com.example.mimedico.ChatMedic;
import com.example.mimedico.MedicMessage;
import com.example.mimedico.model.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mimedico.R;
import com.example.mimedico.model.NotificationType;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class NotificationService extends Service {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private Notification lastNotification;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private boolean flag = false;
    private int id;

    public NotificationService() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userId = intent.getStringExtra("userId");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NotificationService.this);
        createNotificationChannel();
        firebaseDatabase.getReference("users")
                .child(userId)
                .child("notifications")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                        if(iterator.hasNext()){
                            while(iterator.hasNext()){
                                lastNotification = iterator.next().getValue(Notification.class);
                            }
                        }else{
                            flag = true;
                        }
                        databaseReference = firebaseDatabase.getReference("users")
                                .child(userId)
                                .child("notifications");
                        childEventListener = new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                if(flag){
                                    Intent intent1;
                                    PendingIntent pendingIntent;
                                    Notification notification = snapshot.getValue(Notification.class);
                                    if(notification.getType().equals(NotificationType.USER_ACCEPT.getType())){
                                        intent1 = new Intent(NotificationService.this, ChatMedic.class);
                                        intent1.putExtra("consultId", notification.getOtherId());
                                        pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
                                    }else{
                                        intent1 = new Intent(NotificationService.this, MedicMessage.class);
                                        intent1.putExtra("petitionId", notification.getOtherId());
                                        intent1.putExtra("messageId", notification.getSecondId());
                                        pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);
                                    }
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this, "CHANNEL_ID")
                                            .setSmallIcon(R.drawable.logo3)
                                            .setContentTitle(notification.getTitle())
                                            .setContentText(notification.getMessage())
                                            .setContentIntent(pendingIntent)
                                            .setAutoCancel(true)
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                    notificationManager.notify(id++, builder.build());
                                    return;
                                }
                                if(lastNotification.getId().equals(snapshot.getValue(Notification.class).getId())){
                                    flag = true;
                                }
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
                        };
                        databaseReference.addChildEventListener(childEventListener);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "You received a message";
            String description = "";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(childEventListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
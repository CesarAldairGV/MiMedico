package com.example.mimedico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.view.View;

import java.util.UUID;

public class CallActivity extends AppCompatActivity {

    private String username, friendUsername;
    private boolean isPeerConnected = false;
    private FirebaseDatabase firebaseRef = FirebaseDatabase.getInstance();
    private boolean isAudio = true;
    private boolean isVideo = true;
    private String uniqueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        username = getIntent().getStringExtra("username");
        friendUsername = getIntent().getStringExtra("username2");

        findViewById(R.id.callBtn).setOnClickListener(v -> {
            sendCallRequest();
        });

        findViewById(R.id.toggleAudioBtn).setOnClickListener(v -> {
            isAudio = !isAudio;
            callJavascriptFunction("javascript:toggleAudio(\""+ isAudio + "\")");
            ImageView toggleAudio = findViewById(R.id.toggleAudioBtn);
            toggleAudio.setImageResource(isAudio ? R.drawable.ic_baseline_mic_24 : R.drawable.ic_baseline_mic_off_24);
        });

        findViewById(R.id.toggleVideoBtn).setOnClickListener(v -> {
            isVideo = !isVideo;
            callJavascriptFunction("javascript:toggleVideo(\""+ isVideo + "\")");
            ImageView toggleVideo = findViewById(R.id.toggleVideoBtn);
            toggleVideo.setImageResource(isVideo ? R.drawable.ic_baseline_videocam_24 : R.drawable.ic_baseline_videocam_off_24);
        });

        setUpWebView();
    }

    private void sendCallRequest(){
        if(!isPeerConnected){
            Toast.makeText(this, "User not connected!",Toast.LENGTH_LONG).show();
            return;
        }
        firebaseRef.getReference("videochat").child(friendUsername).child("incoming").setValue(username);
        firebaseRef.getReference("videochat").child(friendUsername).child("isAvailable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() == null) return;
                if(snapshot.getValue().toString() == "true"){
                    listenForConnId();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void listenForConnId(){
        firebaseRef.getReference("videochat").child(friendUsername).child("connId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() == null) return;
                switchControls();
                callJavascriptFunction("javascript:startCall(\""+ snapshot.getValue().toString() + "\")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setUpWebView(){
        WebView webView = findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if(request != null) request.grant(request.getResources());
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.addJavascriptInterface(new JavascriptInterface(this),"Android");
        webView.getSettings().setDomStorageEnabled(true);
        WebView.setWebContentsDebuggingEnabled(true);


        loadVideoCall();
    }

    private void loadVideoCall(){
        String path = "file:android_asset/call.html";
        WebView webView = findViewById(R.id.webView);
        webView.loadUrl(path);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                initializePeer();
            }
        });
    }

    private void initializePeer(){
        uniqueId = UUID.randomUUID().toString();
        callJavascriptFunction("javascript:init(\""+ uniqueId + "\")");
        firebaseRef.getReference("videochat").child(username).child("incoming").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    onCallRequest(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onCallRequest(String caller){
        if(caller == null) return;
        findViewById(R.id.callLayout).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.incomingCallTxt)).setText(caller + "is calling");

        findViewById(R.id.acceptBtn).setOnClickListener(v -> {
            firebaseRef.getReference("videochat").child(username).child("connId").setValue(uniqueId);
            firebaseRef.getReference("videochat").child(username).child("isAvailable").setValue(true);

            findViewById(R.id.callLayout).setVisibility(View.GONE);
            switchControls();
        });

        findViewById(R.id.rejectBtn).setOnClickListener(v -> {
            firebaseRef.getReference("videochat").child(username).child("incoming").setValue(null);
            findViewById(R.id.callLayout).setVisibility(View.GONE);
        });
    }

    private void switchControls(){
        findViewById(R.id.inputLayout).setVisibility(View.GONE);
        findViewById(R.id.callControlLayout).setVisibility(View.VISIBLE);
    }

    public void onPeerConnected(){
        isPeerConnected = true;
    }

    private void callJavascriptFunction(String functionString){
        findViewById(R.id.webView).post(() -> {
            WebView webView = findViewById(R.id.webView);
            webView.evaluateJavascript(functionString, null);
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onDestroy() {
        firebaseRef.getReference("videochat").child(username).setValue(null);
        ((WebView)findViewById(R.id.webView)).loadUrl("about:blank");
        super.onDestroy();
    }
}
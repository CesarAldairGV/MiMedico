package com.example.mimedico;

public class JavascriptInterface {
    CallActivity callActivity;
    public JavascriptInterface(CallActivity callActivity){
        this.callActivity = callActivity;
    }

    @android.webkit.JavascriptInterface
    public void onPeerConnected(){
        callActivity.onPeerConnected();
    }
}

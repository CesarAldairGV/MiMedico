package com.example.mimedico.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mimedico.CallActivity;
import com.example.mimedico.ChatUser;
import com.example.mimedico.R;
import com.example.mimedico.model.Consult;

import java.util.List;

public class MyMedicsAdapter extends RecyclerView.Adapter<MyMedicsAdapter.MyMedicsHolder> {
    public class MyMedicsHolder extends RecyclerView.ViewHolder{

        private TextView consultId, medicName, petitionTitle, date;
        private Button openChat, openVideochat;
        private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

        public MyMedicsHolder(@NonNull View itemView) {
            super(itemView);

            //consultId = itemView.findViewById(R.id.userConsultId);
            medicName = itemView.findViewById(R.id.userConsultMedic);
            petitionTitle = itemView.findViewById(R.id.userConsultPetitionTitle);
            date = itemView.findViewById(R.id.userConsultDate);

            openChat = itemView.findViewById(R.id.userConsultButton);
            openVideochat = itemView.findViewById(R.id.userConsultVideochat);
        }

        public void bindData(Consult consult){
            //consultId.append(" " + consult.getId());
            medicName.append(" " + consult.getMedic().getFirstName() + " "  + consult.getMedic().getLastName());
            petitionTitle.append(" " + consult.getSymptomsPetition().getTitle());
            date.append(" " + consult.getDate());

            Intent intent = new Intent(context, ChatUser.class);
            intent.putExtra("consultId", consult.getId());
            openChat.setOnClickListener(v -> {
                context.startActivity(intent);
            });
            openVideochat.setOnClickListener(v -> {

                if(!checkPermissions()){
                    askPermissions();
                    if(!checkPermissions()){
                        Toast.makeText(context, "Permissions denied", Toast.LENGTH_SHORT).show();
                    }else{
                        callActivity(consult);
                    }
                }else{
                    callActivity(consult);
                }
            });
        }

        private void callActivity(Consult consult){
            Intent intentVideo = new Intent(context, CallActivity.class);
            intentVideo.putExtra("userId",consult.getUser().getId());
            intentVideo.putExtra("medicId",consult.getMedic().getId());
            intentVideo.putExtra("username",consult.getUser().getUserName());
            intentVideo.putExtra("username2", consult.getMedic().getUserName());
            context.startActivity(intentVideo);
        }

        private boolean checkPermissions(){
            for(String per : permissions){
                if(ActivityCompat.checkSelfPermission(context, per) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
            return true;
        }

        private void askPermissions(){
            ActivityCompat.requestPermissions((Activity)context, permissions, 1);
        }
    }

    private List<Consult> consults;
    private Context context;
    private LayoutInflater layoutInflater;

    public MyMedicsAdapter(List<Consult> consults, Context context){
        this.consults = consults;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public MyMedicsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.user_consult, parent, false);
        return new MyMedicsAdapter.MyMedicsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMedicsHolder holder, int position) {
        holder.bindData(consults.get(position));
    }

    @Override
    public int getItemCount() {
        return consults.size();
    }
}

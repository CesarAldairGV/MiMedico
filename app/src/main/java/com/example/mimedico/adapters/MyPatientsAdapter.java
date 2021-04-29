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
import com.example.mimedico.ChatMedic;
import com.example.mimedico.MyPatients;
import com.example.mimedico.R;
import com.example.mimedico.model.Consult;

import java.util.List;

public class MyPatientsAdapter extends RecyclerView.Adapter<MyPatientsAdapter.MyPatientHolder> {
    public class MyPatientHolder extends RecyclerView.ViewHolder{
        private TextView consultId, patientName, petitionTitle, date;
        private Button openChat, openVideochat;
        private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        public MyPatientHolder(@NonNull View itemView) {
            super(itemView);

            consultId = itemView.findViewById(R.id.medicConsultId);
            patientName = itemView.findViewById(R.id.medicConsultPatient);
            petitionTitle = itemView.findViewById(R.id.medicConsultPetitionTitle);
            date = itemView.findViewById(R.id.medicConsultDate);

            openChat = itemView.findViewById(R.id.medicConsultButton);
            openVideochat = itemView.findViewById(R.id.medicConsultVideochat);
        }

        public void bindData(Consult consult){
            consultId.append(" " + consult.getId());
            patientName.append(" " + consult.getUser().getFirstName() + " " + consult.getUser().getLastName());
            petitionTitle.append(" " + consult.getSymptomsPetition().getTitle());
            date.append(" " + consult.getDate());

            openChat.setOnClickListener(v -> {
                Intent intent = new Intent(context, ChatMedic.class);
                intent.putExtra("consultId",consult.getId());
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
            intentVideo.putExtra("username",consult.getMedic().getUserName());
            intentVideo.putExtra("username2", consult.getUser().getUserName());
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

    public MyPatientsAdapter(List<Consult> consults, Context context){
        this.consults = consults;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyPatientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.medic_consult, parent, false);
        return new MyPatientsAdapter.MyPatientHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPatientHolder holder, int position) {
        holder.bindData(consults.get(position));
    }

    @Override
    public int getItemCount() {
        return consults.size();
    }

}

package com.example.mimedico.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mimedico.ChatMedic;
import com.example.mimedico.MyPatients;
import com.example.mimedico.R;
import com.example.mimedico.model.Consult;

import java.util.List;

public class MyPatientsAdapter extends RecyclerView.Adapter<MyPatientsAdapter.MyPatientHolder> {
    public class MyPatientHolder extends RecyclerView.ViewHolder{
        private TextView consultId, patientName, petitionTitle, date;
        private Button openChat;
        public MyPatientHolder(@NonNull View itemView) {
            super(itemView);

            consultId = itemView.findViewById(R.id.medicConsultId);
            patientName = itemView.findViewById(R.id.medicConsultPatient);
            petitionTitle = itemView.findViewById(R.id.medicConsultPetitionTitle);
            date = itemView.findViewById(R.id.medicConsultDate);

            openChat = itemView.findViewById(R.id.medicConsultButton);
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

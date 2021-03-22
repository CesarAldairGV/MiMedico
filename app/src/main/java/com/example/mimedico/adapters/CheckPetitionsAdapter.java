package com.example.mimedico.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mimedico.R;
import com.example.mimedico.model.SymptomsPetition;

import java.util.List;

public class CheckPetitionsAdapter extends RecyclerView.Adapter<CheckPetitionsAdapter.CheckPetitionsHolder> {
    public class CheckPetitionsHolder extends RecyclerView.ViewHolder{
        private TextView checkPetitionTitle, checkPetitionDescription, checkPetitionDate;
        private Button sendMessageButton;
        private View itemView;

        public CheckPetitionsHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            checkPetitionTitle = itemView.findViewById(R.id.checkPetitionTitle);
            checkPetitionDescription = itemView.findViewById(R.id.checkPetitionDescription);
            checkPetitionDate = itemView.findViewById(R.id.checkPetitionDate);

            sendMessageButton = itemView.findViewById(R.id.checkPetitionSendButton);
        }

        public void bindData(SymptomsPetition symptomsPetition){
            checkPetitionTitle.setText(symptomsPetition.getTitle());
            checkPetitionDescription.setText(symptomsPetition.getDescription());
            checkPetitionDate.setText(symptomsPetition.getPetitionDate());
            sendMessageButton.setOnClickListener(v -> Toast.makeText(itemView.getContext(), symptomsPetition.getTitle(), Toast.LENGTH_LONG).show());
        }
    }

    private List<SymptomsPetition> symptomsPetitionList;
    private Context context;
    private LayoutInflater layoutInflater;

    public CheckPetitionsAdapter(List<SymptomsPetition> symptomsPetitions, Context context){
        this.symptomsPetitionList = symptomsPetitions;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CheckPetitionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.check_petitions_list, parent, false);
        return new CheckPetitionsAdapter.CheckPetitionsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckPetitionsHolder holder, int position) {
        holder.bindData(symptomsPetitionList.get(position));
    }

    @Override
    public int getItemCount() {
        return symptomsPetitionList.size();
    }

}

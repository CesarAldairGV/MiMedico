package com.example.mimedico.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mimedico.R;
import com.example.mimedico.model.SymptomsPetition;

import java.util.List;

import lombok.AllArgsConstructor;

public class MyPetitionsAdapter extends RecyclerView.Adapter<MyPetitionsAdapter.MyPetitionsHolder> {

    public class MyPetitionsHolder extends RecyclerView.ViewHolder{

        private TextView petitionDescription, petitionDate, petitionAccepted, petitionTitle;

        public MyPetitionsHolder(@NonNull View itemView) {
            super(itemView);
            petitionDescription = itemView.findViewById(R.id.petitionDescription);
            petitionDate = itemView.findViewById(R.id.petitionDate);
            petitionAccepted = itemView.findViewById(R.id.petitionAccepted);
            petitionTitle = itemView.findViewById(R.id.petitionTitle);
        }

        public void bindData(SymptomsPetition symptomsPetition){
            petitionTitle.setText(symptomsPetition.getTitle());
            petitionDescription.setText(symptomsPetition.getDescription());
            petitionDate.setText(symptomsPetition.getPetitionDate().toString());
            petitionAccepted.setText(symptomsPetition.isPetitionAccepted() ? "Accepted" : "No Accepted");
        }
    }

    private List<SymptomsPetition> symptomsPetitionList;
    private LayoutInflater layoutInflater;
    private Context context;

    public MyPetitionsAdapter(List<SymptomsPetition> symptomsPetitions, Context context){
        this.symptomsPetitionList = symptomsPetitions;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyPetitionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.my_petitions_list, parent,false);
        return new MyPetitionsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPetitionsHolder holder, int position) {
        holder.bindData(symptomsPetitionList.get(position));
    }

    @Override
    public int getItemCount() {
        return symptomsPetitionList.size();
    }

}

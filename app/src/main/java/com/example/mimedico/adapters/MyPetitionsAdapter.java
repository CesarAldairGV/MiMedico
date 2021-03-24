package com.example.mimedico.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mimedico.R;
import com.example.mimedico.model.SymptomsPetition;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyPetitionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public class MyPetitionsHolder extends RecyclerView.ViewHolder{

        private TextView petitionDescription, petitionDate, petitionAccepted, petitionTitle;
        private ProgressBar progressBar;
        private ImageView imageView;

        public MyPetitionsHolder(@NonNull View itemView) {
            super(itemView);
            petitionDescription = itemView.findViewById(R.id.petitionDescription);
            petitionDate = itemView.findViewById(R.id.petitionDate);
            petitionAccepted = itemView.findViewById(R.id.petitionAccepted);
            petitionTitle = itemView.findViewById(R.id.petitionTitle);
            imageView = itemView.findViewById(R.id.petitionImage);
            progressBar = itemView.findViewById(R.id.petitionProgressBar);
        }

        public void bindData(SymptomsPetition symptomsPetition){
            petitionTitle.setText(symptomsPetition.getTitle());
            petitionDescription.setText(symptomsPetition.getDescription());
            petitionDate.setText(symptomsPetition.getPetitionDate().toString());
            petitionAccepted.setText(symptomsPetition.isPetitionAccepted() ? "Accepted" : "No Accepted");
            if(symptomsPetition.isImage()){
                progressBar.setVisibility(View.VISIBLE);
                Picasso.get().load(symptomsPetition.getImageUri()).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        }
    }

    private final int LOADING_TYPE = 0;
    private final int SYMPTOM_TYPE = 1;
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.my_petitions_list, parent, false);
            return new MyPetitionsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyPetitionsHolder)
            ((MyPetitionsHolder)holder).bindData(symptomsPetitionList.get(position));
    }

    @Override
    public int getItemCount() {
        return symptomsPetitionList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return symptomsPetitionList.get(position) == null ? LOADING_TYPE : SYMPTOM_TYPE;
    }
}

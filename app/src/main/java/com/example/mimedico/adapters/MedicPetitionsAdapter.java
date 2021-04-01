package com.example.mimedico.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mimedico.MedicPetition;
import com.example.mimedico.R;
import com.example.mimedico.model.User;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MedicPetitionsAdapter extends RecyclerView.Adapter<MedicPetitionsAdapter.MedicPetitionHolder> {

    public class MedicPetitionHolder extends RecyclerView.ViewHolder{

        private TextView name, institution, years;
        private ProgressBar proofBar, photoBar;
        private ImageView proofView, photoView;
        private Button view;

        public MedicPetitionHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.petitionProofMedicName);
            institution = itemView.findViewById(R.id.petitionProofInstitution);
            years = itemView.findViewById(R.id.petitionProofYears);
            proofBar = itemView.findViewById(R.id.petitionProofProgressBar);
            photoBar = itemView.findViewById(R.id.petitionPhotoProgressBar);
            proofView = itemView.findViewById(R.id.petitionProofView);
            photoView = itemView.findViewById(R.id.petitionPhotoView);
            view = itemView.findViewById(R.id.petitionProofViewButton);
        }

        public void bindData(User medic){
            name.setText(medic.getFirstName() + " " + medic.getLastName());
            institution.setText(medic.getInstitution());
            years.setText(medic.getYearsOfExperience() + " Years Of Experience");

            view.setOnClickListener(v -> {
                Intent intent = new Intent(context, MedicPetition.class);
                intent.putExtra("userId",medic.getId());
                context.startActivity(intent);
            });

            proofBar.setVisibility(View.VISIBLE);
            photoBar.setVisibility(View.VISIBLE);
            Picasso.get().load(medic.getMedicProofUrl()).into(proofView, new Callback() {
                @Override
                public void onSuccess() {
                    proofView.setVisibility(View.VISIBLE);
                    proofBar.setVisibility(View.GONE);
                }
                @Override
                public void onError(Exception e) {
                }
            });
            Picasso.get().load(medic.getUserPhotoUrl()).into(photoView, new Callback() {
                @Override
                public void onSuccess() {
                    photoView.setVisibility(View.VISIBLE);
                    photoBar.setVisibility(View.GONE);
                }
                @Override
                public void onError(Exception e) {
                }
            });
        }
    }


    private List<User> medicList;
    private Context context;
    private LayoutInflater layoutInflater;

    public MedicPetitionsAdapter(List<User> medics, Context context){
        this.medicList = medics;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MedicPetitionsAdapter.MedicPetitionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.medic_petition_proof_list, parent, false);
        return new MedicPetitionsAdapter.MedicPetitionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicPetitionsAdapter.MedicPetitionHolder holder, int position) {
        holder.bindData(medicList.get(position));
    }

    @Override
    public int getItemCount() {
        return medicList.size();
    }
}

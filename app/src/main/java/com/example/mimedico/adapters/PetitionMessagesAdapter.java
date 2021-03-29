package com.example.mimedico.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mimedico.R;
import com.example.mimedico.model.SymptomsPetitionMessage;

import java.util.List;

public class PetitionMessagesAdapter extends RecyclerView.Adapter<PetitionMessagesAdapter.PetitionMessagesHolder> {

    public class PetitionMessagesHolder extends RecyclerView.ViewHolder{

        private TextView description, doctorName, date;
        private Button viewButton;

        public PetitionMessagesHolder(@NonNull View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.petitionMessageDescription);
            doctorName = itemView.findViewById(R.id.petitionMessageMedicName);
            date = itemView.findViewById(R.id.petitionMessageDate);
            viewButton = itemView.findViewById(R.id.petitionMessageViewCompleteMessage);

        }

        public void bindData(SymptomsPetitionMessage symptomsPetitionsMessage){
            description.setText(symptomsPetitionsMessage.getMessage());
            doctorName.setText(symptomsPetitionsMessage.getMedic().getFirstName() + " " + symptomsPetitionsMessage.getMedic().getLastName());
            date.setText(symptomsPetitionsMessage.getDate());
        }
    }

    private List<SymptomsPetitionMessage> symptomsPetitionsMessages;
    private Context context;
    private LayoutInflater layoutInflater;

    public PetitionMessagesAdapter(List<SymptomsPetitionMessage> symptomsPetitionsMessages, Context context){
        this.symptomsPetitionsMessages = symptomsPetitionsMessages;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PetitionMessagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.petitions_message_list, parent, false);
        return new PetitionMessagesAdapter.PetitionMessagesHolder(view);
    }

    @Override
    public int getItemCount() {
        return symptomsPetitionsMessages.size();
    }

    @Override
    public void onBindViewHolder(@NonNull PetitionMessagesHolder holder, int position) {
        holder.bindData(symptomsPetitionsMessages.get(position));
    }
}

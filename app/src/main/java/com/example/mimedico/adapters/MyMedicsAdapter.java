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

import com.example.mimedico.ChatUser;
import com.example.mimedico.R;
import com.example.mimedico.model.Consult;

import java.util.List;

public class MyMedicsAdapter extends RecyclerView.Adapter<MyMedicsAdapter.MyMedicsHolder> {
    public class MyMedicsHolder extends RecyclerView.ViewHolder{

        private TextView consultId, medicName, petitionTitle, date;
        private Button openChat;

        public MyMedicsHolder(@NonNull View itemView) {
            super(itemView);

            consultId = itemView.findViewById(R.id.userConsultId);
            medicName = itemView.findViewById(R.id.userConsultMedic);
            petitionTitle = itemView.findViewById(R.id.userConsultPetitionTitle);
            date = itemView.findViewById(R.id.userConsultDate);

            openChat = itemView.findViewById(R.id.userConsultButton);
        }

        public void bindData(Consult consult){
            consultId.append(" " + consult.getId());
            medicName.append(" " + consult.getMedic().getFirstName() + " "  + consult.getMedic().getLastName());
            petitionTitle.append(" " + consult.getSymptomsPetition().getTitle());
            date.append(" " + consult.getDate());

            Intent intent = new Intent(context, ChatUser.class);
            intent.putExtra("consultId", consult.getId());
            openChat.setOnClickListener(v -> {
                context.startActivity(intent);
            });
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

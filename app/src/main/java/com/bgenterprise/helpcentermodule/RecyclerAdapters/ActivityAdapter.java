package com.bgenterprise.helpcentermodule.RecyclerAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.R;


import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter <ActivityAdapter.ViewHolder>{
    private List<QuestionsEnglish> questionList;
    private final OnItemClickListener listener;
    private Context context;

    public ActivityAdapter(Context context, List<QuestionsEnglish> questionList, OnItemClickListener listener) {
        this.questionList = questionList;
        this.listener = listener;
        this.context = context;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.help_center_activities_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionsEnglish issues = questionList.get(position);
        holder.questionView.setText(issues.getActivity_name());
    }

    public interface OnItemClickListener{
        void onClick(QuestionsEnglish questionsEnglish);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView questionView;
        public CardView itemize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionView =itemView.findViewById(R.id.questionView);
            itemize = itemView.findViewById(R.id.itemize);

            itemView.setOnClickListener(view -> {
                listener.onClick(questionList.get(getLayoutPosition()));
            });
        }

    }
}

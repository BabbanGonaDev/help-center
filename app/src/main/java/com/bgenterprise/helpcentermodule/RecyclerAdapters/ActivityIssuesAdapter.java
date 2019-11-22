package com.bgenterprise.helpcentermodule.RecyclerAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.QuestionsAll;
import com.bgenterprise.helpcentermodule.R;

import java.util.List;

public class ActivityIssuesAdapter extends RecyclerView.Adapter<ActivityIssuesAdapter.ViewHolder> {
    List<QuestionsAll> IssuesList;
    private final OnItemClickListener listener;
    private Context context;

    public ActivityIssuesAdapter(Context context, List<QuestionsAll> issuesList, OnItemClickListener listener) {
        IssuesList = issuesList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.help_center_activityissues_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionsAll issues = IssuesList.get(position);
        holder.activityIssues.setText(issues.getIssue_question());
    }


    public interface OnItemClickListener{
        void onClick(QuestionsAll questionsEnglish);
    }


    @Override
    public int getItemCount() {
        return IssuesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView activityIssues;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            activityIssues = itemView.findViewById(R.id.activityIssues);

            itemView.setOnClickListener(v -> {
                listener.onClick(IssuesList.get(getLayoutPosition()));
            });

        }
    }
}

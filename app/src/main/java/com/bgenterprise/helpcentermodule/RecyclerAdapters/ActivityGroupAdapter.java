package com.bgenterprise.helpcentermodule.RecyclerAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;

import com.bgenterprise.helpcentermodule.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;


public class ActivityGroupAdapter extends RecyclerView.Adapter<ActivityGroupAdapter.ActivityGroupViewHolder>{

    private Context mCtx;
    private final OnItemClickListener listener;
    private List<QuestionsEnglish> issuesList;

    public ActivityGroupAdapter(Context context, List<QuestionsEnglish> issuesList, OnItemClickListener listener) {
        this.mCtx = context;
        this.issuesList = issuesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActivityGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.help_center_activitygroup_card, parent, false);
        return new ActivityGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityGroupViewHolder holder, int position) {
        QuestionsEnglish issues = issuesList.get(position);
        holder.mcv_ActivityGroup_category.setText(issues.getActivity_group_id());
    }

    @Override
    public int getItemCount() {
        return issuesList.size();
    }

    public interface OnItemClickListener{
        void onClick(QuestionsEnglish issuesList);
    }

    public class ActivityGroupViewHolder extends RecyclerView.ViewHolder{

        public MaterialTextView mcv_ActivityGroup_category;

        public ActivityGroupViewHolder(View view){
            super(view);
            mcv_ActivityGroup_category = view.findViewById(R.id.mtv_activitygroup_category);

            view.setOnClickListener(view1 -> {
                listener.onClick(issuesList.get(getLayoutPosition()));
            });
        }
    }
}

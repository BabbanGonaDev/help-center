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


public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {

    private Context context;
    private final OnItemClickListener listener;
    private List<QuestionsEnglish> faqList;

    /**
     * TODO: Use the Recyclers from LMTC app as a guide
     */

    public FAQAdapter(Context context, List<QuestionsEnglish> faqList, OnItemClickListener listener) {
        this.context = context;
        this.faqList = faqList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.help_center_faq_card, parent, false);
        return new FAQViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder holder, int position) {
        QuestionsEnglish issue = faqList.get(position);
        holder.mcv_FAQ_headline.setText(issue.getIssue_question());

    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    public interface OnItemClickListener{
        void onClick(QuestionsEnglish issuesList);
    }

    public class FAQViewHolder extends RecyclerView.ViewHolder{

        public MaterialTextView mcv_FAQ_headline;

        public FAQViewHolder(View itemView) {
            super(itemView);
            mcv_FAQ_headline = itemView.findViewById(R.id.mtv_faq_headline_card);

            itemView.setOnClickListener(view -> {
                listener.onClick(faqList.get(getLayoutPosition()));
            });
        }
    }
}

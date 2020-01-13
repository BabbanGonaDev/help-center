package com.bgenterprise.helpcentermodule.Database.Tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "negative_feedback_dropdown")
public class NegativeDropdown {
    @PrimaryKey
    @NonNull
    private String dropdown_id;

    private String language_id;
    private String dropdown_reason;

    public NegativeDropdown(@NonNull String dropdown_id, String language_id, String dropdown_reason){
        this.dropdown_id = dropdown_id;
        this.language_id = language_id;
        this.dropdown_reason = dropdown_reason;
    }

    @NonNull
    public String getDropdown_id() {
        return dropdown_id;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public String getDropdown_reason() {
        return dropdown_reason;
    }
}

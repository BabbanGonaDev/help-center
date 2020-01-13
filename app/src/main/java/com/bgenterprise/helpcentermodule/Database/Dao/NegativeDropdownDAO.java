package com.bgenterprise.helpcentermodule.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bgenterprise.helpcentermodule.Database.Tables.NegativeDropdown;

import java.util.List;

@Dao
public interface NegativeDropdownDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertDropdown(List<NegativeDropdown> negativeDropdown);

    @Update
    void UpdateDropdown(NegativeDropdown... negativeDropdown);

    @Delete
    void DeleteDropdown(NegativeDropdown negativeDropdown);

    @Query("SELECT dropdown_reason FROM negative_feedback_dropdown WHERE language_id = :language")
    List<String> getNegativeReason(String language);
}

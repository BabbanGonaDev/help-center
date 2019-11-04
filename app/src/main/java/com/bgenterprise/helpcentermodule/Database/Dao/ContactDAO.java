package com.bgenterprise.helpcentermodule.Database.Dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bgenterprise.helpcentermodule.Database.Tables.SupportContact;

import java.util.List;

@Dao
public interface ContactDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertContact(SupportContact... supportContacts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFromCSV(List<SupportContact> supportContactList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFromOnline(SupportContact supportContact);

    @Update
    void UpdateContact(SupportContact supportContact);

    @Delete
    void DeleteContact(SupportContact supportContact);

    @Query("SELECT phone_number FROM contact_support WHERE location = :location")
    String getPhoneNumber(String location);

    @Query("SELECT whatsapp_number FROM contact_support WHERE location = :location")
    String getWhatsappNumber(String location);
}

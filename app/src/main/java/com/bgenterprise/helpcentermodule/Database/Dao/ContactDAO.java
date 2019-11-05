package com.bgenterprise.helpcentermodule.Database.Dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.bgenterprise.helpcentermodule.Database.Tables.ContactSupport;

import java.util.List;

@Dao
public interface ContactDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertContact(ContactSupport... contactSupports);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertFromOnline(List<ContactSupport> contactSupport);

    @Update
    void UpdateContact(ContactSupport contactSupport);

    @Delete
    void DeleteContact(ContactSupport contactSupport);

    @Query("SELECT phone_number FROM contact_support WHERE location = :location")
    String getPhoneNumber(String location);

    @Query("SELECT whatsapp_number FROM contact_support WHERE location = :location")
    String getWhatsappNumber(String location);
}

package com.bgenterprise.helpcentermodule.Database.Tables;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "contact_support", indices = {@Index(value = "location", unique = true)})
public class ContactSupport {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String location;
    private String whatsapp_number;
    private String phone_number;

    public ContactSupport(int id, String location, String whatsapp_number, String phone_number){
        this.id = id;
        this.location = location;
        this.phone_number = phone_number;
        this.whatsapp_number = whatsapp_number;
    }

    @Ignore
    public ContactSupport(String location, String whatsapp_number, String phone_number) {
        this.location = location;
        this.whatsapp_number = whatsapp_number;
        this.phone_number = phone_number;
    }

    public int getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getWhatsapp_number() {
        return whatsapp_number;
    }

    public String getPhone_number() {
        return phone_number;
    }
}

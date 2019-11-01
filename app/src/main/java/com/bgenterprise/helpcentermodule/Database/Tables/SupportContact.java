package com.bgenterprise.helpcentermodule.Database.Tables;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SupportContact")
public class SupportContact {

    @PrimaryKey
    private int id;

    private String location;
    private String whatsapp_number;
    private String phone_number;

    public SupportContact(int id, String location, String whatsapp_number, String phone_number){
        this.id = id;
        this.location = location;
        this.phone_number = phone_number;
        this.whatsapp_number = whatsapp_number;
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

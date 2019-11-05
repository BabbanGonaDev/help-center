package com.bgenterprise.helpcentermodule.Network.ModelClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactSupportSyncDown {

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("whatsapp_number")
    @Expose
    private String whatsapp_number;

    @SerializedName("phone_number")
    @Expose
    private String phone_number;

    public ContactSupportSyncDown() {
    }

    public ContactSupportSyncDown(String location, String whatsapp_number, String phone_number) {
        this.location = location;
        this.whatsapp_number = whatsapp_number;
        this.phone_number = phone_number;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWhatsapp_number() {
        return whatsapp_number;
    }

    public void setWhatsapp_number(String whatsapp_number) {
        this.whatsapp_number = whatsapp_number;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}

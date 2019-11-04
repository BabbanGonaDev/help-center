package com.bgenterprise.helpcentermodule.Network.ModelClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NegativeFeedbackResponse {

    @SerializedName("staff_id")
    @Expose
    private String staff_id;

    @SerializedName("app_id")
    @Expose
    private String app_id;

    @SerializedName("sync_status")
    @Expose
    private String sync_status;

    public NegativeFeedbackResponse() {
    }

    public NegativeFeedbackResponse(String staff_id, String app_id, String sync_status) {
        this.staff_id = staff_id;
        this.app_id = app_id;
        this.sync_status = sync_status;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getSync_status() {
        return sync_status;
    }

    public void setSync_status(String sync_status) {
        this.sync_status = sync_status;
    }
}

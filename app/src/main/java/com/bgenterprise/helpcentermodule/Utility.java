package com.bgenterprise.helpcentermodule;

import android.Manifest;

import retrofit2.http.Streaming;

public class Utility {

    public static final String[] app_language = new String[] {"English", "Hausa"};

    public static final String[] appPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE
    };

    public static final String resource_location = "/Helpcenter/resources/";

    public static final String resource_location_en = "/Helpcenter/resources/en/";

    public static final String resource_location_ha = "/Helpcenter/resources/ha/";
}

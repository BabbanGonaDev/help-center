package com.bgenterprise.helpcentermodule;

import android.Manifest;

public class Utility {

    public static final String[] app_language = new String[] {"English", "Hausa"};

    public static final String[] appPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE
    };

    public static final String resource_location = "/Helpcenter/resources/";
}

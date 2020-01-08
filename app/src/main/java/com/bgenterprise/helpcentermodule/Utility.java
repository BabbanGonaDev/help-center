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

    public static final String[] negative_reason_en = new String[] {"My First negative reason",
        "My second negative reason",
        "My third negative reason",
        "My fourth negative reason",
        "my fifth negative reason"};

    public static final String[] negative_reason_ha = new String[] {"First reason Hausa",
            "2nd reason Hausa",
            "3rd reason Hausa",
            "4th reason Hausa",
            "5th reason Hausa"};
}

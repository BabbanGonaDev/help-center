package com.bgenterprise.helpcentermodule;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * A session manager class strictly for the Help Center.
 * */

public class HelpSessionManager {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context mCtx;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Help Center Preferences";

    public static final String KEY_APP_ID = "app_id";
    public static final String KEY_IMPORT_CSV = "import_csv";
    public static final String KEY_ACTIVITY_ID = "activity_id";
    public static final String KEY_ACTIVITY_GROUP_ID = "activity_group_id";
    public static final String KEY_UNIQUE_QUESTION_ID = "unique_question_id";
    public static final String KEY_ACTIVITY_ISSUE = "activity_issue";
    public static final String KEY_STAFF_ID = "staff_id";
    public static final String KEY_APP_LANG = "app_language";
    public static final String KEY_USER_LOCATION = "user_location";
    public static final String KEY_LAST_SYNC_DATE = "last_sync_date";
    public static final String KEY_LAST_SYNC_QUESTIONS_ENGLISH = "last_sync_questions_english";


    public HelpSessionManager(Context context) {
        this.mCtx = context;
        prefs = mCtx.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = prefs.edit();
    }

    public void SET_IMPORT_STATUS(boolean status){
        editor.putBoolean(KEY_IMPORT_CSV, status);
        editor.commit();
    }

    public void SET_ACTIVITY_ID(String value){
        editor.putString(KEY_ACTIVITY_ID, value);
        editor.commit();
    }

    public void SET_KEY_APP_ID(String value){
        editor.putString(KEY_APP_ID, value);
        editor.commit();
    }

    public void SET_ACTIVITY_GROUP_ID(String value){
        editor.putString(KEY_ACTIVITY_GROUP_ID, value);
        editor.commit();
    }

    public void SET_UNIQUE_QUESTION_ID(String value){
        editor.putString(KEY_UNIQUE_QUESTION_ID, String.valueOf(value));
        editor.commit();
    }

    public void SET_ACTIVITY_ISSUE(String value){
        editor.putString(KEY_ACTIVITY_ISSUE, value);
        editor.commit();
    }

    public void SET_STAFF_ID(String value){
        editor.putString(KEY_STAFF_ID, value);
        editor.commit();
    }

    public void SET_LANGUAGE(String app_lang){
        editor.putString(KEY_APP_LANG, app_lang);
        editor.commit();
    }

    public void SET_USER_LOCATION(String value){
        editor.putString(KEY_USER_LOCATION, value);
        editor.commit();
    }
    public void SET_LAST_SYNC_DATE(String value){
        editor.putString(KEY_LAST_SYNC_DATE, value);
        editor.commit();
    }

    public void SET_LAST_SYNC_QUESTIONS_ENGLISH(String value){
        editor.putString(KEY_LAST_SYNC_QUESTIONS_ENGLISH, value);
        editor.commit();
    }

    public Boolean getImportStatus(){
        return prefs.getBoolean(KEY_IMPORT_CSV, false);
    }

    public String getAppLanguage(){
        return prefs.getString(KEY_APP_LANG, "en");
    }

    public HashMap<String, String> getHelpDetails(){
        HashMap<String, String> help = new HashMap<>();

        help.put(KEY_APP_ID, prefs.getString(KEY_APP_ID, ""));
        help.put(KEY_ACTIVITY_GROUP_ID, prefs.getString(KEY_ACTIVITY_GROUP_ID, ""));
        help.put(KEY_ACTIVITY_ID, prefs.getString(KEY_ACTIVITY_ID, ""));
        help.put(KEY_UNIQUE_QUESTION_ID, prefs.getString(KEY_UNIQUE_QUESTION_ID, ""));
        help.put(KEY_ACTIVITY_ISSUE, prefs.getString(KEY_ACTIVITY_ISSUE, ""));
        help.put(KEY_STAFF_ID, prefs.getString(KEY_STAFF_ID, ""));
        help.put(KEY_LAST_SYNC_DATE, prefs.getString(KEY_LAST_SYNC_DATE, ""));
        help.put(KEY_USER_LOCATION, prefs.getString(KEY_USER_LOCATION, "Senegal"));
        help.put(KEY_APP_LANG, prefs.getString(KEY_APP_LANG, "en"));
        help.put(KEY_LAST_SYNC_QUESTIONS_ENGLISH, prefs.getString(KEY_LAST_SYNC_QUESTIONS_ENGLISH, "2019-11-04 00:00:00"));

        return help;
    }

    public void CLEAR_SESSION(){
        editor.remove(KEY_UNIQUE_QUESTION_ID);
        editor.remove(KEY_ACTIVITY_ID);
        editor.remove(KEY_ACTIVITY_GROUP_ID);
        editor.remove(KEY_APP_ID);
        editor.remove(KEY_ACTIVITY_ISSUE);
        editor.commit();
    }

    public void CLEAR_UNIQUE_QUESTION(){
        editor.remove(KEY_UNIQUE_QUESTION_ID);
        editor.commit();
    }

    public void CLEAR_ACTIVITY_ID(){
        editor.remove(KEY_ACTIVITY_ID);
        editor.commit();
    }

    public void CLEAR_ACTIVITY_GROUP_ID(){
        editor.remove(KEY_ACTIVITY_GROUP_ID);
        editor.commit();
    }

    public void CLEAR_APP_ID(){
        editor.remove(KEY_APP_ID);
        editor.commit();
    }

    public void CLEAR_ACTIVITY_ISSUES(){
        editor.remove(KEY_ACTIVITY_ISSUE);
        editor.commit();
    }


}

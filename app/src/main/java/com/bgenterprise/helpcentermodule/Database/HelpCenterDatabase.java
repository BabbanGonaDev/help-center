package com.bgenterprise.helpcentermodule.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bgenterprise.helpcentermodule.Database.Dao.ContactDAO;
import com.bgenterprise.helpcentermodule.Database.Dao.EnglishDAO;
import com.bgenterprise.helpcentermodule.Database.Dao.HausaDAO;
import com.bgenterprise.helpcentermodule.Database.Dao.NegativeFeedbackDAO;
import com.bgenterprise.helpcentermodule.Database.Tables.ContactSupport;
import com.bgenterprise.helpcentermodule.Database.Tables.NegativeFeedback;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;
import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsHausa;

@Database(entities = {QuestionsEnglish.class, QuestionsHausa.class, ContactSupport.class, NegativeFeedback.class}, version = 1, exportSchema = false)
public abstract class HelpCenterDatabase extends RoomDatabase {
    private static HelpCenterDatabase INSTANCE;

    //Table entity classes.
    public abstract HausaDAO getHausaDao();
    public abstract EnglishDAO getEnglishDao();
    public abstract ContactDAO getContactDao();
    public abstract NegativeFeedbackDAO getFeedbackDao();


    //Initialization and creation of database and instance.
    public static HelpCenterDatabase getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(),
                            HelpCenterDatabase.class,
                            "helpcenter-db")
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }
}

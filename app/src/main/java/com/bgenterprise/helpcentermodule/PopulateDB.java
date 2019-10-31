package com.bgenterprise.helpcentermodule;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bgenterprise.helpcentermodule.Database.Dao.EnglishDAO;
import com.bgenterprise.helpcentermodule.Database.HelpCenterDatabase;
import com.bgenterprise.helpcentermodule.Database.Tables.IssuesEnglish;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PopulateDB extends AsyncTask<Void,Void,Void> {

    Context mCtx;
    private HelpCenterDatabase helpCenterDb;

    public PopulateDB(Context mCtx){
        this.mCtx = mCtx;
    }

    @Override
    protected Void doInBackground(Void... params) {
        helpCenterDb = HelpCenterDatabase.getInstance(mCtx);
        helpCenterDb.getEnglishDao().InsertFromCSV(readFromCSVAgain());
        return null;
    }

    private List<IssuesEnglish> readFromCSV(){
        List<IssuesEnglish> issuesEng = new ArrayList<>();
        String[] content = null;
        try{
            InputStream inputStream = mCtx.getAssets().open("help_center_template_tfm2.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            int iteration = 0;
            while((line = reader.readLine()) != null){
                content = line.split(",");

                if(iteration == 0){
                    iteration++;
                    continue;
                }else{
                    issuesEng.add(new IssuesEnglish(Integer.parseInt(content[0]),
                            content[1],
                            content[2],
                            content[3],
                            content[4],
                            content[5],
                            content[6],
                            content[7],
                            Integer.parseInt(content[8]),
                            Integer.parseInt(content[9]),
                            Integer.parseInt(content[10])));
                    Log.d("CHECK", "One Iteration");
                }

            }
        }catch(IOException e){
            e.printStackTrace();
        }

        return issuesEng;
    }

    private List<IssuesEnglish> readFromCSVAgain(){
        List<IssuesEnglish> issuesEng = new ArrayList<>();
        String[] content = null;
        try{
            InputStream inputStream = mCtx.getAssets().open("help_center_template_tfm2.csv");
            CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream));
            int iteration = 0;
            while ((content = csvReader.readNext()) != null) {
                if(iteration == 0){
                    iteration++;
                }else {
                    issuesEng.add(new IssuesEnglish(Integer.parseInt(content[0]),
                            content[1],
                            content[2],
                            content[3],
                            content[4],
                            content[5],
                            content[6],
                            content[7],
                            Integer.parseInt(content[8]),
                            Integer.parseInt(content[9]),
                            Integer.parseInt(content[10])));
                    Log.d("CHECK", "One Iteration");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return issuesEng;
    }
}

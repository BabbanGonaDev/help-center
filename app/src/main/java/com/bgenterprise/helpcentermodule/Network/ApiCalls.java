package com.bgenterprise.helpcentermodule.Network;

import com.bgenterprise.helpcentermodule.Database.Tables.QuestionsEnglish;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiCalls {

    @GET("1991sp")
    Call<List<QuestionsEnglish>> getString();

}

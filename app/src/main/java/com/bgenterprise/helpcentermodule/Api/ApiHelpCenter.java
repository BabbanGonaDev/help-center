package com.bgenterprise.helpcentermodule.Api;

import com.bgenterprise.helpcentermodule.Database.Tables.IssuesEnglish;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiHelpCenter {



        @GET("1991sp")
    Call<List<IssuesEnglish>> getString();

}

package com.bgenterprise.helpcentermodule.Network;

import com.bgenterprise.helpcentermodule.Database.Tables.IssuesEnglish;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiCalls {

    @GET("1991sp")
    Call<List<IssuesEnglish>> getString();

}

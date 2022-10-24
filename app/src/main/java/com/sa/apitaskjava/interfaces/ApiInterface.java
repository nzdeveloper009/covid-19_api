package com.sa.apitaskjava.interfaces;

import com.sa.apitaskjava.models.ModelClass;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    public static String BASE_URL = "https://disease.sh/v3/covid-19/";
//    public static String BASE_URL = "https://disease.sh/v2/";

    @GET("countries")
    Call<List<ModelClass>> getCountryData();
}

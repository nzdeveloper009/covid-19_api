package com.sa.apitaskjava.Utils;

import static com.sa.apitaskjava.interfaces.ApiInterface.BASE_URL;

import com.sa.apitaskjava.interfaces.ApiInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiController {
    public static Retrofit retrofit = null;

    public static ApiInterface getAPIInterface()
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiInterface.class);
    }

}

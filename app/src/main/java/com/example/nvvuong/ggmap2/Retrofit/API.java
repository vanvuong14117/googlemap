package com.example.nvvuong.ggmap2.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
    public static final String Base_Url = "https://maps.googleapis.com/maps/api/";

    public static ServiceAPI getData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ServiceAPI.class);
    }
}

package com.example.nvvuong.ggmap2.Retrofit;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.nvvuong.ggmap2.MapsActivity;


public class Loading extends ProgressDialog {
    public static Loading loading;

    public Loading(Context context) {
        super(context);
        setMessage("loadingggggg");
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }


    public static Loading create(Context context) {
        loading = new Loading(context);
        return loading;
    }
}
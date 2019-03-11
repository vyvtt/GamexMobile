package com.gamex.services.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.gamex.activity.LoginActivity;
import com.gamex.activity.MainActivity;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseCallBack<T> implements Callback<T> {

    private final Context context;

    public BaseCallBack(Context context) {
        this.context = context;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.code() == 401) {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setTitleText("Opps ...")
                    .setContentText("Your session has expired. Please Login again!")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sweetAlertDialog1 -> {
                        SharedPreferences sharedPreferences = context
                                        .getApplicationContext()
                                        .getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        sweetAlertDialog1.dismissWithAnimation();
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                    });
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();

        } else {
            onSuccess(call, response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {

    }

    public abstract void onSuccess(Call<T> call, Response<T> response);

}
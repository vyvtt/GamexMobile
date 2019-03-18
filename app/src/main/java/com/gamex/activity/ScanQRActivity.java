package com.gamex.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gamex.GamexApplication;
import com.gamex.models.Survey;
import com.gamex.services.network.BaseCallBack;
import com.gamex.services.network.DataService;
import com.gamex.utils.Constant;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


public class ScanQRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    @Inject
    @Named("cache")
    DataService dataService;
    Call<ResponseBody> call;
    @Inject
    SharedPreferences sharedPreferences;
    private String accessToken;
    SweetAlertDialog sweetAlertDialog;

    private int CAMERA_PERMISSION_CODE = 1;
    private ZXingScannerView zXingScannerView;
    private ArrayList<BarcodeFormat> scanFormat;
    private String TAG = ScanQRActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((GamexApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);

        accessToken = "Bearer " + sharedPreferences.getString(Constant.PREF_ACCESS_TOKEN, "");

        if (ContextCompat.checkSelfPermission(ScanQRActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // request camera permission ----> onRequestPermissionsResult()
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            //PERMISSION_GRANTED already -> do nothing about permission
            initScan();
        }
    }

    private void initScan() {
        scanFormat = new ArrayList<>();
        scanFormat.add(BarcodeFormat.QR_CODE);
        zXingScannerView = new ZXingScannerView(this);
        zXingScannerView.setFormats(scanFormat);
        setContentView(zXingScannerView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //PERMISSION_GRANTED
                initScan();
            } else {
                permissionDenied();
            }
        }
    }

    private void permissionDenied() {
        Toast.makeText(this, "Camera Permission DENIED", Toast.LENGTH_SHORT).show();
        onBackPressed();
        finish();
    }

    @Override
    public void handleResult(Result rawResult) {
        zXingScannerView.stopCamera();
//        setContentView(R.layout.activity_scan_qr);

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Connecting ...");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        String scanResult = rawResult.getText();

        Log.i(TAG, "Scan: " + scanResult);

        if (scanResult.contains("exhibitionId")) {
            // list surveys
            callApiSurveys(scanResult);
        } else {
            // exhibition
            callApiCheckIn(scanResult);
        }
    }

    private void callApiSurveys(String scanResult) {

        HashMap<String,String> apiParams = new HashMap<>();
        String[] params = scanResult.split("&");
        for (String param : params) {
            String[] temp1 = param.split("=");
            apiParams.put(temp1[0], temp1[1]);
        }

        Call<List<Survey>> call = dataService.scanGetSurveys(accessToken, apiParams);
        call.enqueue(new BaseCallBack<List<Survey>>(this) {
            @Override
            public void onSuccess(Call<List<Survey>> call, Response<List<Survey>> response) {
                Log.i(TAG, response.message());

                try {
                    if (response.isSuccessful()) {

                        List<Survey> surveys = response.body();

                        if (surveys != null) {
                            Intent intent = new Intent(ScanQRActivity.this, CompanyDetailActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putBoolean(Constant.EXTRA_COMPANY_IS_SCAN_SURVEY, true);
//                            bundle.putSerializable(Constant.EXTRA_COMPANY_SURVEY, (Serializable) surveys);
                            bundle.putString(Constant.EXTRA_SCAN_QR_RESULT, scanResult);

                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }

                    } else {

                        String strErr = response.errorBody().string();
                        JSONObject jsonErr = new JSONObject(strErr);
                        String mesErr = jsonErr.getString("message");

                        Log.e(TAG, mesErr);
                        handleFailOnRequest(mesErr, "OK");
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e.fillInStackTrace());
                    handleFailOnRequest("Something went wrong", "Please try again later");
                }
            }

            @Override
            public void onFailure(Call<List<Survey>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t.fillInStackTrace());
                handleFailOnRequest("Cannot connect to GamEx server", "Please try again later");
            }
        });
    }

    private void callApiCheckIn(String scanResult) {
        call = dataService.scanCheckInEvent(accessToken, scanResult);
        call.enqueue(new BaseCallBack<ResponseBody>(this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, response.message());

                try {

                    if (response.isSuccessful()) {

                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        int point = jsonResponse.getInt("point");

                        if (sweetAlertDialog != null) {
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            sweetAlertDialog
                                    .setTitleText("Congratulation")
                                    .setContentText("Check-in Exhibition Successfully. \n You gain " + point + " ")
                                    .setConfirmText("Continue")
                                    .setConfirmClickListener(sweetAlertDialog -> {
                                        Intent intent = new Intent(ScanQRActivity.this, ExhibitionDetailActivity.class);
                                        intent.putExtra(Constant.EXTRA_SCAN_QR_EX_ID, scanResult);
                                        startActivity(intent);
                                        finish();
                                    });
                        }

                    } else {

                        String strErr = response.errorBody().string();
                        JSONObject jsonErr = new JSONObject(strErr);
                        String mesErr = jsonErr.getString("message");

                        handleFailOnRequest(mesErr, "OK");
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e.fillInStackTrace());
                    handleFailOnRequest("Something went wrong", "Please try again later");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t.fillInStackTrace());
                handleFailOnRequest("Cannot connect to GamEx server", "Please try again later");
            }
        });
    }

    private void handleFailOnRequest(String content, String confirm) {
        if (sweetAlertDialog != null) {
            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialog.setTitleText("Oops ...")
                    .setContentText(content)
                    .setConfirmText(confirm)
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                        zXingScannerView.setResultHandler(ScanQRActivity.this);
                        zXingScannerView.startCamera();
                    });
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (zXingScannerView == null) {
            initScan();
        }

        zXingScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        zXingScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();

        zXingScannerView.stopCamera();           // Stop camera on pause

        if (sweetAlertDialog != null) {
            sweetAlertDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sweetAlertDialog != null) {
            sweetAlertDialog.dismiss();
        }
    }
}

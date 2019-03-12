package com.gamex.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.gamex.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class ScanQRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private int CAMERA_PERMISSION_CODE = 1;
    private ZXingScannerView zXingScannerView;
    ArrayList<BarcodeFormat> scanFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scanFormat = new ArrayList<>();
        scanFormat.add(BarcodeFormat.QR_CODE);
        zXingScannerView = new ZXingScannerView(this);

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
        setContentView(R.layout.activity_scan_qr);
        Toast.makeText(this, "Scanned: " + rawResult.getText(), Toast.LENGTH_LONG).show();
        // TODO: Scan result handler here
    }


    @Override
    public void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        zXingScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

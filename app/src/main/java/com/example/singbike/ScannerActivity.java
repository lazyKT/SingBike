package com.example.singbike;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.singbike.Fragments.HomeFragment;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView (R.layout.activity_scanner);
        scannerView = new ZXingScannerView(this);
        setContentView (scannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler (this);
        scannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        // get qr code result and send back to home fragment
        Log.d ("QRCODE_RESULT", result.getText());
        Bundle bundle = new Bundle();
        bundle.putString ("QR_CODE", result.getText());
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments (bundle);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        scannerView.stopCamera();
    }
}

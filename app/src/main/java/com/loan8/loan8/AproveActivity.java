package com.loan8.loan8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

public class AproveActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQ_CODE = 1;
    Button btnRetryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aprove);
        btnRetryId = findViewById(R.id.btnRetryId);
        btnRetryId.setOnClickListener(this);

        dialogShow();
    }

    private void dialogShow() {
        new MaterialDialog.Builder(this)
                .title("Privacy Policy")
                .content(getString(R.string.PrivacyPolicy))
                .positiveText("agree")
                .negativeText("disagree")
                .cancelable(false)
                .canceledOnTouchOutside(false)

                .onPositive((dialog, which) -> {
                    View customView = LayoutInflater.from(AproveActivity.this).inflate(R.layout.customdialogpermission, null);
                    PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                    popupWindow.setFocusable(false);  //prevent to popup window off when touch outside

                    popupWindow.showAtLocation(customView, Gravity.CENTER, 0, 0);

                    Button btnAllowId = customView.findViewById(R.id.btnAllowId);
                    btnAllowId.setVisibility(View.INVISIBLE);
                    CheckBox checkOneId, checkTwoId, checkThreeId;
                    checkOneId = customView.findViewById(R.id.checkOneId);
                    checkTwoId = customView.findViewById(R.id.checkTwoId);
                    checkThreeId = customView.findViewById(R.id.checkThreeId);

                    checkOneId.setOnCheckedChangeListener((compoundButton, b) -> {
                        if (checkOneId.isChecked() && checkTwoId.isChecked() && checkThreeId.isChecked()) {
                            btnAllowId.setVisibility(View.VISIBLE);
                        } else {
                            btnAllowId.setVisibility(View.VISIBLE);
                        }
                    });
                    checkTwoId.setOnCheckedChangeListener((compoundButton, b) -> {
                        if (checkOneId.isChecked() && checkTwoId.isChecked() && checkThreeId.isChecked()) {
                            btnAllowId.setVisibility(View.VISIBLE);
                        } else {
                            btnAllowId.setVisibility(View.VISIBLE);
                        }
                    });
                    checkThreeId.setOnCheckedChangeListener((compoundButton, b) -> {
                        if (checkOneId.isChecked() && checkTwoId.isChecked() && checkThreeId.isChecked()) {
                            btnAllowId.setVisibility(View.VISIBLE);
                        } else {
                            btnAllowId.setVisibility(View.VISIBLE);
                        }
                    });
                    btnAllowId.setVisibility(View.VISIBLE);

                    TextView close = customView.findViewById(R.id.close);
                    close.setOnClickListener(view -> {
                        finishAffinity();
                        System.exit(0);
                    });


                    btnAllowId.setOnClickListener(view -> {
                        //code for permission.
                        if (ActivityCompat.checkSelfPermission(AproveActivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(AproveActivity.this, new String[]{
                                    Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_CALL_LOG,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA}, REQ_CODE);
                        } else {
                            Toast.makeText(AproveActivity.this, "Permission Granted.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AproveActivity.this, SyncDataActivity.class));
                            dialog.dismiss();
                        }
                    });
                    dialog.dismiss();
                })
                .onNeutral((dialog, which) -> Toast.makeText(AproveActivity.this, "Neutral", Toast.LENGTH_SHORT).show())
                .onNegative((dialog, which) -> {
                    finishAffinity();
                    System.exit(0);
                })

                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    Toast.makeText(this, "Request permission method", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AproveActivity.this, SyncDataActivity.class));
                } else {

                }
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRetryId:
                dialogShow();
                break;
        }
    }


}
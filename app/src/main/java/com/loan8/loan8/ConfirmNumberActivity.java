package com.loan8.loan8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ConfirmNumberActivity extends AppCompatActivity implements View.OnClickListener {

    Intent intent;
    String mobileByIntent, conCodeByIntent;

    private String mVerificationId;

    //firebase auth object
    private FirebaseAuth mAuth;

    Button btnSubmitOtpId, btnStartNowId;
    RelativeLayout popUpLayoutId;
    TextView txtMobileId;
    EditText etOpt1Id, etOpt2Id, etOpt3Id, etOpt4Id, etOpt5Id, etOpt6Id;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String mo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_number);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        init();
    }

    private void init() {
        intent = getIntent();
        mobileByIntent = intent.getStringExtra("mobile");
        conCodeByIntent = intent.getStringExtra("code");

        sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mo = sharedPreferences.getString("mobile_number", "");

        btnSubmitOtpId = findViewById(R.id.btnSubmitOtpId);
        btnSubmitOtpId.setOnClickListener(this);
        txtMobileId = findViewById(R.id.txtMobileId);

        etOpt1Id = findViewById(R.id.etOpt1Id);
        etOpt2Id = findViewById(R.id.etOpt2Id);
        etOpt3Id = findViewById(R.id.etOpt3Id);
        etOpt4Id = findViewById(R.id.etOpt4Id);
        etOpt5Id = findViewById(R.id.etOpt5Id);
        etOpt6Id = findViewById(R.id.etOpt6Id);

        etOpt1Id.addTextChangedListener(new GenericTextWatcher(etOpt1Id));
        etOpt2Id.addTextChangedListener(new GenericTextWatcher(etOpt2Id));
        etOpt3Id.addTextChangedListener(new GenericTextWatcher(etOpt3Id));
        etOpt4Id.addTextChangedListener(new GenericTextWatcher(etOpt4Id));
        etOpt5Id.addTextChangedListener(new GenericTextWatcher(etOpt5Id));
        etOpt6Id.addTextChangedListener(new GenericTextWatcher(etOpt6Id));

        popUpLayoutId = findViewById(R.id.popUpLayoutId);

        txtMobileId.setText(mo);

        sendVerificationCode(mobileByIntent);   //code for send the OTP to the mobile number
    }


    private void sendVerificationCode(String mobile) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(conCodeByIntent + mobile)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
            //storing the verification id that is sent to the user
            mVerificationId = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();

            if (code != null) {
                String[] separated = code.split("(?!^)");
                etOpt1Id.setText(separated[0]);
                etOpt2Id.setText(separated[1]);
                etOpt3Id.setText(separated[2]);
                etOpt4Id.setText(separated[3]);
                etOpt5Id.setText(separated[4]);
                etOpt6Id.setText(separated[5]);
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(ConfirmNumberActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(ConfirmNumberActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            shopPopup();
                            /*Intent intent = new Intent(ConfirmNumberActivity.this, AproveActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);*/

                        } else {
                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            Toast.makeText(ConfirmNumberActivity.this, message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmitOtpId:
                //open popup
//                shopPopup();
//                apiOtpCall();
                String c = etOpt1Id.getText().toString() + etOpt2Id.getText().toString() +
                        etOpt3Id.getText().toString() + etOpt4Id.getText().toString() +
                        etOpt5Id.getText().toString() + etOpt6Id.getText().toString();
                if (!TextUtils.isEmpty(c)) {
                    verifyVerificationCode(c);
                }else{
                    Toast.makeText(this, "Please enter Otp Code", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void shopPopup() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_layout_view, null);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        btnStartNowId = popupView.findViewById(R.id.btnStartNowId);
        btnStartNowId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(ConfirmNumberActivity.this, AproveActivity.class));
                Intent intent = new Intent(ConfirmNumberActivity.this, AproveActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });
    }

    /*private  void apiOtpCall(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        //Get mobile number from sharedprefferences.
        String mNumber = sharedPreferences.getString("mobile_number", null);
        String userId = sharedPreferences.getString("user_id", null);

        String otpByEt =  etOpt1Id.getText().toString() + etOpt2Id.getText().toString() + etOpt3Id.getText().toString() + etOpt4Id.getText().toString();

        String mobileNumber = "9865212484";
        String otp = "7085";

        Call<OtpModel> call = RetrofitClient.getInstance().getMyApi().apiOtp(userId, otpByEt);

        call.enqueue(new Callback<OtpModel>() {
            @Override
            public void onResponse(Call<OtpModel> call, Response<OtpModel> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()){
                    OtpModel otpModel = response.body();
                    if (otpModel.status){
                        Toast.makeText(ConfirmNumberActivity.this, otpModel.message, Toast.LENGTH_SHORT).show();
                        shopPopup();
                    } else {
                        Toast.makeText(ConfirmNumberActivity.this, otpModel.message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OtpModel> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }*/

    class GenericTextWatcher implements TextWatcher {

        View view;

        public GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String text = charSequence.toString();

            switch (view.getId()) {
                case R.id.etOpt1Id:
                    if (text.length() == 1)
                        etOpt2Id.requestFocus();
                    break;
                case R.id.etOpt2Id:
                    if (text.length() == 1)
                        etOpt3Id.requestFocus();
                    else if (text.length() == 0)
                        etOpt1Id.requestFocus();
                    break;
                case R.id.etOpt3Id:
                    if (text.length() == 1)
                        etOpt4Id.requestFocus();
                    else if (text.length() == 0)
                        etOpt2Id.requestFocus();
                    break;
                case R.id.etOpt4Id:
                    if (text.length() == 1)
                        etOpt5Id.requestFocus();
                    else if (text.length() == 0)
                        etOpt3Id.requestFocus();
                    break;
                case R.id.etOpt5Id:
                    if (text.length() == 1)
                        etOpt6Id.requestFocus();
                    else if (text.length() == 0)
                        etOpt4Id.requestFocus();
                    break;
                case R.id.etOpt6Id:
                    if (text.length() == 0)
                        etOpt5Id.requestFocus();
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
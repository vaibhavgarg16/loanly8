package com.loan8.loan8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.loan8.loan8.interfaces.Api;
import com.loan8.loan8.models.RegisterModel;
import com.loan8.loan8.utils.ConStant;
import com.loan8.loan8.utils.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.preference.PowerPreference;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout linearLayoutDropRegisterId, linearLayoutDropRegisterTwoId;
    TextView txtRegConCodeId, txtRefOptCodeId, txtSelectLanguageId;
    EditText etMobileId, etNircId, etRefCodeId;
    Button btnRegisterInId;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String dateAfter = "05/03/2021";

    String languageToLoad;

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        languageToLoad = PowerPreference.getDefaultFile().getString("lang", "en");

        Log.d("0210log", "onClick:173 " + languageToLoad);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        setContentView(R.layout.activity_register);

        //Below code and condition for check GPS is on or off in the device
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        getFcmToken();
        init();
    }

    private void buildAlertMessageNoGps() {    //GPS check method
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void init() {
        sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        linearLayoutDropRegisterId = findViewById(R.id.linearLayoutDropRegisterId);
        linearLayoutDropRegisterId.setOnClickListener(this);
//        linearLayoutDropRegisterTwoId = findViewById(R.id.linearLayoutDropRegisterTwoId);
//        linearLayoutDropRegisterTwoId.setOnClickListener(this);
        txtRegConCodeId = findViewById(R.id.txtRegConCodeId);
//        txtRefOptCodeId = findViewById(R.id.txtRefOptCodeId);

        etMobileId = findViewById(R.id.etMobileId);
        etNircId = findViewById(R.id.etNircId);
        etRefCodeId = findViewById(R.id.etRefCodeId);

        btnRegisterInId = findViewById(R.id.btnRegisterInId);
        btnRegisterInId.setOnClickListener(this);

        txtSelectLanguageId = findViewById(R.id.txtSelectLanguageId);
        txtSelectLanguageId.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.linearLayoutDropRegisterId:
                CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        // Implement your code here
                        txtRegConCodeId.setText(dialCode);
                        picker.dismiss();
                    }
                });
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                break;

            case R.id.txtSelectLanguageId:
                //code here for language change
                View customLayoutLanguagePopUp = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.language_select_popup_layout, null);
                PopupWindow languagePopUp = new PopupWindow(customLayoutLanguagePopUp, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                languagePopUp.showAtLocation(customLayoutLanguagePopUp, Gravity.CENTER, 0, 0);

                LinearLayout l1 = customLayoutLanguagePopUp.findViewById(R.id.l1);
                final TextView tvEnglish = customLayoutLanguagePopUp.findViewById(R.id.tvEnglish);
                final TextView tvMalaysia = customLayoutLanguagePopUp.findViewById(R.id.tvMalaysia);
                final TextView tvChina = customLayoutLanguagePopUp.findViewById(R.id.tvChina);
                final TextView tvOther = customLayoutLanguagePopUp.findViewById(R.id.tvOther);

                l1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvEnglish.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_tick_12, 0);
                        tvMalaysia.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvChina.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvOther.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        languageToLoad = "en";
                        ConStant.setLanguageToLoad(languageToLoad);
                        PowerPreference.getDefaultFile().setString("lang", languageToLoad);

                        // Reload current activity
                        finish();
                        startActivity(getIntent());

                        languagePopUp.dismiss();
                    }
                });
                LinearLayout l2 = customLayoutLanguagePopUp.findViewById(R.id.l2);
                l2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvEnglish.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvMalaysia.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_tick_12, 0);
                        tvChina.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvOther.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        languageToLoad = "km";
                        ConStant.setLanguageToLoad(languageToLoad);
                        PowerPreference.getDefaultFile().setString("lang", languageToLoad);

                        // Reload current activity
                        finish();
                        startActivity(getIntent());

                        languagePopUp.dismiss();
                    }
                });
                LinearLayout l3 = customLayoutLanguagePopUp.findViewById(R.id.l3);
                l3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvEnglish.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvMalaysia.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvChina.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_tick_12, 0);
                        tvOther.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        languageToLoad = "my";
                        ConStant.setLanguageToLoad(languageToLoad);
                        PowerPreference.getDefaultFile().setString("lang", languageToLoad);

                        // Reload current Activity
                        languagePopUp.dismiss();
                        finish();
                        startActivity(getIntent());

                    }
                });
                LinearLayout l4 = customLayoutLanguagePopUp.findViewById(R.id.l4);

                l4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvEnglish.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvMalaysia.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvChina.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        tvOther.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icn_tick_12, 0);
                    }
                });
                break;

            case R.id.btnRegisterInId:
                validation();
                break;
        }
    }

    public void apiRegisterCall(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        String conCode = txtRegConCodeId.getText().toString(); //country code of mobile concate with mobileNumber variable.

        String mobileNumber = conCode + etMobileId.getText().toString().trim();
        String nricNumber = /*refCode +*/ etNircId.getText().toString().trim();
        String refNumber = etRefCodeId.getText().toString().trim();


        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<RegisterModel> call = api.apiRegister(mobileNumber, nricNumber, refNumber, token);

        call.enqueue(new Callback<RegisterModel>() {
            @Override
            public void onResponse(Call<RegisterModel> call, Response<RegisterModel> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()){
                    RegisterModel registerModel = response.body();
                    if (registerModel.status){
                        Toast.makeText(RegisterActivity.this, registerModel.message ,Toast.LENGTH_SHORT).show();
                        editor.putString("user_id", String.valueOf(registerModel.userId));
                        editor.putString("mobile_number", mobileNumber);
                        editor.commit();


                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date strDate = null;
                        try {
                            strDate = sdf.parse(dateAfter);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (new Date().after(strDate)) {
//                            startActivity(new Intent(RegisterActivity.this, ConfirmNumberActivity.class));
                            if (registerModel.message.equals("User already existed.")){
                                return;
                            } else {
//                                startActivity(new Intent(RegisterActivity.this, AproveActivity.class));
//                                startActivity(new Intent(RegisterActivity.this, ConfirmNumberActivity.class));

                                //Code to jump to confirmnumberactivity with mobile and con code
                                Intent intentToConf = new Intent(RegisterActivity.this, AproveActivity.class);
                                intentToConf.putExtra("mobile", etMobileId.getText().toString().trim());
                                intentToConf.putExtra("code", conCode);
                                startActivity(intentToConf);
                            }
                        } else {
                            startActivity(new Intent(RegisterActivity.this, BottomHolderActivity/*AproveActivity*/.class));
                            //Code to jump to confirmnumberactivity with mobile and con code
                            Intent intentToConf = new Intent(RegisterActivity.this, ConfirmNumberActivity.class);
                            intentToConf.putExtra("mobile", etMobileId.getText().toString().trim());
                            intentToConf.putExtra("code", conCode);
                            startActivity(intentToConf);
                        }

                        /*if (registerModel.message.equals("User already existed.")){
                            return;
                        } else {
                            startActivity(new Intent(RegisterActivity.this, ConfirmNumberActivity.class));
                        }*/

                    } else {
                        Toast.makeText(RegisterActivity.this, registerModel.message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getFcmToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    private static final String TAG = "ppp";

                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();

                        /*// Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();*/
                    }
                });
    }

    public void validation(){
        if (etMobileId.getText().toString().trim().equalsIgnoreCase("")){
            etMobileId.requestFocus();
            etMobileId.setError("Please enter Mobile");
        } else if (etNircId.getText().toString().trim().equalsIgnoreCase("")){
            etNircId.requestFocus();
            etNircId.setError("Please enter NRIC number");
        } else {
            //call the apiRegister() method.
            apiRegisterCall();
        }
    }
}
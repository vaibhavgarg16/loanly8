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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.loan8.loan8.interfaces.Api;
import com.loan8.loan8.models.LoginModel;
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

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogInId;
    TextView txtRegisterId, txtConCodeId, etMobileId, etNircId, txtSelectLanguageId;
    LinearLayout linearLayoutDropLoginId;
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

        setContentView(R.layout.activity_log_in);

        //Below code and condition for check GPS is on or off in the device
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        getFcmToken();
        init();
    }

    private void init() {
        btnLogInId = findViewById(R.id.btnLogInId);
        btnLogInId.setOnClickListener(this);
        txtRegisterId = findViewById(R.id.txtRegisterId);
        txtRegisterId.setOnClickListener(this);
        linearLayoutDropLoginId = findViewById(R.id.linearLayoutDropLoginId);
        linearLayoutDropLoginId.setOnClickListener(this);
        txtSelectLanguageId = findViewById(R.id.txtSelectLanguageId);
        txtSelectLanguageId.setOnClickListener(this);
        txtConCodeId = findViewById(R.id.txtConCodeId);

        etMobileId = findViewById(R.id.etMobileId);
        etNircId = findViewById(R.id.etNircId);

        sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtRegisterId:
                startActivity(new Intent(LogInActivity.this, RegisterActivity.class));
                break;
            case R.id.linearLayoutDropLoginId:
                CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        // Implement your code here
                        txtConCodeId.setText(dialCode);
                        picker.dismiss();
                    }
                });
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                break;
            case R.id.btnLogInId:
                //startActivity(new Intent(LogInActivity.this, ConfirmNumberActivity.class));
                validation();
                break;

            case R.id.txtSelectLanguageId:
                View customLayoutLanguagePopUp = LayoutInflater.from(LogInActivity.this).inflate(R.layout.language_select_popup_layout, null);
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
        }
    }

    private void validation() {
        if (etMobileId.getText().toString().trim().equalsIgnoreCase("")) {
            etMobileId.requestFocus();
            etMobileId.setError("Please enter Mobile");
        } else if (etNircId.getText().toString().trim().equalsIgnoreCase("")) {
            etNircId.requestFocus();
            etNircId.setError("Please enter NRIC number");
        } else {
            //call the apilogincall() method.
            apiloginCall();
        }
    }

    public void apiloginCall() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        String conCode = txtConCodeId.getText().toString(); //country code of mobile concate with mobileNumber variable.

        String mobileNumber = conCode + etMobileId.getText().toString().trim();
        String nricNumber = etNircId.getText().toString().trim();
        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<LoginModel> call = api.apiLogin(mobileNumber, nricNumber, token);

        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                progressDialog.dismiss();
//                Log.d("LoginResponse ", response.body().mobile_number + response.body().message + response.body().status);
//                if (response.isSuccessful()){
                if (response.body() != null) {
                    LoginModel loginModel = response.body();
                    if (loginModel.getStatus()) {
                        //Commenting below code for redirect to the SyncDataActivity.class only for test or client purpose
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date strDate = null;
                        try {
                            strDate = sdf.parse(dateAfter);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (new Date().after(strDate)) {
                            //Code to jump to confirmnumberactivity with mobile and con code
                            Intent intentToConf = new Intent(LogInActivity.this, AproveActivity.class);
                            intentToConf.putExtra("mobile", etMobileId.getText().toString().trim());
                            intentToConf.putExtra("code", conCode);
                            startActivity(intentToConf);
                        } else {
                            startActivity(new Intent(LogInActivity.this, BottomHolderActivity.class));
                        }

                        Toast.makeText(LogInActivity.this, loginModel.message, Toast.LENGTH_SHORT).show();
                        editor.putString("user_id", String.valueOf(loginModel.userId));
                        editor.putString("mobile_number", mobileNumber);
                        editor.putString("mobile", etMobileId.getText().toString().trim());
                        editor.putString("conCode", conCode);
                        editor.commit();
                    } else {
                        Toast.makeText(LogInActivity.this, loginModel.message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LogInActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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

                        // Log and toast

                        Log.d("TAG", token);
//                        Toast.makeText(LogInActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
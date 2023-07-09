package com.loan8.loan8;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loan8.loan8.fragments.CompleteProfileActivity;
import com.loan8.loan8.models.FacebookDataModel;
import com.loan8.loan8.utils.RetrofitClient;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FacebookDesignActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    EditText etMobileNumberId, etpasswordId;
    Button btnFbLoginId, btnFbRegisterId;

    //Creating object of sharedpreferences.
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String userId;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_design);

        //For initilize widgets
        init();
    }

    private void init() {
        sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        etMobileNumberId = findViewById(R.id.etMobileNumberId);
        etpasswordId = findViewById(R.id.etpasswordId);
        btnFbLoginId = findViewById(R.id.btnFbLoginId);
        btnFbLoginId.setOnClickListener(this);
        btnFbRegisterId = findViewById(R.id.btnFbRegisterId);
        btnFbRegisterId.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFbLoginId:
                validation();
                break;

            case R.id.btnFbRegisterId:
                Toast.makeText(this, "Please Register on Facebook", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //Method for login function
    /*private void loginTask(){
        editor.putString("fbUserName", etMobileNumberId.getText().toString());
        editor.putString("fbPassword", etpasswordId.getText().toString());
        editor.commit();
        Toast.makeText(this, "Data Saved.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(FacebookDesignActivity.this, CompleteProfileActivity.class));
        finish();
    }*/

    //Validation for edittext.
    private void validation() {
        if (etMobileNumberId.getText().toString().equalsIgnoreCase("")) {
            etMobileNumberId.setError("Please Enter User Name");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(etMobileNumberId.getText()).matches()) {
            etMobileNumberId.setError("Please Enter Valid Email");
        } else if (etpasswordId.getText().toString().equalsIgnoreCase("")) {
            etpasswordId.setError("Please Enter Password");
        } else if (etpasswordId.getText().toString().length() < 6) {
            etpasswordId.setError("Please Enter Correct Password");
        }
            /*else if (!PASSWORD_PATTERN.matcher(etpasswordId.getText().toString()).matches()) {
            etpasswordId.setError("Please Enter Valid Password");
        }*/
        else {
            //call api here
            apiSaveFacebookData();
        }
    }


    private void apiSaveFacebookData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        userId = sharedPreferences.getString("user_id", "");

        String user = etMobileNumberId.getText().toString().trim();
        String password = etpasswordId.getText().toString().trim();


        Call<FacebookDataModel> call = RetrofitClient.getInstance().getMyApi().saveFbData(userId,
                user, password);

        call.enqueue(new Callback<FacebookDataModel>() {
            @Override
            public void onResponse(Call<FacebookDataModel> call, Response<FacebookDataModel> response) {
                progressDialog.dismiss();
                FacebookDataModel facebookDataModel = response.body();
                if (response.body().status) {
                    Toast.makeText(FacebookDesignActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(FacebookDesignActivity.this, CompleteProfileActivity.class));
                } else {
                    Toast.makeText(FacebookDesignActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FacebookDataModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(FacebookDesignActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
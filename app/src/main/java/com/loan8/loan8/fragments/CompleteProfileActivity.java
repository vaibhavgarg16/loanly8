package com.loan8.loan8.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.anilokcun.uwmediapicker.UwMediaPicker;

import com.loan8.loan8.BottomHolderActivity;


import com.loan8.loan8.R;
import com.loan8.loan8.interfaces.Api;
import com.loan8.loan8.models.ImageUploadModel;
import com.loan8.loan8.models.UpdateUserModel;
import com.loan8.loan8.models.UserProfileShow;

import com.loan8.loan8.utils.RetrofitClient;
import com.bumptech.glide.Glide;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CompleteProfileActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnProfileSubmitId;
    EditText etNameId, etNricNumberId, etEmergancyNameId, etEmergancyRelationId, etEmergancyPhoneId;
    ImageView imgUploadId, videoUploadId, imgUploadWithBackId, imgUploadVideoVoiceIntoId;
    TextView txtViewMoreId, txtViewTutorialId;
    CallbackManager callbackManager;
    AccessToken accessToken;
    boolean isLoggedIn;
    String videopath, imagepath, imageBackPath, videoVoiceIntroPath;
    Button btnFacebookId;
    String id = "front", VideoId = "video1";
    //Creating object of sharedpreferences.
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    String userId;
    private FirebaseAuth mAuth;

    String dateAfter = "05/03/2021";
    static final int REQ_CODE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 111;
    static final int REQUEST_CAMERA_CAPTURE = 112;
    static final int REQUEST_CAMERA_CAPTURE2 = 113;

    long mCick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_complete_profile, container, false);*/

        FacebookSdk.sdkInitialize(getApplicationContext()); //initilize facebook sdk
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userId = sharedPreferences.getString("user_id", "");
        callbackManager = CallbackManager.Factory.create();
        btnFacebookId = findViewById(R.id.btnFacebookId);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date strDate = null;
        try {
            strDate = sdf.parse(dateAfter);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (new Date().after(strDate)) {
            btnFacebookId.setVisibility(View.GONE);
        } else {
            btnFacebookId.setVisibility(View.GONE);
        }

        btnFacebookId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        requestPermissionTest();


        accessToken = AccessToken.getCurrentAccessToken();
        //code for check facebook is logged in or not
        isLoggedIn = accessToken != null && !accessToken.isExpired();
        btnProfileSubmitId = findViewById(R.id.btnProfileSubmitId);
        btnProfileSubmitId.setOnClickListener(this);

        etNameId = findViewById(R.id.etNameId);
        etNricNumberId = findViewById(R.id.etNricNumberId);
        etEmergancyNameId = findViewById(R.id.etEmergancyNameId);
        etEmergancyRelationId = findViewById(R.id.etEmergancyRelationId);
        etEmergancyPhoneId = findViewById(R.id.etEmergancyPhoneId);

        imgUploadId = findViewById(R.id.imgUploadId);
        imgUploadId.setOnClickListener(this);

        videoUploadId = findViewById(R.id.videoUploadId);
        videoUploadId.setOnClickListener(this);

        imgUploadWithBackId = findViewById(R.id.imgUploadWithBackId);
        imgUploadWithBackId.setOnClickListener(this);

        imgUploadVideoVoiceIntoId = findViewById(R.id.imgUploadVideoVoiceIntoId);
        imgUploadVideoVoiceIntoId.setOnClickListener(this);

        txtViewMoreId = findViewById(R.id.txtViewMoreId);
        txtViewMoreId.setOnClickListener(this);
        txtViewTutorialId = findViewById(R.id.txtViewTutorialId);
        txtViewTutorialId.setOnClickListener(this);

        apiUserProfileShow();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnProfileSubmitId:
                if (validation()) {
                    String user_name = etNameId.getText().toString();
                    String nric_no = etNricNumberId.getText().toString();
                    String etName_no = etEmergancyNameId.getText().toString();
                    String relation_no = etEmergancyRelationId.getText().toString();
                    String phone_id = etEmergancyPhoneId.getText().toString();
                    apiUserProfileUpdate(user_name, nric_no, etName_no, relation_no, phone_id);
                }
                break;

            case R.id.imgUploadId:
//                selectImageFromGallery();
                if (SystemClock.elapsedRealtime() - mCick < 5000) {
                    return;
                }
                mCick = SystemClock.elapsedRealtime();
                id = "front";

                ShowDialog(121, REQUEST_CAMERA_CAPTURE);

                //Condition for get selfie video and voice Introduction
            case R.id.imgUploadVideoVoiceIntoId:
                if (SystemClock.elapsedRealtime() - mCick < 5000) {
                    return;
                }
                mCick = SystemClock.elapsedRealtime();
                VideoId = "video1";

                AlertDialog.Builder builder2 = new AlertDialog.Builder(CompleteProfileActivity.this);
                LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View customLayout2 = inflater2.inflate(R.layout.video_pick_dialog, null);
                builder2.setTitle("Choose Video");
                builder2.setView(customLayout2);
                builder2.setCancelable(false);
                ImageView cameraPick2 = customLayout2.findViewById(R.id.videoPick);
                ImageView galleryPick2 = customLayout2.findViewById(R.id.galleryPick);

                galleryPick2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UwMediaPicker
                                .Companion.with(CompleteProfileActivity.this)                        // Activity or Fragment
                                .setGalleryMode(UwMediaPicker.GalleryMode.VideoGallery) // GalleryMode: ImageGallery/VideoGallery/ImageAndVideoGallery, default is ImageGallery
                                .setGridColumnCount(4)
                                .setRequestCode(122)// Grid column count, default is 3
                                .setMaxSelectableMediaCount(1)                         // Maximum selectable media count, default is null which means infinite
                                .setLightStatusBar(true)                                // Is llight status bar enable, default is true
                                .enableImageCompression(true)                // Is image compression enable, default is false
                                .setCompressionMaxWidth(1280F)                // Compressed image's max width px, default is 1280
                                .setCompressionMaxHeight(720F)                // Compressed image's max height px, default is 720
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)        // Compressed image's format, default is JPEG
                                .setCompressionQuality(85)                // Image compression quality, default is 85
                                .open();    // Compressed image file's destination path, default is "${application.getExternalFilesDir(null).path}/Pictures"
                    }
                });
                cameraPick2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dispatchTakeVideoIntent();
                    }
                });

                builder2.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder2.show();
                break;

            //conditon for get selfie with back id
            case R.id.imgUploadWithBackId:
                if (SystemClock.elapsedRealtime() - mCick < 5000) {
                    return;
                }
                mCick = SystemClock.elapsedRealtime();
                id = "back";

                ShowDialog(123, REQUEST_CAMERA_CAPTURE2);

                break;

            case R.id.videoUploadId:
//                selectVideoFromGallery();
                VideoId = "video2";
//                getVideoPath();
                break;

            //case for photo id tutorial listener
            case R.id.txtViewMoreId:
                View sampleCustomLayout = LayoutInflater.from(this).inflate(R.layout.view_sample_layout, null);
                PopupWindow popupWindow = new PopupWindow(sampleCustomLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAtLocation(sampleCustomLayout, Gravity.CENTER, 0, 0);
                Button btnPhotoOk = sampleCustomLayout.findViewById(R.id.btnPhotoOk);
                btnPhotoOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                    }
                });
                break;
            //case for video tutorial listener
            case R.id.txtViewTutorialId:
                View sampleVideoLayout = LayoutInflater.from(this).inflate(R.layout.video_tutr_layout, null);
                PopupWindow videoPopup = new PopupWindow(sampleVideoLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                videoPopup.showAtLocation(sampleVideoLayout, Gravity.CENTER, 0, 0);
                Button btnVideoOk = sampleVideoLayout.findViewById(R.id.btnVideoOk);
                btnVideoOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        videoPopup.dismiss();
                    }
                });
                break;
        }
    }

    public void ShowDialog(int Request, int camera) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CompleteProfileActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View customLayout = inflater.inflate(R.layout.file_pick_dialog, null);
        builder.setTitle("Choose Image");
        builder.setView(customLayout);
        builder.setCancelable(false);
        ImageView cameraPick = customLayout.findViewById(R.id.cameraPick);
        ImageView galleryPick = customLayout.findViewById(R.id.galleryPick);

        galleryPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UwMediaPicker
                        .Companion.with(CompleteProfileActivity.this)                        // Activity or Fragment
                        .setGalleryMode(UwMediaPicker.GalleryMode.ImageGallery) // GalleryMode: ImageGallery/VideoGallery/ImageAndVideoGallery, default is ImageGallery
                        .setGridColumnCount(4)
                        .setRequestCode(Request)// Grid column count, default is 3
                        .setMaxSelectableMediaCount(1)                         // Maximum selectable media count, default is null which means infinite
                        .setLightStatusBar(true)                                // Is llight status bar enable, default is true
                        .enableImageCompression(true)                // Is image compression enable, default is false
                        .setCompressionMaxWidth(1280F)                // Compressed image's max width px, default is 1280
                        .setCompressionMaxHeight(720F)                // Compressed image's max height px, default is 720
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)        // Compressed image's format, default is JPEG
                        .setCompressionQuality(85)                // Image compression quality, default is 85
                        .open();    // Compressed image file's destination path, default is "${application.getExternalFilesDir(null).path}/Pictures"
            }
        });
        cameraPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakeCameraIntent(camera);
            }
        });

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //new library condition
        if (requestCode == 121 && resultCode == RESULT_OK) {
            assert data != null;
            String[] files = data.getStringArrayExtra(UwMediaPicker.UwMediaPickerImagesArrayKey);
            ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(files));
            if (stringList.get(0).equals("")) {
                Toast.makeText(this, "Not select any thing.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show();
//                menuPath = files.get(0).getPath();
                imgUploadId.getLayoutParams().height = 300;
                imgUploadId.getLayoutParams().width = 300;
                Glide.with(this)
                        .load(stringList.get(0))
                        .into(imgUploadId);
                apiUploadImage(stringList.get(0), "front");
            }
        } else if (requestCode == REQUEST_CAMERA_CAPTURE && resultCode == RESULT_OK) {
            if (data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri tempUri = getImageUri(getApplicationContext(), photo);
                Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show();
                imgUploadId.getLayoutParams().height = 300;
                imgUploadId.getLayoutParams().width = 300;
                Glide.with(this)
                        .load(tempUri)
                        .into(imgUploadId);
                apiUploadImage(getRealPathFromURI(tempUri), "front");
            }
        } else if (requestCode == 122 && resultCode == RESULT_OK) {
            assert data != null;
            String[] files = data.getStringArrayExtra(UwMediaPicker.UwMediaPickerVideosArrayKey);
            ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(files));
            if (stringList.get(0).equals("")) {
                Toast.makeText(this, "Not select any thing.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Video uploaded", Toast.LENGTH_SHORT).show();
//                menuPath = files.get(0).getPath();
                imgUploadVideoVoiceIntoId.getLayoutParams().height = 300;
                imgUploadVideoVoiceIntoId.getLayoutParams().width = 300;
                Glide.with(this)
                        .load(stringList.get(0))
                        .into(imgUploadVideoVoiceIntoId);
                apiUploadImage(stringList.get(0), "video1");
            }
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri file = data.getData();
            if (TextUtils.isEmpty(file.getPath())) {
                Toast.makeText(this, "Not select any thing.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Video uploaded", Toast.LENGTH_SHORT).show();
                imgUploadVideoVoiceIntoId.getLayoutParams().height = 300;
                imgUploadVideoVoiceIntoId.getLayoutParams().width = 300;
                Glide.with(this)
                        .load(getRealPathFromURI(file))
                        .into(imgUploadVideoVoiceIntoId);
                apiUploadImage(getRealPathFromURI(file), "video1");
            }
        } else if (requestCode == 123 && resultCode == RESULT_OK) {
            assert data != null;
            String[] files = data.getStringArrayExtra(UwMediaPicker.UwMediaPickerImagesArrayKey);
            ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(files));
            if (stringList.get(0).equals("")) {
                Toast.makeText(this, "Not select any thing.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show();
//                menuPath = files.get(0).getPath();
                imgUploadWithBackId.getLayoutParams().height = 300;
                imgUploadWithBackId.getLayoutParams().width = 300;
                Glide.with(this)
                        .load(stringList.get(0))
                        .into(imgUploadWithBackId);
                apiUploadImage(stringList.get(0), "back");
            }
        } else if (requestCode == REQUEST_CAMERA_CAPTURE2 && resultCode == RESULT_OK) {
            if (data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri tempUri = getImageUri(getApplicationContext(), photo);
                Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show();
                imgUploadWithBackId.getLayoutParams().height = 300;
                imgUploadWithBackId.getLayoutParams().width = 300;
                Glide.with(this)
                        .load(tempUri)
                        .into(imgUploadWithBackId);
                apiUploadImage(getRealPathFromURI(tempUri), "back");
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void dispatchTakeCameraIntent(int request) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, request);
    }

    public boolean validation() {
        if (TextUtils.isEmpty(etNameId.getText())) {
            etNameId.setError("Please enter name");
            etNameId.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(etNricNumberId.getText())) {
            etNricNumberId.setError("Please enter name");
            etNricNumberId.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(etEmergancyPhoneId.getText())) {
            etEmergancyPhoneId.setError("Please enter emergancy phone");
            etEmergancyPhoneId.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    //call api for update profile user data
    private void apiUserProfileUpdate(String user_name, String nric_no, String etName_no, String relation_no, String phone_id) {
        progressDialog.show();

        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<UpdateUserModel> call = api.apiUpdateUser(userId, user_name, nric_no, etName_no,
                relation_no, phone_id, imagepath, videoVoiceIntroPath, imageBackPath, videopath);

        call.enqueue(new Callback<UpdateUserModel>() {
            @Override
            public void onResponse(Call<UpdateUserModel> call, Response<UpdateUserModel> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    UpdateUserModel updateUserModel = response.body();
                    if (updateUserModel.getStatus()) {
                        Toast.makeText(CompleteProfileActivity.this, updateUserModel.message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CompleteProfileActivity.this, BottomHolderActivity.class));
                    } else {
                        Toast.makeText(CompleteProfileActivity.this, updateUserModel.message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UpdateUserModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CompleteProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // call api for display profile user data.
    private void apiUserProfileShow() {
        ProgressDialog progressDialog = new ProgressDialog(CompleteProfileActivity.this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        String userId = sharedPreferences.getString("user_id", "");

        Call<UserProfileShow> call = RetrofitClient.getInstance().getMyApi().apiUserProShow(userId);
        call.enqueue(new Callback<UserProfileShow>() {
            @Override
            public void onResponse(Call<UserProfileShow> call, Response<UserProfileShow> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    UserProfileShow userProfileShow = response.body();
                    if (userProfileShow.status) {
                        etNameId.setText(userProfileShow.userName);
                        etNricNumberId.setText(userProfileShow.nricNumber);
                        etEmergancyNameId.setText(userProfileShow.emergencyName);
                        etEmergancyRelationId.setText(userProfileShow.emergencyRelationship);
                        etEmergancyPhoneId.setText(userProfileShow.emergencyContactNumber);

                    } else {
                        Toast.makeText(CompleteProfileActivity.this, userProfileShow.message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileShow> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CompleteProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Call upload image api.
    private void apiUploadImage(String path, String type) {
        progressDialog.show();
        File file = new File(path);
        file.getName();
        Log.d("FILE ", file.getName());

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Call<ImageUploadModel> call = RetrofitClient.getInstance().getMyApi().uploadImage(body, type);
        call.enqueue(new Callback<ImageUploadModel>() {
            @Override
            public void onResponse(Call<ImageUploadModel> call, Response<ImageUploadModel> response) {
                Log.d("Mess ", response.body().message + "My Name");
                if (response.body().getStatus()) {
                    progressDialog.dismiss();
                    Toast.makeText(CompleteProfileActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                    String imagePath = response.body().getFile_path();
                    if (imagePath.contains(".jpg") || imagePath.contains(".jpeg") || imagePath.contains(".png")) {
                        if (response.body().getFile_type().equals("front")) {
                            imagepath = response.body().getFile_path();
                        } else if (response.body().getFile_type().equals("back")) {
                            imageBackPath = response.body().getFile_path();
                        }
                    } else {
                        if (response.body().getFile_type().equals("video1")) {
                            videoVoiceIntroPath = response.body().file_path;
                        } else if (response.body().getFile_type().equals("video2")) {
                            videopath = response.body().file_path;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ImageUploadModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CompleteProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestPermissionTest() {
        //code for permission.
        if (ActivityCompat.checkSelfPermission(CompleteProfileActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CompleteProfileActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, REQ_CODE);
        } else {
            Toast.makeText(CompleteProfileActivity.this, "Permission Granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQ_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    Toast.makeText(this, "Request permission method", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(RegisterActivity.this, SecondActivity.class));
                } else {
                    Toast.makeText(this, "Please accept permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
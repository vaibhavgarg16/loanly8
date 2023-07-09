package com.loan8.loan8.interfaces;

import androidx.annotation.NonNull;

import com.loan8.loan8.models.BackgroundLocationModel;
import com.loan8.loan8.models.FacebookDataModel;
import com.loan8.loan8.models.ImageUploadModel;
import com.loan8.loan8.models.LoginModel;
import com.loan8.loan8.models.OtpModel;
import com.loan8.loan8.models.PhotosUploadModel;
import com.loan8.loan8.models.RegisterModel;
import com.loan8.loan8.models.UpdateUserModel;
import com.loan8.loan8.models.UserProfileShow;
import com.loan8.loan8.models.VideoUploadModel;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Api {

    //http://readyforyourreview.com/loanly_android/api_data/?task=user_register
    //http://readyforyourreview.com/loanly_android/api_data/?task=user_register&mobile_number= &nric_number= &referral_number=
    @POST("api_data/?task=user_register")
    Call<RegisterModel> apiRegister(@Query("mobile_number") String mobile_number,
                                    @Query("nric_number") String nric_number,
                                    @Query("referral_number") String referral_number,
                                    @Query("fcm_token") String fcm_token);

    //http://readyforyourreview.com/loanly_android/api_data/?task=user_login
    //http://readyforyourreview.com/loanly_android/api_data/?task=user_login&mobile_number=9865212484&nric_number=23456
    @POST("api_data/?task=user_login")
    Call<LoginModel> apiLogin(@Query("mobile_number") String mobile_number,
                              @Query("nric_number") String nric_number,
                              @Query("fcm_token") String fcm_token);


    //http://readyforyourreview.com/loanly_android/api_data/?task=user_login_verify_otp&mobile_number=9865212484&otp=4545
    @POST("api_data/?task=user_login_verify_otp")
    Call<OtpModel> apiOtp(@Query("mobile_number") String mobile_number,
                          @Query("otp") String otp);

    //update profile
    //http://readyforyourreview.com/loanly_android/api_data/?task=user_update&user_id=40&nric_number=543768&referral_number=8934125044&user_name=ravi&emergency_name=mujeeb&emergency_relationship=sister&emergency_phone_number=9785342100
    //https://readyforyourreview.com/loanly_android/api_data/?task=user_update&user_id=37&user_name=ruchika&nric_number=12345&emergency_name=name2020&emergency_relationship=bro&emergency_phone_number=2020&id_photo=url&selfie_video=url
    @POST("api_data/?task=user_update")
    Call<UpdateUserModel> apiUpdateUser(@Query("user_id") String user_id,
            /*@Query("referral_number") String referral_number,*/
                                        @Query("user_name") String user_name,
                                        @Query("nric_number") String nric_number,
                                        @Query("emergency_name") String emergency_name,
                                        @Query("emergency_relationship") String emergency_relationship,
                                        @Query("emergency_phone_number") String emergency_phone_number,
                                        @Query("id_photo") String id_photo,
                                        @Query("selfie_video") String selfie_video,
                                        @Query("id_back_selfie") String id_back_selfie,
                                        @Query("video_voice_intro") String video_voice_intro);

    //show profile
    // http://readyforyourreview.com/loanly_android/api_data/?task=user_profile_show&id=37
    @POST("api_data/?task=user_profile_show")
    Call<UserProfileShow> apiUserProShow(@Query("id") String id);

    //https://readyforyourreview.com/loanly_android/api_data/?task=user_upload_file
    @NonNull
    @Multipart
    @POST("api_data/?task=user_upload_file")
    Call<ImageUploadModel> uploadImage(@Part MultipartBody.Part image,
                                       @Query("file_type") String file_type);


    @NonNull
    @Multipart
    @POST("api_data/?task=user_upload_file_video")
    Call<VideoUploadModel> uploadVideo(@Part MultipartBody.Part video,
                                       @Query("user_id") String user_id);


    //https://readyforyourreview.com/api_data/?task=user_upload_file_location_images_contact
    @Multipart
    @POST("api_data/?task=user_upload_file_location_images_contact")
//    Call<PhotosUploadModel> photosVideos(@Part MultipartBody.Part photosVideos);
    Call<PhotosUploadModel> photosVideos(@Part ArrayList<MultipartBody.Part> photosVideos,
                                         @Part("contact[]") ArrayList<String> contact,
                                         @Query("user_id") String user_id,
                                         @Part("logs[]") ArrayList<String> logs,
                                         @Query("lat_long") String lat_long);
    /*@Query("contact") ArrayList<String> contact,*/
//                                         @Part ArrayList<MultipartBody.Part> contact,

    //https://readyforyourreview.com/api_data/?task=save_fb_login_details&user_id=36&fb_username=23456&fb_password=134564354
    @POST("api_data/?task=save_fb_login_details")
    Call<FacebookDataModel> saveFbData(@Query("user_id") String user_id,
                                       @Query("fb_username") String fb_username,
                                       @Query("fb_password") String fb_password);

    //http://readyforyourreview.com/api_data/?task=user_lat_long_update&user_id=658&lat_long=464
    @POST("api_data/?task=user_lat_long_update")
    Call<BackgroundLocationModel> apiBackgroundLocationUpdate(@Query("user_id") String user_id,
                                                              @Query("lat_long") String lat_long);

    //http://readyforyourreview.com/api_data/?task=user_cont_images_update&user_id=686


}

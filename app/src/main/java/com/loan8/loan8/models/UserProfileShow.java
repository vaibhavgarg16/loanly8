package com.loan8.loan8.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileShow {

    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("error_code")
    @Expose
    public String errorCode;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("nric_number")
    @Expose
    public String nricNumber;
    @SerializedName("emergency_name")
    @Expose
    public String emergencyName;
    @SerializedName("emergency_relationship")
    @Expose
    public String emergencyRelationship;
    @SerializedName("emergency_contact_number")
    @Expose
    public String emergencyContactNumber;
    @SerializedName("id_photo")
    @Expose
    public String idPhoto;
    @SerializedName("selfie_video")
    @Expose
    public String selfieVideo;

}

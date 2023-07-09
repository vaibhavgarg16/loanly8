package com.loan8.loan8.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtpModel {

    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("error_code")
    @Expose
    public String errorCode;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("mobile_number")
    @Expose
    public String mobileNumber;
}

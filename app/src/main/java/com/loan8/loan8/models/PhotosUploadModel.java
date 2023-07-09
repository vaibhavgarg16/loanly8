package com.loan8.loan8.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotosUploadModel {

    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("error_code")
    @Expose
    public String errorCode;
    @SerializedName("message")
    @Expose
    public String message;

}

package com.loan8.loan8.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateUserModel {

    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("error_code")
    @Expose
    public String errorCode;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("user-id")
    @Expose
    public String userId;

    public Boolean getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }
}

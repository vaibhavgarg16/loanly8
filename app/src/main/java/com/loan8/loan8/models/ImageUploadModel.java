package com.loan8.loan8.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageUploadModel {

    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("error_code")
    @Expose
    public String errorCode;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("file_path")
    @Expose
    public String file_path;
    @SerializedName("file_type")
    @Expose
    public String file_type;

    public Boolean getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getFile_path() {
        return file_path;
    }

    public String getFile_type() {
        return file_type;
    }
}

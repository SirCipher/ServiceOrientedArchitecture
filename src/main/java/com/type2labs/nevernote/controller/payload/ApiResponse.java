package com.type2labs.nevernote.controller.payload;

public class ApiResponse {

    private Boolean success;
    private String message;

    public ApiResponse(Boolean success) {
        this.success = success;
    }

    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}

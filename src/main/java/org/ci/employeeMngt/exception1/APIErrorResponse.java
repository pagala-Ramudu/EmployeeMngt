package org.ci.employeeMngt.exception1;

public class APIErrorResponse {

    private String errorCode;
    private String errorMessage;

    // Constructors, getters, and setters

    // Constructor for simplicity
    public APIErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}

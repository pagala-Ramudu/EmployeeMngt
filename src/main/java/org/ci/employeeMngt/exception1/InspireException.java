package org.ci.employeeMngt.exception1;

import org.springframework.validation.BindingResult;

public class InspireException extends RuntimeException {

    private  APIErrorCode errorCode;
    private  String exData;
    private  BindingResult bindingResult;

    public InspireException(String message) {
        super(message);
    }

    public InspireException(APIErrorCode errorCode, String exData, BindingResult bindingResult) {
        super();
        this.errorCode = errorCode;
        this.exData = exData;
        this.bindingResult = bindingResult;
    }

    public InspireException(APIErrorCode errorCode, String exData) {
        super();
        this.errorCode = errorCode;
        this.exData = exData;

    }

    public InspireException(APIErrorCode apiErrorCode) {
        this.errorCode = errorCode;

    }

    public APIErrorCode getErrorCode() {
        return errorCode;
    }

    public String getExData() {
        return exData;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}


package me.zeejfps.paw.models;

public class APIError {

    public static final String AUTH_INVALID_USERNAME_PASSWORD = "AUTH_INVALID_USERNAME_PASSWORD";

    private String message;
    private int errorCode;
    private String errorString;

    public String getMessage() {
        return message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorString() {
        return errorString;
    }

    @Override
    public String toString() {
        return String.format("%s %d %s", errorString, errorCode, message);
    }
}

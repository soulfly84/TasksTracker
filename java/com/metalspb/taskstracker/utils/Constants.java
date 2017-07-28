package com.metalspb.taskstracker.utils;


public final class Constants {

    //Login & Registration
    public static final String SUCCESSFULLY_LOGINNED = "Successfully loginned";
    public static final String SUCCESSFULLY_REGISTERED = "Successfully registered";
    public static final String STATUS_LOGIN_BUSY_ALREADY = "Login busy already";
    public static final String STATUS_SUCCESSFULLY_REGISTERED = "Successfully registered";
    public static final String STATUS_SUCCESSFULLY_LOGINNED = "Successfully loginned";
    public static final String STATUS_WRONG_CREDENTIALS = "Wrong credentials";
    public static final String STATUS_ERROR = "Unknown error";


    //Tasks save and update
    public static final String STATUS_TASK_CREATED = "Task created successfully";
    public static final String STATUS_TASK_UPDATED = "Updated successfully";
    public static final String STATUS_TASK_FAILED_TO_UPDATE = "Failed to update";
    public static final String STATUS_TASK_FAILED_TO_CREATE = "Failed to create task. Please try again";

    //Task status
    public final static int TASK_NEW = 0;
    public final static int TASK_PENDING = 1;
    public final static int TASK_DONE = 2;


    public static int UPDATE_STARTED = 0;
    public static int UPDATE_FINISHED = 4;


    //Tasks Loaded

    public static final String SHARE_PREF_FILE_NAME = "money_tracker_shared_pref";
    public static final String TOKEN_KEY = "token_key";

    public static final String GOOGLE_TOKEN_KEY = "google_token_key";
    private static final String G_PLUS_SCOPE = "oauth2:https://www.googleapis.com/auth/plus.me";
    private static final String USERINFO_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
    private static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
    public static final String SCOPES = G_PLUS_SCOPE + " " + USERINFO_SCOPE + " " + EMAIL_SCOPE;
    public static final String EMAIL_KEY = "email";
    public static final String NAME_KEY = "task";
    public static final String PICTURE_KEY = "picture";


}

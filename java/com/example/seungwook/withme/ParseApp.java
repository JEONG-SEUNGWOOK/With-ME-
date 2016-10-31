package com.example.seungwook.withme;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

/**
 * Created by seungwook on 2015. 11. 27..
 */
public class ParseApp extends Application{

    static final String TAG = "WithME! ";
    @Override
    public void onCreate(){
        super.onCreate();

        String Parse_Application_ID = "DI3F5oPotJllxYGwyfqgIkNZibPJqw0NgNVhc0y8";
        String Parse_Client_Key = "Hv3ZSJWXYgfgvao6QbFudAMaMc2kGIcDTiFqHCJj";


        /* Facebook SDK 초기화 */
        FacebookSdk.sdkInitialize(getApplicationContext());
        /* Parse SDK 초기화 */
        Parse.initialize(this, Parse_Application_ID, Parse_Client_Key);
        /* Parse-Facebook 연동 */
        ParseFacebookUtils.initialize(this);

        ParseUser.enableAutomaticUser();
        ParseACL defauAcl = new ParseACL();

        defauAcl.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defauAcl, true);
    }
}

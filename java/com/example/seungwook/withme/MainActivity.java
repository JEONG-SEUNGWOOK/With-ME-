package com.example.seungwook.withme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import io.socket.emitter.Emitter;
import io.socket.client.IO;
import io.socket.client.Socket;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends Activity {





    private CallbackManager callbackManager;

    public static final java.util.List<String> mPermissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
    }};

    private Button facebook_loginButton;
    private Button parse_loginButton;
    private TextView signupButton;
    private TextView resetPassword;

    private String passwordtxt;
    private String useremailtxt;
    private EditText password;
    private EditText useremail;

    private Intent gintent;

    Profile mFbProfile;
    ParseUser parseUser;
    String fname = null, femail = null;

    private static final int B_ACTIVITY = 0;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        callbackManager = CallbackManager.Factory.create();

        mSocket.on("login", onLogin);

        /* 화면 초기화 */
        setContentView(R.layout.activity_main);

        // Check if there is a currently logged in user
        // and it's linked to a Facebook account.
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            // Go to the user info activity
            startListActivity();
        }


        password = (EditText)findViewById(R.id.password);
        useremail = (EditText)findViewById(R.id.email);

        //페이스북 로그인
        facebook_loginButton = (Button)findViewById(R.id.facebook_login);
        //일반 로그인
        parse_loginButton = (Button)findViewById(R.id.parse_login);
        //회원가입
        signupButton = (TextView)findViewById(R.id.signup);
        //비밀번호 재설정
        resetPassword = (TextView)findViewById(R.id.resetpassword);


        mFbProfile = Profile.getCurrentProfile();

        /* 일반계정 로그인 */
        parse_loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useremailtxt = useremail.getText().toString();
                passwordtxt = password.getText().toString();


                ParseUser.logInInBackground(useremailtxt, passwordtxt, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, com.parse.ParseException e) {
                        if (user != null) {

                            startListActivity();
                            finish();
                        }
                        else if(useremailtxt.equals("")) {
                            Toast.makeText(getApplicationContext(), "ID를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "존재하지 않는 ID입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

            }
        });

        /* 회원가입 버튼 클릭시 */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isClickable()){
                    Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                    startActivityForResult(intent, B_ACTIVITY);
                }
            }
        });

        /* 비밀번호 재설정 버튼 클릭시 */
        resetPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(v.isClickable()){
                    startActivity(new Intent(MainActivity.this, ResetPassword.class));
                }
            }
        });

        /* 페이스북 로그인 */
        facebook_loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(MainActivity.this, mPermissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {

                        if (user == null) {
                            Log.d(ParseApp.TAG, "Uh oh. The user cancelled the Facebook login.");
                            Toast.makeText(getApplicationContext(),"Uh oh. The user cancelled the Facebook login.", Toast.LENGTH_SHORT).show();
                        } else if (user.isNew()) {
                            Log.d(ParseApp.TAG, "User signed up and logged in through Facebook!");
                            //getUserDetailsFromFB();
                            Toast.makeText(getApplicationContext(),"User signed up and logged in through Facebook!", Toast.LENGTH_SHORT).show();
                            startListActivity();
                        } else {
                            Log.d(ParseApp.TAG, "User logged in through Facebook!");
                            Toast.makeText(getApplicationContext(),"User logged in through Facebook!", Toast.LENGTH_SHORT).show();
                            //getUserDetailsFromParse();
                            startListActivity();
                        }
                    }
                });

            }
        });

        ParseUser user = new ParseUser();
        user.setUsername("my name");
        user.setPassword("my pass");
        user.setEmail("email@example.com");

// other fields can be set just like with ParseObject
        user.put("phone", "650-555-0000");

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
    }



    private void getUserDetailsFromFB() {

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        try {
                            femail = response.getJSONObject().getString("email");
                            Log.d(ParseApp.TAG,"email is "+femail);
                            fname = response.getJSONObject().getString("name");
                            Log.d(ParseApp.TAG,"name is "+fname);

                            saveNewUser();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();


    }

    private void getUserDetailsFromParse() {
        parseUser = ParseUser.getCurrentUser();

        parseUser.put("name",fname);
        parseUser.setEmail(femail);



    }
    private void saveNewUser() {
        parseUser = ParseUser.getCurrentUser();
        parseUser.put("name", fname);
        parseUser.setEmail(femail);

        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(MainActivity.this, "New user:" + fname + " Signed up", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.off("login", onLogin);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        /* 회원가입 완료한 id(email) 값 받아오기 */
        if(resultCode == RESULT_OK){
                useremailtxt = data.getExtras().getString("data");
                useremail.setText(useremailtxt);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    /* 방목록 액티비티 전환 */
    private void startListActivity() {
        mSocket.on("login", onLogin);
        gintent = new Intent(this, DrawerList.class);
        startActivity(gintent);
        finish();
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                return;
            }

            //Intent intent = new Intent();
            gintent.putExtra("username", useremailtxt);
            gintent.putExtra("numUsers", numUsers);
            //setResult(RESULT_OK, intent);
        }
    };



}

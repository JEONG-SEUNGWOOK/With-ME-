package com.example.seungwook.withme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class List extends AppCompatActivity{

    Button logout;

    TextView mUsername, mEmailID;
    ProfilePictureView userProfilePictureView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        ParseUser currentUser = ParseUser.getCurrentUser();

        mUsername = (TextView)findViewById(R.id.welcome);
        mEmailID = (TextView)findViewById(R.id.email);
        userProfilePictureView = (ProfilePictureView)findViewById(R.id.userProfilePicture);


        //Fetch Facebook user info if it is logged
        if ((currentUser != null) && currentUser.isAuthenticated()) {
            makeMeRequest();
        }



        logout=(Button)findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                startMainActivity();
                finish();
            }
        });


    }



    public void onResume() {
        super.onResume();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Check if the user is currently logged
            // and show any cached content
            updateViewsWithProfileInfo();
        } else {
            // If the user is not logged in, go to the
            // activity showing the login view.
            startMainActivity();
        }
    }


    private void makeMeRequest() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (jsonObject != null) {
                            JSONObject fid = new JSONObject();
                            String fname = null;
                            String femail = null;

                            try {
                                fid.put("facebookId",graphResponse.getJSONObject().getLong("id"));
                                fname = graphResponse.getJSONObject().getString("name");

                                if (jsonObject.getString("email") != null)
                                    femail = graphResponse.getJSONObject().getString("email");

                                // Save the user profile info in a user property
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                currentUser.put("facebookId",fid);
                                currentUser.put("name", fname);
                                currentUser.setEmail(femail);
                                currentUser.saveInBackground();

                                // Show the user info
                                updateViewsWithProfileInfo();
                            } catch (JSONException e) {
                                Log.d(ParseApp.TAG,
                                        "Error parsing returned user data. " + e);
                            }
                        } else if (graphResponse.getError() != null) {
                            switch (graphResponse.getError().getCategory()) {
                                case LOGIN_RECOVERABLE:
                                    Log.d(ParseApp.TAG,
                                            "Authentication error: " + graphResponse.getError());
                                    break;

                                case TRANSIENT:
                                    Log.d(ParseApp.TAG,
                                            "Transient error. Try again. " + graphResponse.getError());
                                    break;

                                case OTHER:
                                    Log.d(ParseApp.TAG,
                                            "Some other error: " + graphResponse.getError());
                                    break;
                            }
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void updateViewsWithProfileInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
            JSONObject fid = currentUser.getJSONObject("facebookId");

            String femail = currentUser.getEmail();
            try {

                if (currentUser.has("facebookId")) {
                    userProfilePictureView.setProfileId(fid.getString("facebookId"));
                } else {
                    // Show the default, blank user profile picture
                    userProfilePictureView.setProfileId(null);
                }

                if (currentUser.has("name")) {
                    mUsername.setText("Hello! "+currentUser.get("name"));
                } else {
                    mUsername.setText("");
                }

                if (femail != null) {
                    mEmailID.setText(femail);
                } else {
                    mEmailID.setText("");
                }

            } catch (JSONException e) {
                Log.d(ParseApp.TAG, "Error parsing saved user data.");
            }

    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}

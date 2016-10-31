package com.example.seungwook.withme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    EditText username;
    EditText useremail;
    EditText password;
    TextView signupButton;

    String usernametxt;
    String passwordtxt;
    String useremailtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        useremail = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        username = (EditText)findViewById(R.id.username);

        signupButton = (TextView)findViewById(R.id.signup);

        /* 회원가입 */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useremailtxt = useremail.getText().toString();
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();


                if (useremailtxt.equals("") && passwordtxt.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please complete the sign up form", Toast.LENGTH_SHORT).show();
                } else {
                    ParseUser user = new ParseUser();
                    user.setUsername(useremailtxt);
                    user.setPassword(passwordtxt);
                    user.setEmail(useremailtxt);
                    user.put("name", usernametxt);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.putExtra("data", useremailtxt);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "ID는 이메일형식만 가능합니다!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}

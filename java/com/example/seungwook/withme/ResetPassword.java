package com.example.seungwook.withme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ResetPassword extends AppCompatActivity {

    EditText email;
    Button resetButton ;
    String emailtxt;
    TextView noti;
    public AlertDialog mDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = (EditText)findViewById(R.id.email);
        resetButton = (Button)findViewById(R.id.resetpassword);
        noti = (TextView)findViewById(R.id.noti);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailtxt = email.getText().toString();
                ParseUser.requestPasswordResetInBackground(emailtxt,
                        new RequestPasswordResetCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    // An email was successfully sent with reset instructions.
                                    mDialog = createDialog();
                                    mDialog.show();
                                } else {
                                    // Something went wrong. Look at the ParseException to see what's up.
                                    noti.setText("email전송실패. 다시 한번 시도해주세요.");
                                }
                            }
                        });
            }
        });
    }
    private AlertDialog createDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("email 전송완료. 메일을 확인하여 비밀번호를 변경해주세요.");
        ab.setCancelable(false);

        //확인버튼 눌렀을 때
        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                finish();
            }
        });



        return ab.create();
    }
}

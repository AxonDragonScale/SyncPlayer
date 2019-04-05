package com.sdpd.syncplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUpActivity extends AppCompatActivity {

    Button btnSubmit;
    TextInputLayout tilNickWrapper;
    TextInputEditText tietNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tilNickWrapper = findViewById(R.id.til_nickWrapper);
        tietNick = findViewById(R.id.tiet_nick);
        btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nick = tietNick.getText().toString();
                GlobalData.nick = nick;

                SharedPreferences sharedPrefs = getSharedPreferences(getString(R.string.sharedPrefsKey), MODE_PRIVATE);
                SharedPreferences.Editor sharedPrefsEditor= sharedPrefs.edit();

                sharedPrefsEditor.putString(getString(R.string.nickSharedPrefsKey), nick);
                sharedPrefsEditor.putBoolean(getString(R.string.isFirstRunSharedPrefsKey), false);
                sharedPrefsEditor.commit();

                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

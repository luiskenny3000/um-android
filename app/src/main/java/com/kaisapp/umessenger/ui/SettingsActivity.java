package com.kaisapp.umessenger.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kaisapp.umessenger.R;
import com.kaisapp.umessenger.utils.Util;

import static com.kaisapp.umessenger.utils.Util.getIp;
import static com.kaisapp.umessenger.utils.Util.getPhoneNumber;
import static com.kaisapp.umessenger.utils.Util.isValidIp;
import static com.kaisapp.umessenger.utils.Util.setIp;
import static com.kaisapp.umessenger.utils.Util.setPhoneNumber;

/**
 * Created by kenny on 11/2/17.
 */

public class SettingsActivity extends AppCompatActivity {
    EditText etIp;
    EditText etPort;
    Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        etIp = (EditText)findViewById(R.id.et_ip);
        etPort = (EditText)findViewById(R.id.et_port);
        buttonSave = (Button)findViewById(R.id.button_save);

        etIp.setText(getIp(this));
        etPort.setText(String.valueOf(getPhoneNumber(this)));

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidIp(etIp.getText().toString()) && Util.isPhoneNumber(etPort.getText().toString())) {
                    setIp(SettingsActivity.this, etIp.getText().toString());
                    setPhoneNumber(SettingsActivity.this, etPort.getText().toString());
                    finish();
                } else {
                    Toast.makeText(SettingsActivity.this, "Datos inválidos", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

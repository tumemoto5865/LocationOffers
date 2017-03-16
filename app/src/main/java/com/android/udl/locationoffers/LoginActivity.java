package com.android.udl.locationoffers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.udl.locationoffers.database.CommerceSQLiteHelper;
import com.android.udl.locationoffers.database.DatabaseUtilities;
import com.android.udl.locationoffers.domain.Commerce;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText et_user, et_pass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_user = (EditText) findViewById(R.id.editText_login_user);
        et_pass = (EditText) findViewById(R.id.editText_login_pass);

        Button btn = (Button) findViewById(R.id.button_login);
        Button btn_reg = (Button) findViewById(R.id.button_register);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (login(getString(R.string.user))) {
                    startModeActivity(getString(R.string.user));
                } else if (loginCommerce()) {
                    startModeActivity(getString(R.string.commerce));
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Username or password invalid!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterCommerceActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean login (String s) {
        return et_user.getText().toString().equals(s); //&& et_pass.getText().toString().equals(s);
    }

    private boolean loginCommerce () {

        CommerceSQLiteHelper csh =
                new CommerceSQLiteHelper(getApplicationContext(), "DBCommerces", null, 1);
        DatabaseUtilities databaseUtilities = new DatabaseUtilities("Commerces", csh);
        List<Commerce> commerces = databaseUtilities.getCommerceDataFromDB();
        for (Commerce commerce: commerces) {
            if (commerce.getName().equals(et_user.getText().toString()) &&
                    commerce.getPassword().equals(et_pass.getText().toString())) {
                return true;
            }
        }
        return false;
    }

    private void startModeActivity (String s) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("mode", s);
        startActivity(intent);
        finish();
    }

}

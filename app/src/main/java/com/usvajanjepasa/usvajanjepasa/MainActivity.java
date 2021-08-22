package com.usvajanjepasa.usvajanjepasa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    Button loginBtn;
    TextView noLogin;
    Button registerBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        this.registerBtn = (Button) findViewById(R.id.register_btn);
        this.loginBtn = (Button) findViewById(R.id.login_btn);
        this.noLogin = (TextView) findViewById(R.id.noLogin);
        this.registerBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
        this.loginBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        this.noLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //HomeFragment f = new HomeFragment();
                FragmentTransaction ft = MainActivity.this.getSupportFragmentManager().beginTransaction();
                //ft.replace(R.id.content, f, "");
                ft.commit();
            }
        });
    }
}

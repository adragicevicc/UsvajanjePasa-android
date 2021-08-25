package com.usvajanjepasa.usvajanjepasa;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeProfileActivity extends AppCompatActivity {
    ActionBar actionBar;
    FirebaseAuth mAuth;
    private NavigationBarView.OnItemSelectedListener selectedListener = new NavigationBarView.OnItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_home:
                    HomeProfileActivity.this.actionBar.setTitle("Početna");
                    HomeFragment f1 = new HomeFragment();
                    FragmentTransaction ft1 = HomeProfileActivity.this.getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.content, f1, "");
                    ft1.commit();
                    return true;
                case R.id.menu_profile:
                    HomeProfileActivity.this.actionBar.setTitle("Nalog");
                    ProfileFragment f2 = new ProfileFragment();
                    FragmentTransaction ft2 = HomeProfileActivity.this.getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.content, f2, "");
                    ft2.commit();
                    return true;
                default:
                    return false;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_profile);

        this.mAuth = FirebaseAuth.getInstance();
        ((BottomNavigationView) findViewById(R.id.nav)).setOnItemSelectedListener(this.selectedListener);

        ActionBar supportActionBar = getSupportActionBar();
        this.actionBar = supportActionBar;
        supportActionBar.hide();

        HomeFragment f1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content, f1, "");
        ft1.commit();
    }

    private void checkUser() {
        if (this.mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, MainActivity.class));
            //finish();
        }
    }

    @Override
    public void onStart() {
        checkUser();
        super.onStart();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void logout() {
        this.mAuth.signOut();
        checkUser();
    }
}

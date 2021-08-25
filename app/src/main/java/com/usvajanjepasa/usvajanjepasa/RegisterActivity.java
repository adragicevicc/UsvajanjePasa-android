package com.usvajanjepasa.usvajanjepasa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText emailIn;
    TextView login2;
    private FirebaseAuth mAuth;
    EditText nameIn;
    EditText passwordIn;
    Button registerBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        nameIn = findViewById(R.id.name);
        emailIn = findViewById(R.id.email);
        passwordIn = findViewById(R.id.password);
        registerBtn = findViewById(R.id.register_btn2);
        login2 =  findViewById(R.id.login2);
        mAuth = FirebaseAuth.getInstance();
        registerBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String name = nameIn.getText().toString().trim();
                String email = emailIn.getText().toString().trim();
                String password = passwordIn.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailIn.setError("Neispravan Email");
                    emailIn.setFocusable(true);
                } else if (password.length() < 6) {
                    passwordIn.setError("Lozinka mora imati minimum 6 karaktera");
                    passwordIn.setFocusable(true);
                } else if (name.trim().length() < 1) {
                    RegisterActivity.this.nameIn.setError("Morate uneti ime!");
                    nameIn.setFocusable(true);
                } else {
                    registerUser(name, email, password);
                }
            }
        });
        this.login2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //RegisterActivity.this.startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void registerUser(final String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = RegisterActivity.this.mAuth.getCurrentUser();
                    String email = user.getEmail();
                    String uid = user.getUid();
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("email", email);
                    hashMap.put("uid", uid);
                    hashMap.put("name", name);
                    hashMap.put("phone", "");
                    FirebaseDatabase.getInstance().getReference("Users").child(uid).setValue(hashMap);
                    Toast.makeText(RegisterActivity.this, "Registrovan", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, HomeProfileActivity.class));

                }
                Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}

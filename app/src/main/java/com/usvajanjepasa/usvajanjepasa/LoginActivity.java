package com.usvajanjepasa.usvajanjepasa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText emailIn;
    Button loginBtn;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    EditText passwordIn;
    TextView register2;
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == -1) {
                try {
                    LoginActivity.this.firebaseAuthWithGoogle(GoogleSignIn.getSignedInAccountFromIntent(result.getData()).getResult(ApiException.class).getIdToken());
                } catch (ApiException e) {
                    Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    });
    SignInButton signInButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        this.emailIn = (EditText) findViewById(R.id.email);
        this.passwordIn = (EditText) findViewById(R.id.password);
        this.loginBtn = (Button) findViewById(R.id.login_btn);
        this.register2 = (TextView) findViewById(R.id.register2);
        this.signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        this.mGoogleSignInClient = GoogleSignIn.getClient((Activity) this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build());
        this.mAuth = FirebaseAuth.getInstance();

        this.loginBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String email = LoginActivity.this.emailIn.getText().toString().trim();
                String password = LoginActivity.this.passwordIn.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    LoginActivity.this.emailIn.setError("Neispravan Email");
                    LoginActivity.this.emailIn.setFocusable(true);
                    return;
                }
                LoginActivity.this.loginUser(email, password);
            }
        });
        this.register2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        this.signInButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                LoginActivity.this.resultLauncher.launch(new Intent(LoginActivity.this.mGoogleSignInClient.getSignInIntent()));
            }
        });
    }

    private void loginUser(String email, String password) {
        this.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mAuth.getCurrentUser();
                    startActivity(new Intent(LoginActivity.this, HomeProfileActivity.class));

                }
                Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        this.mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {


            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = LoginActivity.this.mAuth.getCurrentUser();
                    if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                        String email = user.getEmail();
                        String name = user.getDisplayName();
                        String uid = user.getUid();
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("email", email);
                        hashMap.put("uid", uid);
                        hashMap.put("name", name);
                        hashMap.put("phone", "");
                        FirebaseDatabase.getInstance().getReference("Users").child(uid).setValue(hashMap);
                    }
                    LoginActivity.this.startActivity(new Intent(LoginActivity.this, HomeProfileActivity.class));
                    LoginActivity.this.finish();
                    return;
                }
                Toast.makeText(LoginActivity.this, "Neuspešna prijava", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}

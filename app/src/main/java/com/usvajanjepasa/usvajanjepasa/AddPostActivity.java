package com.usvajanjepasa.usvajanjepasa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {
    private static final int IMAGE_PICK = 400;
    private final int STORAGE_REQ_CODE = ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION;
    ActionBar actionBar;
    ImageButton addImgBtn;
    DatabaseReference databaseReference;
    String email;
    ActivityResultLauncher<String> getContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {

        public void onActivityResult(Uri result) {
            if (result != null) {
                AddPostActivity.this.imgUri = result;
                AddPostActivity.this.postIV.setImageURI(result);
                AddPostActivity.this.checkImg();
            }
        }
    });
    Uri imgUri = null;
    FirebaseAuth mAuth;
    String name;
    Button postBtn;
    EditText postET;
    ImageView postIV;
    ProgressBar progressBar;
    String[] storagePermissions;
    String uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        ActionBar supportActionBar = getSupportActionBar();
        actionBar = supportActionBar;
        supportActionBar.setTitle("Dodaj novi oglas");
        mAuth = FirebaseAuth.getInstance();
        checkUser();
        postET = findViewById(R.id.postET);
        postIV = findViewById(R.id.postIV);
        postBtn = findViewById(R.id.postBtn);
        addImgBtn = findViewById(R.id.addImgBtn);
        checkImg();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference = reference;
        reference.orderByChild("email").equalTo(this.email).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    AddPostActivity.this.name = "" + ds.child("name").getValue();
                    AddPostActivity.this.email = "" + ds.child("email").getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        this.storagePermissions = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
        this.addImgBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (!AddPostActivity.this.checkPermission()) {
                    AddPostActivity.this.requestPermission();
                } else {
                    AddPostActivity.this.pickPhoto();
                }
            }
        });
        this.postBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String postText = AddPostActivity.this.postET.getText().toString().trim();
                if (TextUtils.isEmpty(postText)) {
                    Toast.makeText(AddPostActivity.this, "Tekst oglasa je obavezan", Toast.LENGTH_SHORT).show();
                } else if (AddPostActivity.this.imgUri == null) {
                    AddPostActivity.this.uploadPost(postText, "noImage");
                } else {
                    AddPostActivity addPostActivity = AddPostActivity.this;
                    addPostActivity.uploadPost(postText, String.valueOf(addPostActivity.imgUri));
                }
            }
        });
    }

    private void checkImg() {
        if (imgUri != null) {
            addImgBtn.setVisibility(View.GONE);
            postIV.setVisibility(View.VISIBLE);
        } else {
            postIV.setVisibility(View.GONE);
        }
    }

    private void uploadPost(final String postText, String uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_bar);
        final AlertDialog dialog = builder.create();
        dialog.show();
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Posts/post_" + timeStamp;
        if (!uri.equals("noImage")) {
            FirebaseStorage.getInstance().getReference().child(filePathAndName).putFile(Uri.parse(uri)).addOnSuccessListener((OnSuccessListener) new OnSuccessListener<UploadTask.TaskSnapshot>() {

                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                    do {
                    } while (!task.isSuccessful());
                    String downloadUri = task.getResult().toString();
                    if (task.isSuccessful()) {
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("uid", AddPostActivity.this.uid);
                        hashMap.put("usersName", AddPostActivity.this.name);
                        hashMap.put("usersEmail", AddPostActivity.this.email);
                        hashMap.put("postID", timeStamp);
                        hashMap.put("postText", postText);
                        hashMap.put("postImage", downloadUri);
                        hashMap.put("postTime", timeStamp);
                        FirebaseDatabase.getInstance().getReference().child("Posts").child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {

                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddPostActivity.this, "Oglas je dodat!", Toast.LENGTH_SHORT).show();
                                AddPostActivity.this.postET.setText("");
                                AddPostActivity.this.postIV.setImageURI(null);
                                AddPostActivity.this.imgUri = null;
                                dialog.dismiss();
                                AddPostActivity.this.startActivity(new Intent(AddPostActivity.this, HomeProfileActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {

                            @Override
                            public void onFailure(Exception e) {
                                dialog.dismiss();
                                Toast.makeText(AddPostActivity.this, "Dogodila se greska. Pokusajte ponovo.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener((OnFailureListener) new OnFailureListener() {
                /* class com.usvajanjepasaandroid.usvajanjepasaandroid.AddPostActivity.AnonymousClass4 */

                @Override // com.google.android.gms.tasks.OnFailureListener
                public void onFailure(Exception e) {
                    dialog.dismiss();
                    Toast.makeText(AddPostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("uid", this.uid);
        hashMap.put("usersName", this.name);
        hashMap.put("usersEmail", this.email);
        hashMap.put("postID", timeStamp);
        hashMap.put("postText", postText);
        hashMap.put("postImage", "noImage");
        hashMap.put("postTime", timeStamp);
        FirebaseDatabase.getInstance().getReference().child("Posts").child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            /* class com.usvajanjepasaandroid.usvajanjepasaandroid.AddPostActivity.AnonymousClass7 */

            public void onSuccess(Void aVoid) {
                Toast.makeText(AddPostActivity.this, "Oglas je dodat!", Toast.LENGTH_SHORT).show();
                AddPostActivity.this.postET.setText("");
                AddPostActivity.this.postIV.setImageURI(null);
                AddPostActivity.this.imgUri = null;
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            /* class com.usvajanjepasaandroid.usvajanjepasaandroid.AddPostActivity.AnonymousClass6 */

            @Override // com.google.android.gms.tasks.OnFailureListener
            public void onFailure(Exception e) {
                dialog.dismiss();
                Toast.makeText(AddPostActivity.this, "Dogodila se greska. Pokusajte ponovo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void pickPhoto() {
        new Intent("android.intent.action.PICK").setType("image/*");
        this.getContent.launch("image/*");
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, this.storagePermissions, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStart() {
        super.onStart();
        checkUser();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        checkUser();
    }

    @Override // androidx.appcompat.app.AppCompatActivity
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void checkUser() {
        FirebaseUser user = this.mAuth.getCurrentUser();
        if (user != null) {
            this.email = user.getEmail();
            this.uid = user.getUid();
            return;
        }
        startActivity(new Intent(this, LoginActivity.class));
        //finish();
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback, androidx.fragment.app.FragmentActivity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean storageAccepted = false;
        if (grantResults.length > 0) {
            if (grantResults[0] == 0) {
                storageAccepted = true;
            }
            if (storageAccepted) {
                pickPhoto();
                return;
            }
            return;
        }
        Toast.makeText(this, "Neophodne su dozvole za pristup", Toast.LENGTH_SHORT).show();
    }
}

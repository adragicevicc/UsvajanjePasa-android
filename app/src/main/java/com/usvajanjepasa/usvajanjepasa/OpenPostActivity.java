package com.usvajanjepasa.usvajanjepasa;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class OpenPostActivity extends AppCompatActivity {
    AdapterComments adapterComments;
    RelativeLayout addCommLay;
    EditText commentET;
    List<ModelComment> commentList;
    String currEmail;
    String currName;
    String currUid;
    AlertDialog dialog;
    LinearLayout linearLayout;
    ImageButton moreOptBtn;
    String pUid;
    String pUsersEmail;
    String pUsersName;
    String pText;
    TextView pUsersNameTV;
    ImageButton postCommentBtn;
    TextView postCommentsTV;
    String postID;
    String postImage;
    ImageView postImageIV;
    TextView postTextTV;
    RecyclerView recyclerView;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_post);
        getSupportActionBar().hide();
        this.postID = getIntent().getStringExtra("postID");
        this.pUsersNameTV = (TextView) findViewById(R.id.pUsersNameTV);
        this.postTextTV = (TextView) findViewById(R.id.postTextTV);
        this.postImageIV = (ImageView) findViewById(R.id.postImageIV);
        this.moreOptBtn = (ImageButton) findViewById(R.id.moreOptBtn);
        this.addCommLay = (RelativeLayout) findViewById(R.id.addCommLay);
        this.linearLayout = (LinearLayout) findViewById(R.id.profileLinLay);
        this.recyclerView = (RecyclerView) findViewById(R.id.commentsRV);
        this.commentET = (EditText) findViewById(R.id.commentET);
        this.postCommentBtn = (ImageButton) findViewById(R.id.postCommentBtn);
        openPost();
        checkUser();
        loadUsersInfo();
        loadComments();
        this.postCommentBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                OpenPostActivity.this.postComment();
            }
        });
    }

    private void loadUsersInfo() {
        FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(this.currUid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override // com.google.firebase.database.ValueEventListener
            public void onDataChange(DataSnapshot snapshot) {
                Iterator<DataSnapshot> it = snapshot.getChildren().iterator();
                while (it.hasNext()) {
                    OpenPostActivity.this.currName = "" + it.next().child("name").getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void loadComments() {
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.commentList = new ArrayList();
        FirebaseDatabase.getInstance().getReference("Posts").child(this.postID).child("comments").addValueEventListener(new ValueEventListener() {

            @Override // com.google.firebase.database.ValueEventListener
            public void onDataChange(DataSnapshot snapshot) {
                OpenPostActivity.this.commentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelComment modelComment = (ModelComment) dataSnapshot.getValue(ModelComment.class);
                    Log.d("TAG", modelComment.getComment());
                    OpenPostActivity.this.commentList.add(modelComment);
                    OpenPostActivity.this.adapterComments = new AdapterComments(OpenPostActivity.this.getApplicationContext(), OpenPostActivity.this.commentList);
                    OpenPostActivity.this.recyclerView.setAdapter(OpenPostActivity.this.adapterComments);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void postComment() {
        String comment = this.commentET.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, "Komentar ne moze biti prazan!", Toast.LENGTH_SHORT).show();
            return;
        }
        String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(this.postID).child("comments");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comID", timestamp);
        hashMap.put("comment", comment);
        hashMap.put("currUid", this.currUid);
        hashMap.put("currEmail", this.currEmail);
        hashMap.put("currName", this.currName);
        databaseReference.child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {

            public void onSuccess(Void aVoid) {
                Toast.makeText(OpenPostActivity.this, "Komentar dodat!", Toast.LENGTH_SHORT).show();
                OpenPostActivity.this.commentET.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override // com.google.android.gms.tasks.OnFailureListener
            public void onFailure(Exception e) {
                Toast.makeText(OpenPostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openPost() {
        FirebaseDatabase.getInstance().getReference("Posts").orderByChild("postID").equalTo(this.postID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //String str = "" + dataSnapshot.child("postText").getValue();
                    String str2 = "" + dataSnapshot.child("postTime").getValue();
                    postImage = "" + dataSnapshot.child("postImage").getValue();
                    pText = "" + dataSnapshot.child("postText").getValue();
                    pUid = "" + dataSnapshot.child("uid").getValue();
                    pUsersEmail = "" + dataSnapshot.child("usersEmail").getValue();
                    pUsersName = "" + dataSnapshot.child("usersName").getValue();
                    pUsersNameTV.setText(pUsersName);
                    postTextTV.setText(pText);
                    if (postImage.equals("noImage")) {
                        postImageIV.setVisibility(View.GONE);
                    } else {
                        postImageIV.setVisibility(View.VISIBLE);
                        try {
                            Picasso.get().load(postImage).into(postImageIV);
                        } catch (Exception e) {
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void checkUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            this.currEmail = user.getEmail();
            this.currUid = user.getUid();
            return;
        }
        this.addCommLay.setVisibility(View.GONE);
    }
}

package com.usvajanjepasa.usvajanjepasa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {
    AdapterPost adapterPost;
    DatabaseReference databaseReference;
    FirebaseDatabase db;
    TextView emailTV;
    FloatingActionButton floatingActionButton;
    FirebaseAuth mAuth;
    TextView nameTV;
    TextView phoneTV;
    List<ModelPost> postList;
    RecyclerView profilePostsRV;
    String uid;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        FirebaseAuth instance = FirebaseAuth.getInstance();
        this.mAuth = instance;
        this.user = instance.getCurrentUser();
        FirebaseDatabase instance2 = FirebaseDatabase.getInstance();
        this.db = instance2;
        this.databaseReference = instance2.getReference("Users");
        this.nameTV = (TextView) view.findViewById(R.id.nameTV);
        this.emailTV = (TextView) view.findViewById(R.id.emailTV);
        this.phoneTV = (TextView) view.findViewById(R.id.phoneTV);
        this.uid = this.user.getUid();
        this.floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.profilePostsRV);
        this.profilePostsRV = recyclerView;
        recyclerView.setNestedScrollingEnabled(false);
        this.postList = new ArrayList();
        this.databaseReference.orderByChild("email").equalTo(this.user.getEmail()).addValueEventListener(new ValueEventListener() {
            /* class com.usvajanjepasaandroid.usvajanjepasaandroid.ProfileFragment.AnonymousClass1 */

            @Override // com.google.firebase.database.ValueEventListener
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ProfileFragment.this.nameTV.setText("" + ds.child("name").getValue());
                    ProfileFragment.this.emailTV.setText("" + ds.child("email").getValue());
                    ProfileFragment.this.phoneTV.setText("" + ds.child("phone").getValue());
                }
            }

            @Override // com.google.firebase.database.ValueEventListener
            public void onCancelled(DatabaseError error) {
            }
        });
        this.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            /* class com.usvajanjepasaandroid.usvajanjepasaandroid.ProfileFragment.AnonymousClass2 */

            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileFragment.this.getActivity());
                builder.setItems(new String[]{"Izmeni ime", "Izmeni telefon", "Odjavi se"}, new DialogInterface.OnClickListener() {
                    /* class com.usvajanjepasaandroid.usvajanjepasaandroid.ProfileFragment.AnonymousClass2.AnonymousClass1 */

                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            ProfileFragment.this.editProfile("ime", "name");
                        } else if (which == 1) {
                            ProfileFragment.this.editProfile("telefon", "phone");
                        } else if (which == 2) {
                            ((HomeProfileActivity) ProfileFragment.this.getActivity()).logout();
                        }
                    }
                });
                builder.create().show();
            }
        });
        showPosts();
        return view;
    }

    private void showPosts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        this.profilePostsRV.setLayoutManager(layoutManager);
        FirebaseDatabase.getInstance().getReference("Posts").orderByChild("uid").equalTo(this.uid).addValueEventListener(new ValueEventListener() {
            /* class com.usvajanjepasaandroid.usvajanjepasaandroid.ProfileFragment.AnonymousClass3 */

            @Override // com.google.firebase.database.ValueEventListener
            public void onDataChange(DataSnapshot snapshot) {
                ProfileFragment.this.postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ProfileFragment.this.postList.add((ModelPost) dataSnapshot.getValue(ModelPost.class));
                    ProfileFragment.this.adapterPost = new AdapterPost(ProfileFragment.this.getActivity(), ProfileFragment.this.postList);
                    ProfileFragment.this.profilePostsRV.setAdapter(ProfileFragment.this.adapterPost);
                }
            }

            @Override // com.google.firebase.database.ValueEventListener
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProfileFragment.this.getActivity(), "" + error.getMessage(), 0).show();
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void editProfile(final String forEdit, final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Izmeni " + forEdit);
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(1);
        linearLayout.setPadding(10, 10, 10, 10);
        final EditText editText = new EditText(getActivity());
        editText.setHint("Unesi " + forEdit);
        linearLayout.addView(editText);
        builder.setView(linearLayout);
        builder.setPositiveButton("Izmeni", new DialogInterface.OnClickListener() {
            /* class com.usvajanjepasaandroid.usvajanjepasaandroid.ProfileFragment.AnonymousClass4 */

            public void onClick(DialogInterface dialog, int which) {
                final String val = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(val)) {
                    HashMap<String, Object> res = new HashMap<>();
                    res.put(key, val);
                    ProfileFragment.this.databaseReference.child(ProfileFragment.this.user.getUid()).updateChildren(res).addOnSuccessListener(new OnSuccessListener<Void>() {
                        /* class com.usvajanjepasaandroid.usvajanjepasaandroid.ProfileFragment.AnonymousClass4.AnonymousClass2 */

                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileFragment.this.getActivity(), "Uspešno ste izmenili " + forEdit, 0).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        /* class com.usvajanjepasaandroid.usvajanjepasaandroid.ProfileFragment.AnonymousClass4.AnonymousClass1 */

                        @Override // com.google.android.gms.tasks.OnFailureListener
                        public void onFailure(Exception e) {
                            Toast.makeText(ProfileFragment.this.getActivity(), "" + e.getMessage(), 0).show();
                        }
                    });
                    if (key.equals("name")) {
                        FirebaseDatabase.getInstance().getReference("Posts").orderByChild("uid").equalTo(ProfileFragment.this.uid).addValueEventListener(new ValueEventListener() {
                            /* class com.usvajanjepasaandroid.usvajanjepasaandroid.ProfileFragment.AnonymousClass4.AnonymousClass3 */

                            @Override // com.google.firebase.database.ValueEventListener
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    dataSnapshot.getRef().child(dataSnapshot.getKey()).child("usersName").setValue(val);
                                }
                            }

                            @Override // com.google.firebase.database.ValueEventListener
                            public void onCancelled(DatabaseError error) {
                            }
                        });
                        return;
                    }
                    return;
                }
                Toast.makeText(ProfileFragment.this.getActivity(), "Unesite " + forEdit, 0).show();
            }
        });
        builder.setNegativeButton("Otkaži", new DialogInterface.OnClickListener() {
            /* class com.usvajanjepasaandroid.usvajanjepasaandroid.ProfileFragment.AnonymousClass5 */

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}

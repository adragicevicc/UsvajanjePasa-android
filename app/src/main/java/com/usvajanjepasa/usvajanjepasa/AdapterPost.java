package com.usvajanjepasa.usvajanjepasa;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder> {
    Context context;
    String currUid;
    FirebaseAuth mAuth;
    List<ModelPost> postList;
    FirebaseUser user;

    public AdapterPost(Context context2, List<ModelPost> postList2) {
        this.context = context2;
        this.postList = postList2;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            this.currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            this.currUid = "";
        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(this.context).inflate(R.layout.posts, parent, false));
    }

    public void onBindViewHolder(final MyHolder holder, int position) {
        String uid = this.postList.get(position).getUid();
        String usersName = this.postList.get(position).getUsersName();
        String usersEmail = this.postList.get(position).getUsersEmail();
        String postID = this.postList.get(position).getPostID();
        String postText = this.postList.get(position).getPostText();
        String pImage = this.postList.get(position).getPostImage();
        String postTime = this.postList.get(position).getPostTime();

        this.postList.get(position).getPostComments();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(postTime));
        DateFormat.format("dd/MM/yyy hh:mm:aa", calendar).toString();

        FirebaseAuth instance = FirebaseAuth.getInstance();
        this.mAuth = instance;
        this.user = instance.getCurrentUser();

        holder.usersName.setText(usersName);
        holder.postText.setText(postText);

        if (usersName.equals("")) {
            holder.usersName.setText(usersEmail);
        } else {
            holder.usersName.setText(usersName);
        }
        if (pImage.equals("noImage")) {
            holder.postImage.setVisibility(View.GONE);
        } else {
            holder.postImage.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(pImage).into(holder.postImage);
            } catch (Exception e) {
            }
        }
        if (user != null) {
            if (currUid.equals(uid) || user.getEmail().equals("testusvajanje@gmail.com")) {
                holder.moreOptBtn.setVisibility(View.VISIBLE);
            } else {
                holder.moreOptBtn.setVisibility(View.GONE);
            }
        }
        holder.moreOptBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                AdapterPost.this.showOpt(holder.moreOptBtn, uid, AdapterPost.this.currUid, postID, pImage);
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(AdapterPost.this.context, OpenPostActivity.class);
                intent.putExtra("postID", postID);
                AdapterPost.this.context.startActivity(intent);
            }
        });
    }


    private void showOpt(ImageButton moreOptBtn, String uid, String currUid2, final String postID, final String pImage) {
        PopupMenu popupMenu = new PopupMenu(this.context, moreOptBtn, GravityCompat.END);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Izbrisi");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() != 0) {
                    return false;
                }
                AdapterPost.this.deletePost(postID, pImage);
                return false;
            }
        });
        popupMenu.show();
    }


    private void deletePost(final String postID, String pImage) {
        if (pImage.equals("noImage")) {
            deleteData(postID);
        } else {
            FirebaseStorage.getInstance().getReferenceFromUrl(pImage).delete().addOnSuccessListener(new OnSuccessListener<Void>() {

                public void onSuccess(Void aVoid) {
                    AdapterPost.this.deleteData(postID);
                }
            }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(AdapterPost.this.context, "Dogodila se greska. Pokusajte ponovo", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteData(String postID) {
        FirebaseDatabase.getInstance().getReference("Posts").orderByChild("postID").equalTo(postID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().removeValue();
                }
                Toast.makeText(AdapterPost.this.context, "Oglas je uspesno izbrisan", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder {
        Button commentBtn;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        ImageButton moreOptBtn;
        ImageView postImage;
        TextView postText;
        LinearLayout profileLayout;
        TextView usersName;

        public MyHolder(View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.postImageIV);
            usersName = itemView.findViewById(R.id.userNameTV);
            postText = itemView.findViewById(R.id.postTextTV);
            moreOptBtn = itemView.findViewById(R.id.moreOptBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            profileLayout = itemView.findViewById(R.id.profileLinLay);

            checkUser();
        }

        private void checkUser() {
            if (mAuth.getCurrentUser() == null) {
                moreOptBtn.setVisibility(View.GONE);
            }
        }
    }
}

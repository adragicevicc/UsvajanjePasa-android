package com.usvajanjepasa.usvajanjepasa;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    AdapterPost adapterPost;
    Button addPostBtn;
    FirebaseAuth mAuth;
    List<ModelPost> postList;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        addPostBtn = view.findViewById(R.id.addPostBtn);
        mAuth = FirebaseAuth.getInstance();
        checkUser();
        RecyclerView recyclerView2 = view.findViewById(R.id.postsRecV);
        recyclerView = recyclerView2;
        recyclerView2.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        postList = new ArrayList();
        loadPosts();
        addPostBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddPostActivity.class));

            }
        });
        return view;
    }

    private void loadPosts() {
        FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    postList.add(dataSnapshot.getValue(ModelPost.class));
                    adapterPost = new AdapterPost(getActivity(), postList);
                    recyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUser() {
        if (this.mAuth.getCurrentUser() == null) {
            this.addPostBtn.setVisibility(View.GONE);
        }
    }
}

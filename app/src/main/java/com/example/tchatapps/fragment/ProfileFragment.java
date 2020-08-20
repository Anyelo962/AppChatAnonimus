package com.example.tchatapps.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tchatapps.Adapter.FotosAdapter;
import com.example.tchatapps.Models.Post;
import com.example.tchatapps.Models.User;
import com.example.tchatapps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {

    ImageView image_profile, option;
    TextView posts, follower, following, fullname,bio, username;
    Button edit_profile;

    RecyclerView recyclerView;
    FotosAdapter fotosAdapter;
    List<Post> postList;

    FirebaseUser firebaseUser;
    String profileId;
    ImageButton my_photos, save_photos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

      View view =  inflater.inflate(R.layout.profile_fragment, container, false);

      firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("profileid", "none");

        image_profile = view.findViewById(R.id.idImage_profile);
        option = view.findViewById(R.id.option);
        posts = view.findViewById(R.id.idPosts);
        follower = view.findViewById(R.id.idFollowersProfile);
        following = view.findViewById(R.id.idFollowingProfile);
        fullname = view.findViewById(R.id.fullnameProfile);
        username = view.findViewById(R.id.usernameProfile);
        edit_profile = view.findViewById(R.id.edit_profileProfile);
        bio = view.findViewById(R.id.bioProfile);
        my_photos = view.findViewById(R.id.my_fotosProfile);

        recyclerView = view.findViewById(R.id.recyclerViewPictureProfile);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        fotosAdapter = new FotosAdapter(getContext(), postList);
        recyclerView.setAdapter(fotosAdapter);

        userInfo();
        getFollower();
        getNrPost();
        fotos();

        if(profileId.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit Profile");
        }
        else{
            checkFollowing();
            image_profile.setVisibility(View.GONE);
        }
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String btn = edit_profile.getText().toString();
                if(btn.equals("Edit profile")){
                        //Aqui para editar el perfil del usuario
                }
                else if(btn.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseUser.getUid()).child("following").child(profileId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(profileId).child("followers")
                            .child(firebaseUser.getUid()).setValue(true);
                }
                else if(btn.equals("following")){
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(firebaseUser.getUid()).child("following")
                            .child(profileId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(profileId).child("followers")
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        return view;
    }

    public void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               try {
                   if(getContext() == null){
                       return;
                   }
                   User user = snapshot.getValue(User.class);
                   Glide.with(getContext()).load(user.getImagenUrl()).into(image_profile);
                   username.setText(user.getNombre());
                   fullname.setText(user.getNombreCompleto());

               }catch (Exception error){
                   Toast.makeText(getContext(),""+error, Toast.LENGTH_LONG);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkFollowing(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("follow").child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileId).exists()){
                    edit_profile.setText("following");
                }else{
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getFollower(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(profileId).child("followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                follower.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference referenceOne = FirebaseDatabase.getInstance().getReference()
                .child("follow").child(profileId).child("following");
        referenceOne.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getNrPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getPostPublisher().equals(profileId)){
                        i++;
                    }
                }

                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);

                    if(post.getPostPublisher().equals(profileId)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                fotosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

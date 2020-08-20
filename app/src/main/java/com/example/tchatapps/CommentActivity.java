package com.example.tchatapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.tchatapps.Adapter.commentAdapter;
import com.example.tchatapps.Models.Comments;
import com.example.tchatapps.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private commentAdapter commentAdapte;
    private List<Comments> commentsList;
    EditText addComment;
    ImageView imageProfile;
    TextView post;
    String postId;
    String publisher;

    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.idToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.idRecyclerViewComment);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentsList = new ArrayList<>();
        commentAdapte = new commentAdapter(this, commentsList);
        recyclerView.setAdapter(commentAdapte);


        addComment = findViewById(R.id.id_addComment);
        imageProfile = findViewById(R.id.id_imageProfile);
        post = findViewById(R.id.id_post);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        postId = intent.getStringExtra("PostId");
        publisher = intent.getStringExtra("publisherId");

       post.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(addComment.getText().toString().equals("")){
                   Toast.makeText(getApplicationContext(), "No puede enviar comentarios vacioes", Toast.LENGTH_SHORT).show();
               }
               else{
                   addCommentUser();
               }
           }
       });

       getImage();
       readcomment();
    }

    public void addCommentUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments")
                .child(postId);
        Map<String, Object> hashMap = new HashMap<>();

        hashMap.put("Comment", addComment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());

        reference.push().setValue(hashMap);
        addComment.setText("");
    }
    private void getImage(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImagenUrl()).into(imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readcomment(){
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference().child("comments").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Comments comments = snapshot.getValue(Comments.class);
                    commentsList.add(comments);
                }

                commentAdapte.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
package com.example.tchatapps.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tchatapps.Models.User;
import com.example.tchatapps.R;
import com.example.tchatapps.fragment.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder> {

    private Context mContext;
    private List<User> mUser;
    private boolean isFragment;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUser, boolean isFragment) {
        this.mContext = mContext;
        this.mUser = mUser;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent,false);
        return new UserAdapter.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            final User user = mUser.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);


        holder.username.setText(user.getNombre());
        holder.fullname.setText(user.getNombreCompleto());
        Glide.with(mContext).load(user.getImagenUrl()).into(holder.imageView);
        isFollowin(user.getId(), holder.btn_follow);

        if(user.getId().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE ).edit();
                editor.putString("profileid", user.getId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.id_fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.btn_follow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(firebaseUser.getUid()).child("following")
                            .child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(user.getId()).child("followers")
                            .child(firebaseUser.getUid()).setValue(true);
                } else{
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(firebaseUser.getUid()).child("following")
                            .child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(user.getId()).child("followers")
                            .child(firebaseUser.getUid()).removeValue();
                }

            }

        });


    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        private TextView username;
        private TextView fullname;
        public CircleImageView imageView;
        public Button btn_follow;
        public viewholder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.id_username);
            fullname = itemView.findViewById(R.id.id_fullName);
            imageView = itemView.findViewById(R.id.id_imageProfile);

            btn_follow = itemView.findViewById(R.id.id_BuscarButton);
        }
    }

    private void isFollowin(final String userId, final Button button){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("follow").child(firebaseUser.getUid()).child("following");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(userId).exists()){
                    button.setText("following");

                }
                else{
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}

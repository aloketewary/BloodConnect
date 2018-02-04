package io.aloketewary.bloodconnect.page.userAction.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.aloketewary.bloodconnect.R;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUserList;
    // Firebase
    private DatabaseReference mUserDatabse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = (Toolbar) findViewById(R.id.users_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Users List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Firebase
        mUserDatabse = FirebaseDatabase.getInstance().getReference().child("users");

        mUserList = (RecyclerView) findViewById(R.id.users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users, UserViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<Users, UserViewHolder>(
                Users.class,
                R.layout.users_single_layout,
                UserViewHolder.class,
                mUserDatabse
        ) {
            @Override
            protected void populateViewHolder(UserViewHolder userViewHolder, Users users, int position) {
                userViewHolder.setName(users.getName());
                userViewHolder.setStatus(users.getStatus());
                userViewHolder.setImage(users.getThumb_image(), getApplicationContext());

                final String userId = getRef(position).getKey();

                userViewHolder.mView.setOnClickListener(new View.OnClickListener(){
                   @Override
                   public void onClick(View view) {
                       Intent profileIntent = new Intent(UsersActivity.this, ProfileActivity.class);
                       profileIntent.putExtra("user_id", userId);
                       startActivity(profileIntent);
                   }
               });
            }
        };

        mUserList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public UserViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String name) {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setStatus(String status) {
            TextView userStatusView = mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }

        public void setImage(String thumbImage, Context ctx) {
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.users_single_image);
            Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.default_avatar).into(userImageView);
        }
    }
}

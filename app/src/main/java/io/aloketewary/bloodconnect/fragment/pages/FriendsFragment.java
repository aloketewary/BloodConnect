package io.aloketewary.bloodconnect.fragment.pages;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.aloketewary.bloodconnect.R;
import io.aloketewary.bloodconnect.page.userAction.activity.ProfileActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsRecyclerView;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;

    private String mCurrentUserId;
    private View mMainView;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_friends, container,false);

        mFriendsRecyclerView = (RecyclerView) mMainView.findViewById(R.id.friends_reclyer_view);
        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends").child(mCurrentUserId);
        mFriendsDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUserDatabase.keepSynced(true);

        mFriendsRecyclerView.setHasFixedSize(true);
        mFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mFriendsDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, int position) {
                friendsViewHolder.setDate(friends.getDate());

                final String listUserId = getRef(position).getKey();

                mUserDatabase.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        final String userImage = dataSnapshot.child("thumb_image").getValue().toString();
                        if(dataSnapshot.hasChild("online")) {
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setOnlineStatus(userOnline);
                        }

                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setImage(userImage, getContext());

                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence charSequence[] = new CharSequence[]{"Open Profile", "Send Message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setItems(charSequence, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(i == 0) {
                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("user_id", listUserId);
                                            startActivity(profileIntent);
                                        } else if(i == 1) {
                                            Intent profileIntent = new Intent(getContext(), ChatActivity.class);
                                            profileIntent.putExtra("user_id", listUserId);
                                            profileIntent.putExtra("user_name", userName);
                                            profileIntent.putExtra("user_thumb_image", userImage);
                                            startActivity(profileIntent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mFriendsRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDate(String date) {
            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(date);
        }

        public void setName(String name) {
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        public void setImage(String thumbImage, Context ctx) {
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.users_single_image);
            Picasso.with(ctx).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_avatar).into(userImageView);
        }

        public void setOnlineStatus(String userOnline) {
            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online);
            if(userOnline.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }
}

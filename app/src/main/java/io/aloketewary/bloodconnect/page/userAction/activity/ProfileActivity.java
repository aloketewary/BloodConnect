package io.aloketewary.bloodconnect.page.userAction.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.aloketewary.bloodconnect.R;
import io.aloketewary.bloodconnect.util.Constant;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriedsCount;
    private Button mSendReqBtn, mDeclinedBtn;

    private ProgressDialog mProfileProgress;
    // Firebase
    private DatabaseReference mUserDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;

    private int mCurrentState;

    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String userId = getIntent().getStringExtra("user_id");

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mRelativeLayout = (RelativeLayout)findViewById(R.id.profile_relative_layout);

        mProfileImage = (ImageView) findViewById(R.id.profile_avatar_image);
        mProfileName = (TextView) findViewById(R.id.profile_display_name);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriedsCount = (TextView) findViewById(R.id.profile_total_friends);
        mSendReqBtn = (Button) findViewById(R.id.profile_send_req);
        mDeclinedBtn = (Button) findViewById(R.id.profile_decline_req);

        mCurrentState = Constant.NOT_FRIEND;

        mProfileProgress = new ProgressDialog(this);
        mProfileProgress.setTitle("Loading User Data");
        mProfileProgress.setMessage("Please wait while we load user data");
        mProfileProgress.setCanceledOnTouchOutside(false);
        mProfileProgress.show();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String displayName = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String onlineStat = dataSnapshot.child("online").getValue().toString();
                // Set online status
                if(onlineStat.equals("true")) {
                    mRelativeLayout.setVisibility(View.VISIBLE);
                } else {
                    mRelativeLayout.setVisibility(View.GONE);
                }

                mProfileName.setText(displayName);
                mProfileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

                //----------------------- FRIEND LIST / REQUEST FEATURE -------------------------
                mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(userId)) {
                            String req_type = dataSnapshot.child(userId).child("req_type").getValue().toString();
                            if (req_type.equals("received")) {
                                mCurrentState = Constant.FRIEND_REQUEST_RECEIVED;
                                mSendReqBtn.setText("Accept friend Request");
                                mDeclinedBtn.setVisibility(View.VISIBLE);
                                mDeclinedBtn.setEnabled(true);

                            } else if (req_type.equals("sent")) {
                                mCurrentState = Constant.FRIEND_REQUEST_SENT;
                                mSendReqBtn.setText("cancel friend Request");

                                mDeclinedBtn.setVisibility(View.INVISIBLE);
                                mDeclinedBtn.setEnabled(false);
                            }
                            mProfileProgress.dismiss();
                        } else {
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(userId)) {
                                        mCurrentState = Constant.FRIENDS;
                                        mSendReqBtn.setText("Unfriend this person");
                                        mDeclinedBtn.setVisibility(View.INVISIBLE);
                                        mDeclinedBtn.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            mProfileProgress.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSendReqBtn.setEnabled(false);
                // --------------- NOT FRIEND STATE ---------------
                if (mCurrentState == Constant.NOT_FRIEND) {
                    DatabaseReference newNotificationRef = mRootRef.child("notifications").child(userId).push();
                    String newNotificationId = newNotificationRef.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", mCurrentUser.getUid());
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("friend_req/" + mCurrentUser.getUid() + "/" + userId + "/req_type", Constant.REQUEST_SEND);
                    requestMap.put("friend_req/" + userId + "/" + mCurrentUser.getUid() + "/req_type", Constant.REQUEST_RECEIVED);
                    requestMap.put("notifications/" + userId + "/" + newNotificationId, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(ProfileActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                            }
                            mSendReqBtn.setEnabled(true);
                            mCurrentState = Constant.FRIEND_REQUEST_SENT;
                            mSendReqBtn.setText("cancel friend Request");
                            mDeclinedBtn.setVisibility(View.INVISIBLE);
                            mDeclinedBtn.setEnabled(false);
                        }
                    });
                }

                // ---------------- CANCEL REQUEST STATE ----------------
                if (mCurrentState == Constant.FRIEND_REQUEST_SENT) {
                    Map cancelReqMap = new HashMap();
                    cancelReqMap.put("friend_req/" + mCurrentUser.getUid() + "/" + userId, null);
                    cancelReqMap.put("friend_req/" + userId + "/" + mCurrentUser.getUid(), null);
                    mRootRef.updateChildren(cancelReqMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            mSendReqBtn.setEnabled(true);
                            mCurrentState = Constant.NOT_FRIEND;
                            mSendReqBtn.setText("send friend Request");
                            mDeclinedBtn.setVisibility(View.INVISIBLE);
                            mDeclinedBtn.setEnabled(false);
                        }
                    });
                }

                // --------------------- REQUEST RECEIVED STATE -----------------------------
                if (mCurrentState == Constant.FRIEND_REQUEST_RECEIVED) {
                    String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("friends/" + mCurrentUser.getUid() + "/" + userId + "/date", currentDate);
                    friendsMap.put("friends/" + userId + "/" + mCurrentUser.getUid() + "/date", currentDate);

                    friendsMap.put("friend_req/" + mCurrentUser.getUid() + "/" + userId, null);
                    friendsMap.put("friend_req/" + userId + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            mFriendReqDatabase.child(userId).child(mCurrentUser.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mSendReqBtn.setEnabled(true);
                                            mCurrentState = Constant.FRIENDS;
                                            mSendReqBtn.setText("Unfriend this person");
                                            mDeclinedBtn.setVisibility(View.INVISIBLE);
                                            mDeclinedBtn.setEnabled(false);
                                        }
                                    });
                        }
                    });
                }

                // --------------------- UNFRIEND REQUEST --------------------
                if (mCurrentState == Constant.FRIENDS) {
                    Map unfriendMap = new HashMap();
                    unfriendMap.put("friends/" + mCurrentUser.getUid() + "/" + userId, null);
                    unfriendMap.put("friends/" + userId + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                mSendReqBtn.setEnabled(true);
                                mCurrentState = Constant.NOT_FRIEND;
                                mSendReqBtn.setText("Send Friend Request");
                                mDeclinedBtn.setVisibility(View.INVISIBLE);
                                mDeclinedBtn.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser != null) {
            mRootRef.child("users").child(mCurrentUser.getUid()).child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mCurrentUser != null) {
            mRootRef.child("users").child(mCurrentUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}

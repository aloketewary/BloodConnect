package io.aloketewary.bloodconnect.fragment.pages;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.aloketewary.bloodconnect.R;
import io.aloketewary.bloodconnect.TimeAgo;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    private String mChatUser;
    private String mChatUsername;
    private String mCurrentUserId;

    private TextView mChatTitleView;
    private TextView mChatLastSeenView;
    private CircleImageView mProfileImage;
    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatMessage;

    private Toolbar mChatToolbar;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutMgr;
    private MessageAdapter mAdapter;

    private static final int TOTAL_ITEM_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private int loadPos = 0;
    private String lastKey = "";
    private String prevKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mChatUser = getIntent().getStringExtra("user_id");
        mChatUsername = getIntent().getStringExtra("user_name");

        mChatToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mChatMessage = (EditText) findViewById(R.id.chat_message);
        mChatAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
        mChatSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);

        mAdapter = new MessageAdapter(messagesList);
        mMessagesList = (RecyclerView) findViewById(R.id.chat_messages_list);
        mLinearLayoutMgr = new LinearLayoutManager(this);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayoutMgr);
        mMessagesList.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chat_swipe_message_layout);

        loadMessages();

        // getSupportActionBar().setTitle(mChatUsername);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarView = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(actionbarView);
        //----------- CUSTOM ACTION BAR ITEMS ------------------
        mChatTitleView = (TextView) findViewById(R.id.chat_custom_display_name);
        mChatLastSeenView = (TextView) findViewById(R.id.chat_custom_last_seen);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image);

        mChatTitleView.setText(mChatUsername);

        mRootRef.child("users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumbImage = dataSnapshot.child("thumb_image").getValue().toString();
                if (online.equals("true")) {
                    mChatLastSeenView.setText("Online");
                } else {
                    TimeAgo timeAgo = new TimeAgo();
                    long lastTime = Long.parseLong(online);
                    String lastSeenText = timeAgo.getTimeAgo(lastTime, getApplicationContext());
                    mChatLastSeenView.setText(lastSeenText);
                }
                Picasso.with(getApplicationContext()).load(thumbImage)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar)
                        .into(mProfileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatUser)) {
                    Map chatAppMap = new HashMap();
                    chatAppMap.put("seen", false);
                    chatAppMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatAppMap.put("chat/" + mCurrentUserId + "/" + mChatUser, chatAppMap);
                    chatAppMap.put("chat/" + mChatUser + "/" + mCurrentUserId, chatAppMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // --------------- SEND CHAT MESSAGE -----------------------
        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage ++;
                loadPos = 0;
                loadMoreMessages();
            }
        });
    }

    private void loadMoreMessages() {
        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.orderByKey().endAt(lastKey).limitToLast(TOTAL_ITEM_TO_LOAD);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages msg = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();
                if(!prevKey.equals(messageKey)){
                    messagesList.add(loadPos++,msg);
                } else {
                    prevKey = lastKey;
                }
                if(loadPos == 1) {
                    lastKey = messageKey;
                }

                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
                mLinearLayoutMgr.scrollToPositionWithOffset(10, 0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadMessages() {
        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEM_TO_LOAD);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages msg = dataSnapshot.getValue(Messages.class);
                loadPos++;
                if(loadPos == 1){
                    lastKey = dataSnapshot.getKey();
                    prevKey = lastKey;
                }
                messagesList.add(msg);
                mAdapter.notifyDataSetChanged();
                mMessagesList.scrollToPosition(messagesList.size()-1);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String message = mChatMessage.getText().toString();
        if (!TextUtils.isEmpty(message)) {

            String currentUserRef = "messages/" + mCurrentUserId + "/" + mChatUser;
            String chatUserRef = "messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference userMessagePush = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();
            String pushId = userMessagePush.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("timestamp", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef + "/" + pushId, messageMap);
            messageUserMap.put(chatUserRef + "/" + pushId, messageMap);

            mChatMessage.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


}

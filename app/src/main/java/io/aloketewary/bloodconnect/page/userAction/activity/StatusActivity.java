package io.aloketewary.bloodconnect.page.userAction.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.aloketewary.bloodconnect.R;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputLayout mStatus;
    private Button mSaveButton;

    private ProgressDialog mStatusProgress;

    // Firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mToolbar = (Toolbar) findViewById(R.id.status_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String statusVal = getIntent().getStringExtra("status_value");

        mStatus = (TextInputLayout) findViewById(R.id.status_text);
        mSaveButton = (Button) findViewById(R.id.status_save_button);

        mStatus.getEditText().setText(statusVal);

        mStatusProgress = new ProgressDialog(this);
        // Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = mCurrentUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUid);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Progress
                mStatusProgress = new ProgressDialog(StatusActivity.this);
                mStatusProgress.setTitle("Saving Changes");
                mStatusProgress.setMessage("Please wait while changes saved");
                mStatusProgress.show();

                String status = mStatus.getEditText().getText().toString();

                if(!TextUtils.isEmpty(status)){
                    mStatusDatabase.child("status").setValue(status)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        mStatusProgress.dismiss();
                                    } else {
                                        Toast.makeText(StatusActivity.this, "There was some error in saving changes", Toast.LENGTH_LONG).show();
                                        mStatusProgress.hide();
                                    }
                               }
                           }
                    );
                }
            }
        });
    }
}

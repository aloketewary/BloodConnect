package io.aloketewary.bloodconnect.page.userAction.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import io.aloketewary.bloodconnect.MainActivity;
import io.aloketewary.bloodconnect.R;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mLoginButton;
    private Toolbar mToolbar;
    private static final String TAG = "LoginActivity";
    private ProgressDialog mLoginProgress;

    // Firebase Auth
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "APPLCATION STARTED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginProgress = new ProgressDialog(this);
        // Toolbar Set
        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUserDatabse = FirebaseDatabase.getInstance().getReference().child("users");

        mEmail = (TextInputLayout) findViewById(R.id.login_email);
        mPassword = (TextInputLayout) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credential");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    login_user(email, password);
                }
            }
        });
    }

    private void login_user(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mLoginProgress.dismiss();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            String currentUser = mAuth.getCurrentUser().getUid();
                            mUserDatabse.child(currentUser).child("device_token").setValue(deviceToken)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "signInWithEmail:success");
                                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(mainIntent);
                                            finish();
                                        }
                                    });

                        } else {
                            mLoginProgress.hide();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}


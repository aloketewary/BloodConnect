package io.aloketewary.bloodconnect.page.userAction.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import io.aloketewary.bloodconnect.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String userId = getIntent().getStringExtra("user_id");
        mName = (TextView)findViewById(R.id.profile_display_name);
        mName.setText(userId);
    }
}

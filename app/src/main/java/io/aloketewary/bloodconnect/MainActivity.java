package io.aloketewary.bloodconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import io.aloketewary.bloodconnect.page.userAction.activity.AccountActivity;
import io.aloketewary.bloodconnect.page.userAction.activity.StartActivity;
import io.aloketewary.bloodconnect.page.userAction.activity.UsersActivity;

public class MainActivity extends AppCompatActivity {

    // Firebase Auth
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private FirebaseUser currentUser;

    private ViewPager mViewPager;
    private TabViewPagerAdapter mTabViewPageerAdapter;
    private TabLayout mTabLayout;

    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        }
        // Set content view
        setContentView(R.layout.activity_main);
        // Map Toolbar
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Blood Connect");
        super.onCreate(savedInstanceState);
        // View Pager
        mViewPager = (ViewPager) findViewById(R.id.main_tab_view);
        mTabViewPageerAdapter = new TabViewPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mTabViewPageerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
        if(currentUser == null) {
            sentToStart();
        } else {
            mUserRef.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(currentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void sentToStart() {
        startActivity(new Intent(MainActivity.this, StartActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_button){
            FirebaseAuth.getInstance().signOut();
            sentToStart();
        }
        if(item.getItemId() == R.id.main_account_settings_button){
            Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(accountIntent);
        }
        if(item.getItemId() == R.id.main_all_users_button){
            Intent usersIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(usersIntent);
        }
        return true;
    }
}

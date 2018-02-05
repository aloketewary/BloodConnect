package io.aloketewary.bloodconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

import io.aloketewary.bloodconnect.page.slide.activity.OnBoardActivity;
import io.aloketewary.bloodconnect.util.Constant;

public class WelcomeActivity extends AppCompatActivity {

    LinearLayout line1,line2;
    //Button buttonSub;
    Animation upToDown, downToUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        int timeout = 3000;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        Timer timer = new Timer();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        // On start this welcome page
        setContentView(R.layout.activity_welcome);

        // buttonSub = findViewById(R.id.buttonsub);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        upToDown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downToUp = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        line1.setAnimation(upToDown);
        line2.setAnimation(downToUp);


        if(sharedPreferences.getBoolean(Constant.COMPLETED_ONBOARDING_PREF_NAME, false)) {
            // after certain time it will hide
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent homepage = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(homepage);
                    finish();
                }
            }, timeout);
        } else {
            // after certain time it will hide
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(WelcomeActivity.this, OnBoardActivity.class));
                    finish();
                }
            }, timeout);
        }
    }
}

package io.aloketewary.bloodconnect;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

import io.aloketewary.bloodconnect.page.slide.activity.OnBoardActivity;

public class WelcomeActivity extends AppCompatActivity {

    LinearLayout line1,line2;
    Button buttonsub;
    Animation uptodown, downtoup;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        int timeout = 2000;
        super.onCreate(savedInstanceState);
        Timer timer = new Timer();
        // On start this welcome page
        setContentView(R.layout.activity_welcome);
        buttonsub = findViewById(R.id.buttonsub);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        line1.setAnimation(uptodown);
        line2.setAnimation(downtoup);

        // after certain time it will hide
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
                Intent homepage = new Intent(WelcomeActivity.this, OnBoardActivity.class);
                startActivity(homepage);
            }
        }, timeout);
    }
}

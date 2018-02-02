package io.aloketewary.bloodconnect.page.slide.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.aloketewary.bloodconnect.MainActivity;
import io.aloketewary.bloodconnect.R;

public class OnBoardActivity extends AppCompatActivity {

    private ViewPager slideViewPager;
    private LinearLayout dotLayout;
    private SliderAdapter sliderAdapter;
    private Button nextButton;
    private Button prevButton;
    private Button finishBtn;

    private TextView[] sliderDots;
    private int sliderCurrentPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);
        slideViewPager = findViewById(R.id.slideViewPager);
        dotLayout = findViewById(R.id.dotLine);
        nextButton = findViewById(R.id.nextBtn);
        prevButton = findViewById(R.id.prevBtn);
        finishBtn = findViewById(R.id.finishBtn);

        sliderAdapter = new SliderAdapter(this);
        slideViewPager.setAdapter(sliderAdapter);

        addDotIndicator(0);

        slideViewPager.addOnPageChangeListener(viewListner);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideViewPager.setCurrentItem(sliderCurrentPage + 1);
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                slideViewPager.setCurrentItem(sliderCurrentPage - 1);
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Start MainActivity.class
                Intent myIntent = new Intent(OnBoardActivity.this, MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }

    public void addDotIndicator(int stat) {
        sliderDots = new TextView[3];
        dotLayout.removeAllViews();

        for (int i = 0 ; i < sliderDots.length ; i++){
            sliderDots[i] = new TextView(this);
            sliderDots[i].setText(Html.fromHtml("&#8226;"));
            sliderDots[i].setTextSize(35);
            sliderDots[i].setTextColor(getResources().getColor(R.color.transparentWhite));
            sliderDots[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            dotLayout.addView(sliderDots[i]);
        }

        if(sliderDots.length > 0){
           sliderDots[stat].setTextColor(getResources().getColor(R.color.whiteText));
        }
    }

    ViewPager.OnPageChangeListener viewListner = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotIndicator(position);
            sliderCurrentPage = position;

            if(position == 0){
                nextButton.setEnabled(true);
                prevButton.setEnabled(false);
                finishBtn.setEnabled(false);
                finishBtn.setText("");
                finishBtn.setVisibility(View.INVISIBLE);
                prevButton.setVisibility(View.INVISIBLE);
                nextButton.setText("Next");
                prevButton.setText("");
            } else if (position == sliderDots.length - 1) {
                finishBtn.setEnabled(true);
                prevButton.setEnabled(true);
                prevButton.setVisibility(View.VISIBLE);
                finishBtn.setVisibility(View.VISIBLE);
                finishBtn.setText("Finish");
                prevButton.setText("Prev");
                nextButton.setEnabled(false);
                nextButton.setText("");
                nextButton.setVisibility(View.INVISIBLE);
            } else {
                nextButton.setEnabled(true);
                prevButton.setEnabled(true);
                finishBtn.setEnabled(false);
                finishBtn.setText("");
                finishBtn.setVisibility(View.INVISIBLE);
                prevButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                nextButton.setText("Next");
                prevButton.setText("Prev");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}

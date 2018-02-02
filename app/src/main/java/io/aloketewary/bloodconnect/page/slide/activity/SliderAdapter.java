package io.aloketewary.bloodconnect.page.slide.activity;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.aloketewary.bloodconnect.R;

/**
 * Created by AlokeT on 2/1/2018.
 */

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public int[] slideImages = {
        R.drawable.food_icon,
            R.drawable.sleep_icon,
            R.drawable.code_icon
    };

    public String[] slideHeaders = {
            "EAT",
            "SLEEP",
            "CODE"
    };

    public String[] slideDesc = {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis eu cursus magna, non elementum mauris. Orci varius natoque penatibus et magnis dis parturient montes",
            "Proin euismod quam eros, et volutpat ex accumsan et. Quisque at ante fermentum, ultricies est sit amet, mollis ante. Nullam ornare venenatis metus",
            "Nulla eleifend est lacinia odio dapibus tempor. Vestibulum aliquam nec ex eget aliquet. Donec in faucibus mi. Nunc sed cursus risus. Sed tincidunt purus purus"
    };

    public SliderAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return slideHeaders.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout, container, false);

        ImageView slideImageView = view.findViewById(R.id.slide_images);
        TextView slideHeaderText = view.findViewById(R.id.slide_header);
        TextView slideDescText = view.findViewById(R.id.slide_desc);

        slideImageView.setImageResource(slideImages[position]);
        slideHeaderText.setText(slideHeaders[position]);
        slideDescText.setText(slideDesc[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}

package com.pbicv.ddpx;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements Button.OnClickListener {

    private TextView textView;
    private LottieAnimationView lottieAnimationView;
    Timer timer = new Timer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lottieAnimationView=findViewById(R.id.alottie);
        // 是否循环播放
        lottieAnimationView.loop(true);
        // 设置播放速率，例如：2代表播放速率是不设置时的二倍
        lottieAnimationView.setSpeed(1f);
        // 开始播放
        lottieAnimationView.playAnimation();

        textView=findViewById(R.id.marquee_text);
        //启用走马灯效果
        textView.setSelected(true);
        //获取焦点
        textView.requestFocus();
        textView.setFocusableInTouchMode(true);
        textView.setFocusable(true);

        ViewPager viewPager = findViewById(R.id.viewPager);
        List<Integer> images = Arrays.asList(R.drawable.img2, R.drawable.img3, R.drawable.img1,R.drawable.img4);
        CarouselPagerAdapter adapter = new CarouselPagerAdapter(this, images);
        viewPager.setAdapter(adapter);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int currentPosition = viewPager.getCurrentItem();
                        int totalImages = adapter.getCount();
                        int nextPosition = (currentPosition + 1) % totalImages;
                        viewPager.setCurrentItem(nextPosition);
                    }
                });
            }
        }, 0, 3000); // 设置滑动间隔时间，单位为毫秒
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btnGame){
            startGame();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        timer.purge();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    public void startGame(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        finish();
    }
}
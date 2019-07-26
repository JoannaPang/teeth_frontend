package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

public class WelcomeActivity extends Activity implements View.OnTouchListener{
    private static String TestLog = "TestLog";
    // zsl splash需要的变量
    private ViewFlipper viewFlipper;
    private int viewIds[] = {R.layout.splash_item_0, R.layout.splash_item_1, R.layout.splash_item_2, R.layout.splash_item_3};
    private float startX; //手指按下时的x坐标
    private float endX; //手指抬起时的x坐标
    private float moveX = 100f; //判断是否切换页面的标准值
    private GestureDetector gestureDetector; //创建手势监听器

    // pang
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_dc);
        // zsl splash
        initViewFlipper();

        Button getInButton = (Button) findViewById(R.id.yindaoye_button);
        getInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TestLog, "Welcome Activity");
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this, LoginActivity.class);
                //intent.setClass(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // zsl 初始化ViewFlipper 监听手势
    private void initViewFlipper() {
        viewFlipper = (ViewFlipper) findViewById(R.id.splash_viewFlipper);
        viewFlipper.setOnTouchListener(this);
        gestureDetector = new GestureDetector(this, new MyGestureListener());
    }
    // zsl 动态加载界面
    private void addViews() {
        View itemView;
        for (int viewId : viewIds) {
            itemView = View.inflate(this, viewId, null);
            viewFlipper.addView(itemView);
        }
    }
    // zsl 手势动作识别
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e2.getX() - e1.getX() > moveX) {
                viewFlipper.setInAnimation(WelcomeActivity.this, R.anim.splash_left_in);
                viewFlipper.setOutAnimation(WelcomeActivity.this, R.anim.splash_right_out);
                viewFlipper.showPrevious();
            } else if (e2.getX() - e1.getX() < moveX) {
                viewFlipper.setInAnimation(WelcomeActivity.this, R.anim.splash_right_in);
                viewFlipper.setOutAnimation(WelcomeActivity.this, R.anim.splash_left_out);
                viewFlipper.showNext();
            }
            return true;
        }
    }
    // zsl 监听触摸屏幕
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }
}

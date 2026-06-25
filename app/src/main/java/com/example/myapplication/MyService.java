package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Service;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MyService extends Service {

//    Tutorial: https://www.youtube.com/watch?v=xLN89H_QP5s

    private WindowManager windowManager;
    private View floatingBubble;
    private View removeView;
    private WindowManager.LayoutParams params;

    @SuppressLint({"ClickableViewAccesibility", "InflateParams"})
    @RequiresApi(api = Build.VERSION_CODES.0)
    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        floatingBubble = LayoutInflater.from(this).inflate(R.layout.floating_view, null);
        ConstraintLayout container = floatingBubble.findViewById(R.id.container);
        Button dodajNoweZamowienieBtn = floatingBubble.findViewById(R.id.addNoweZamowienieBtn);

        removeView = LayoutInflater.from(this).inflate(R.layout.remove_layout, null);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 100;
        params.y = 100;

        windowManager.addView(floatingBubble, params);

        WindowManager.LayoutParams removeParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                200,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        removeParams.gravity = Gravity.BOTTOM;
        windowManager.addView(removeView, removeParams);
        removeView.setVisibility(View.GONE);

        dodajNoweZamowienieBtn.setOnClickListener(v -> stopSelf());
        container.setOnTouchListener(new BubbleTouchListener());
    }

    private class BubbleTouchListener implements View.OnTouchListener {
        private int initialX, initialY;
        private float initialTouchX, initialTouchY;
        private boolean isLongPress = false;
        private final Handler longPressHandler = new Handler();

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();

                    isLongPress = false;
                    longPressHandler.postDelayed(() -> {
                        isLongPress = true;
                        showRemoveZone();
                    }, 500);

                case MotionEvent.ACTION_MOVE:
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(floatingBubble, params);
            }
        }
    }

}

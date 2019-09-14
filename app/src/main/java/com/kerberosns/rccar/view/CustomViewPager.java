package com.kerberosns.rccar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {
    private boolean enabled;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        enabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (enabled) {
            return super.onTouchEvent(event);
        } else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (enabled) {
            return super.onInterceptTouchEvent(event);
        } else {
            return false;
        }
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }
}

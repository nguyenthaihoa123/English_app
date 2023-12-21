package com.example.englishapp.viewmodel;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

public class SwipeTouchListener implements RecyclerView.OnItemTouchListener {

    private GestureDetector gestureDetector;
    private OnSwipeListener onSwipeListener;

    public SwipeTouchListener(Context context, OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && onSwipeListener != null) {
            gestureDetector.onTouchEvent(e);
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 50;
        private static final int SWIPE_VELOCITY_THRESHOLD = 50;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY)
                    && Math.abs(distanceX) > SWIPE_THRESHOLD
                    && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0) {
                    onSwipeListener.onSwipeRight();
                } else {
                    onSwipeListener.onSwipeLeft();
                }
                return true;
            }
            return false;
        }
    }

    public interface OnSwipeListener {
        void onSwipeLeft();

        void onSwipeRight();
    }
}
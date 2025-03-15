package com.example.samurai.jugador;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private final SamuraiController samuraiController;

    public GestureListener(SamuraiController samuraiController) {
        this.samuraiController = samuraiController;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 == null || e2 == null) return false;

        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        Log.d("GestureListener", "onFling detected: diffX=" + diffX + ", diffY=" + diffY);

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    samuraiController.performDash(true);  // Dash a la derecha
                    Log.d("GestureListener", "DASH RIGHT");
                } else {
                    samuraiController.performDash(false);  // Dash a la izquierda
                    Log.d("GestureListener", "DASH LEFT");
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;  // Permite que otros gestos también sean detectados
    }
}

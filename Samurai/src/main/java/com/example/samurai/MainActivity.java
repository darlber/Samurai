package com.example.samurai;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {
    private GameFragment gameFragment;
    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFullScreen();
        loadMenuFragment();
        MusicManager.play(this, R.raw.music);
        SoundManager.init(this);
    }

    private void setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    private void loadMenuFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame, new MenuFragment()).commit();
    }
    protected void onStart() {
        super.onStart();
        if (gameFragment != null) {
            gameFragment.togglePause();
        }
        // Reanudar la música
        MusicManager.resume();
    }
    @Override
    protected void onResume() {
        super.onResume();
        setFullScreen();
    }
    @Override
    protected void onStop() {
        super.onStop();
        MusicManager.pause();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicManager.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pausar la música
        MusicManager.pause();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        // Reanudar el juego si es necesario
        if (gameFragment != null) {
            gameFragment.togglePause();
        }
    }
}
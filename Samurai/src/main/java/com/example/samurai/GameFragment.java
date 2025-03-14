package com.example.samurai;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.samurai.enemigos.Enemy;
import com.example.samurai.enemigos.EnemyManager;
import com.example.samurai.jugador.Samurai;
import com.example.samurai.jugador.SamuraiController;

import java.util.ArrayList;
import java.util.List;

public class GameFragment extends Fragment {
    private SamuraiController samuraiController;
    private EnemyManager enemyManager;
    private ScoreManager scoreManager;
    private ImageView samuraiAnimation;
    private List<Enemy> enemies = new ArrayList<>();
    private boolean isGameRunning = true;
    private boolean isGamePaused = false;
    private View innerCircle, outerCircle;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_layout, container, false);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        samuraiAnimation = rootView.findViewById(R.id.samuraiAnimation);
        outerCircle = rootView.findViewById(R.id.outerCircle);
        innerCircle = rootView.findViewById(R.id.innerCircle);
        Button btnAttack = rootView.findViewById(R.id.btnAttack);
        ImageView pauseButton = rootView.findViewById(R.id.pauseButton);
        Button btnSpecialAttack = rootView.findViewById(R.id.btnSpecialAttack);
        TextView scoreTextView = rootView.findViewById(R.id.scoreTextView);
        TextView healthTextView = rootView.findViewById(R.id.healthTextView);

        scoreManager = new ScoreManager(requireContext(), scoreTextView);
        enemyManager = new EnemyManager(requireContext(), screenWidth, screenHeight);
        samuraiController = new SamuraiController(requireContext(), samuraiAnimation, enemyManager, screenWidth, screenHeight);

        enemyManager.spawnEnemies();
        samuraiController.setupControls(outerCircle, innerCircle, btnAttack, btnSpecialAttack, rootView);
        handleBackButton();
        pauseButton.setOnClickListener(v -> togglePause());

        enemies = enemyManager.getEnemies();
        Samurai samurai = samuraiController.getSamurai();
        CustomView customView = new CustomView(requireContext(), screenWidth, screenHeight, enemies, enemyManager, samurai, samuraiController, scoreManager, this);
        ((ViewGroup) rootView).addView(customView);

        updateHealthTextView(healthTextView, samurai);

        return rootView;
    }

    private void updateHealthTextView(TextView healthTextView, Samurai samurai) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                healthTextView.setText(getString(R.string.health_text, samurai.getHealth()));
                if (!samurai.isDead()) {
                    new Handler(Looper.getMainLooper()).postDelayed(this, 100);
                }
            }
        }, 100);
    }

    public void endGame() {
        isGameRunning = false;
        if (scoreManager != null) {
            scoreManager.saveHighScore();
        }
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame, new MenuFragment())
                .addToBackStack(null)
                .commit();
    }

    private void handleBackButton() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                endGame();
            }
        });
    }

    private void togglePause() {
        isGamePaused = !isGamePaused;
        if (samuraiController != null) {
            samuraiController.setGamePaused(isGamePaused);
        }
        if (enemyManager != null) {
            enemyManager.setGamePaused(isGamePaused);
        }
        if (isGamePaused) {
            showPauseMenu();
        }
    }

    private void showPauseMenu() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Juego en pausa")
                .setMessage("¿Qué deseas hacer?")
                .setPositiveButton("Reanudar", (dialog, which) -> togglePause())
                .setNegativeButton("Menú principal", (dialog, which) -> endGame())
                .setCancelable(false)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (scoreManager != null) {
            scoreManager.saveHighScore();
        }
    }
}
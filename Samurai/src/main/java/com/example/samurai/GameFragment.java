package com.example.samurai;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

import java.util.List;

public class GameFragment extends Fragment {
    private SamuraiController samuraiController;
    private EnemyManager enemyManager;
    private ScoreManager scoreManager;
    private boolean isGamePaused = false;
    private View innerCircle, outerCircle;
    private ImageView health1, health2, health3, health4, health5;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_layout, container, false);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        ImageView samuraiAnimation = rootView.findViewById(R.id.samuraiAnimation);
        outerCircle = rootView.findViewById(R.id.outerCircle);
        innerCircle = rootView.findViewById(R.id.innerCircle);
        Button btnAttack = rootView.findViewById(R.id.btnAttack);
        ImageView pauseButton = rootView.findViewById(R.id.pauseButton);
        Button btnSpecialAttack = rootView.findViewById(R.id.btnSpecialAttack);
        TextView scoreTextView = rootView.findViewById(R.id.scoreTextView);

        health1 = rootView.findViewById(R.id.health1);
        health2 = rootView.findViewById(R.id.health2);
        health3 = rootView.findViewById(R.id.health3);
        health4 = rootView.findViewById(R.id.health4);
        health5 = rootView.findViewById(R.id.health5);

        scoreManager = new ScoreManager(requireContext(), scoreTextView);
        enemyManager = new EnemyManager(requireContext(), screenWidth, screenHeight);
        samuraiController = new SamuraiController(requireContext(), samuraiAnimation, enemyManager, screenWidth, screenHeight,(ViewGroup) rootView);

        enemyManager.spawnEnemy();
        outerCircle.post(() -> {
            outerCircle.bringToFront();
            innerCircle.bringToFront();
        });

        samuraiController.setupControls(outerCircle, innerCircle, btnAttack, btnSpecialAttack, rootView);
        handleBackButton();
        pauseButton.setOnClickListener(v -> togglePause());

        List<Enemy> enemies = enemyManager.getEnemies();
        Samurai samurai = samuraiController.getSamurai();

        CustomView customView = new CustomView(requireContext(), screenWidth, enemies, enemyManager, samurai, samuraiController, scoreManager, this);
        ((ViewGroup) rootView).addView(customView, 0);

        updateHealthTextView(samurai);

        return rootView;
    }

    public void updateHealthTextView(Samurai samurai) {
        int health = samurai.getHealth();

        health1.setVisibility(health >= 1 ? View.VISIBLE : View.GONE);
        health2.setVisibility(health >= 2 ? View.VISIBLE : View.GONE);
        health3.setVisibility(health >= 3 ? View.VISIBLE : View.GONE);
        health4.setVisibility(health >= 4 ? View.VISIBLE : View.GONE);
        health5.setVisibility(health >= 5 ? View.VISIBLE : View.GONE);
    }


    public void endGame() {
        if (scoreManager != null) {
            scoreManager.saveHighScore();
        }
    }

    private void handleBackButton() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                togglePause();
            }
        });
    }

    void togglePause() {
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
            .setNegativeButton("Menú principal", (dialog, which) -> {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frame, new MenuFragment())
                        .commit();
            })
            .setCancelable(false)
            .show();
}

    public void showGameOverDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.game_over, null);
        int score = scoreManager.getScore();
        int enemiesDefeated = samuraiController.getEnemiesDefeated();

        dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);
        Button btnMainMenu = dialogView.findViewById(R.id.btn_main_menu);
        Button btnRetry = dialogView.findViewById(R.id.btn_retry);

        message.setText(getString(R.string.game_over_message, score, enemiesDefeated));

        // Reproducir música de Game Over
        MusicManager.release();
        MusicManager.play(requireContext(), R.raw.game_over);
        // Ocultar el samurái
        View samuraiView = requireView().findViewById(R.id.samuraiAnimation);
        if (samuraiView != null) {
            samuraiView.setVisibility(View.GONE);
        }
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        View.OnClickListener buttonClickListener = v -> {
            // Reanudar música principal
            MusicManager.release();
            MusicManager.play(requireContext(), R.raw.music);
            alertDialog.dismiss();
        };

        btnMainMenu.setOnClickListener(v -> {
            buttonClickListener.onClick(v);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frame, new MenuFragment())
                    .commit();
        });

        btnRetry.setOnClickListener(v -> {
            buttonClickListener.onClick(v);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frame, new GameFragment())
                    .commit();
        });

        alertDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (scoreManager != null) {
            scoreManager.saveHighScore();
        }
    }
}
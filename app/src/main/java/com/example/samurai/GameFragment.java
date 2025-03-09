package com.example.samurai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private TextView scoreTextView; // TextView para mostrar los puntos

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_layout, container, false);
        // Obtener dimensiones de la pantalla
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        samuraiAnimation = rootView.findViewById(R.id.samuraiAnimation);
        scoreTextView = rootView.findViewById(R.id.scoreTextView);
        // Configurar botones
        ImageButton btnLeft = rootView.findViewById(R.id.btnLeft);
        ImageButton btnRight = rootView.findViewById(R.id.btnRight);
        Button btnAttack = rootView.findViewById(R.id.btnAttack);

        scoreManager = new ScoreManager(requireContext(), scoreTextView);
        enemyManager = new EnemyManager(requireContext(), screenWidth, screenHeight);

        // Create and initialize SamuraiController
        samuraiController = new SamuraiController(
                requireContext(),
                samuraiAnimation,
                enemyManager,
                screenWidth,
                screenHeight
        );
        samuraiController.setupControls(btnLeft, btnRight, btnAttack);

        // Crear y agregar CustomView para dibujar enemigos
        enemies = enemyManager.getEnemies();
        Samurai samurai = samuraiController.getSamurai();
        CustomView customView = new CustomView(requireContext(), screenWidth, screenHeight, enemies, enemyManager, samurai, scoreManager);
        ((ViewGroup) rootView).addView(customView);

        enemyManager.spawnEnemies();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        boolean isRunning = false; // Detener el hilo cuando el Fragment se destruya
    }

}
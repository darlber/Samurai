package com.example.samurai;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_layout, container, false);
        // Obtener dimensiones de la pantalla
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        samuraiAnimation = rootView.findViewById(R.id.samuraiAnimation);
        // TextView para mostrar los puntos
        rootView.findViewById(R.id.scoreTextView);
        // Configurar botones
        ImageButton btnLeft = rootView.findViewById(R.id.btnLeft);
        ImageButton btnRight = rootView.findViewById(R.id.btnRight);
        Button btnAttack = rootView.findViewById(R.id.btnAttack);
        ImageView pauseButton = rootView.findViewById(R.id.pauseButton);
        Button btnSpecialAttack = rootView.findViewById(R.id.btnSpecialAttack);
        // Obtener el TextView del puntaje
        TextView scoreTextView = rootView.findViewById(R.id.scoreTextView);
        TextView healthTextView = rootView.findViewById(R.id.healthTextView); // TextView para la vida
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
        samuraiController.setupControls(btnLeft, btnRight, btnAttack, btnSpecialAttack, rootView);
        handleBackButton();
        pauseButton.setOnClickListener(v -> togglePause());
        // Crear y agregar CustomView para dibujar enemigos
        enemies = enemyManager.getEnemies();
        Samurai samurai = samuraiController.getSamurai();
        CustomView customView = new CustomView(requireContext(), screenWidth, screenHeight, enemies, enemyManager, samurai,samuraiController, scoreManager, this);
        ((ViewGroup) rootView).addView(customView);

        enemyManager.spawnEnemies();
        // Actualizar la vida del samurái en la interfaz de usuario
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                healthTextView.setText("Vida: " + samurai.getHealth());
                if (!samurai.isDead()) {
                    new Handler(Looper.getMainLooper()).postDelayed(this, 100); // Actualizar cada 100ms
                }
            }
        }, 100);


        return rootView;
    }

    // Método para terminar el juego
    public void endGame() {
        isGameRunning = false; // Detener el bucle del juego
        if (scoreManager != null) {
            scoreManager.saveHighScore(); // Guardar el high score
        }

        // Cambiar al menú principal
        MenuFragment menuFragment = new MenuFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame, menuFragment)
                .addToBackStack(null)
                .commit();
    }

    private void handleBackButton() {
        // Registrar un callback para manejar el botón Atrás
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                endGame(); // Terminar el juego y guardar el high score
            }
        });
    }

    // Método para pausar o reanudar el juego
    private void togglePause() {
        isGamePaused = !isGamePaused; // Cambiar el estado de pausa

        if (samuraiController != null) {
            samuraiController.setGamePaused(isGamePaused);
        }

        if (enemyManager != null) {
            enemyManager.setGamePaused(isGamePaused);
        }

        if (isGamePaused) {
            showPauseMenu(); // Mostrar el menú de pausa
        }
    }

    private void showPauseMenu() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Juego en pausa")
                .setMessage("¿Qué deseas hacer?")
                .setPositiveButton("Reanudar", (dialog, which) -> togglePause())
                .setNegativeButton("Menú principal", (dialog, which) -> endGame())
                .setCancelable(false) // Evitar que el usuario cierre el diálogo sin seleccionar una opción
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Guardar el high score cuando el juego termine
        if (scoreManager != null) {
            scoreManager.saveHighScore();
        }
    }

}
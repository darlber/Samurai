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
import java.util.Random;

public class GameFragment extends Fragment {
    private Samurai samurai;
    private SamuraiController samuraiController;
    private EnemyManager enemyManager;
    private ScoreManager scoreManager;

    private ImageView samuraiAnimation;
    private boolean isRunning = true;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean isAttacking = false;

    private List<Enemy> enemies = new ArrayList<>();
    private Handler enemyHandler = new Handler();
    private Random random = new Random();


    private int score = 0; // Contador de puntos
    private TextView scoreTextView; // TextView para mostrar los puntos

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_layout, container, false);
        samuraiAnimation = rootView.findViewById(R.id.samuraiAnimation);

        // Obtener el TextView para mostrar los puntos
        scoreTextView = rootView.findViewById(R.id.scoreTextView);
        scoreManager = new ScoreManager(requireContext(), scoreTextView);

        // Obtener dimensiones de la pantalla
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Crear el objeto Samurai con las dimensiones de la pantalla
        samurai = new Samurai(requireContext(), screenWidth, screenHeight);

        // Configurar la posición inicial del samurái en la mitad inferior de la pantalla
        samuraiAnimation.setX(samurai.getX()); // Posición X centrada
        samuraiAnimation.setY(samurai.getY()); // Posición Y en la parte inferior

        // Configurar botones
        ImageButton btnLeft = rootView.findViewById(R.id.btnLeft);
        ImageButton btnRight = rootView.findViewById(R.id.btnRight);
        Button btnAttack = rootView.findViewById(R.id.btnAttack);

        btnAttack.setOnClickListener(v -> {
            if (!isAttacking) {
                isAttacking = true;

                // Cambiar la animación del samurái a "ataque"
                setSamuraiAnimation(samurai.getAttackAnimation());

                // Determinar la dirección del ataque
                boolean facingRight = samuraiAnimation.getScaleX() > 0;

                // Ajustar el hitbox del ataque
                int attackWidth = samurai.getWidth() / 3; // 🔥 Misma distancia en ambas direcciones
                int attackX = facingRight
                        ? (int) samuraiAnimation.getX() + samurai.getWidth() / 2  // Derecha
                        : (int) samuraiAnimation.getX() + samurai.getWidth() / 2 - attackWidth; // Izquierda

                int attackY = (int) samuraiAnimation.getY();
                int attackHeight = samurai.getHeight();

                // Verificar colisión con enemigos en la dirección correcta
                for (int i = 0; i < enemies.size(); i++) {
                    Enemy enemy = enemies.get(i);

                    // 🔥 Filtrar enemigos en la dirección correcta
                    if ((facingRight && enemy.getX() > samuraiAnimation.getX()) ||
                            (!facingRight && enemy.getX() < samuraiAnimation.getX())) {

                        if (enemy.checkCollision(attackX, attackY, attackWidth, attackHeight)) {
                            enemy.takeDamage();

                        }
                    }
                }

                // Calcular duración de la animación de ataque
                int totalDuration = 0;
                AnimationDrawable attackAnimation = samurai.getAttackAnimation();
                for (int i = 0; i < attackAnimation.getNumberOfFrames(); i++) {
                    totalDuration += attackAnimation.getDuration(i);
                }

                // Volver a la animación correcta después del ataque
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (movingLeft || movingRight) {
                        setSamuraiAnimation(samurai.getRunAnimation());
                    } else {
                        setSamuraiAnimation(samurai.getIdleAnimation());
                    }
                    isAttacking = false;
                }, totalDuration);
            }
        });


        btnLeft.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    movingLeft = true;
                    samuraiAnimation.setScaleX(-1.5f); // Voltear a la izquierda
                    setSamuraiAnimation(samurai.getRunAnimation()); // Cambiar a animación de caminar
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    movingLeft = false;
                    setSamuraiAnimation(samurai.getIdleAnimation()); // Cambiar a animación idle
                    v.performClick();
                    break;
            }
            return true;
        });

        btnRight.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    movingRight = true;
                    samuraiAnimation.setScaleX(1.5f); // Voltear a la derecha
                    setSamuraiAnimation(samurai.getRunAnimation()); // Cambiar a animación de caminar
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    movingRight = false;
                    setSamuraiAnimation(samurai.getIdleAnimation()); // Cambiar a animación idle
                    v.performClick();
                    break;
            }
            return true;
        });

        // Iniciar animación y movimiento del samurái
        startSamuraiAnimation();
        moveSamurai();

        // Crear y agregar CustomView para dibujar enemigos
        CustomView customView = new CustomView(requireContext(), screenWidth, screenHeight);
        ((ViewGroup) rootView).addView(customView);

        // Generar enemigos
        spawnEnemies();
        return rootView;
    }

    private void startSamuraiAnimation() {
        // Cargar la animación desde el archivo XML
        samuraiAnimation.setBackgroundResource(R.drawable.idle_anim);
        // Obtener la animación como AnimationDrawable
        AnimationDrawable animation = (AnimationDrawable) samuraiAnimation.getBackground();
        // Mostrar el ImageView y comenzar la animación
        samuraiAnimation.setVisibility(View.VISIBLE);
        animation.start();
    }

    private void setSamuraiAnimation(AnimationDrawable animation) {
        samuraiAnimation.setBackground(animation);
        animation.stop();
        animation.start();
    }

    private void moveSamurai() {
        new Thread(() -> {
            while (isRunning) {
                samuraiAnimation.post(() -> {
                    if (movingLeft) {
                        samuraiAnimation.setX(samuraiAnimation.getX() - 5);
                        if (samuraiAnimation.getX() + samuraiAnimation.getWidth() < 0) {
                            samuraiAnimation.setX(getResources().getDisplayMetrics().widthPixels);
                        }
                    } else if (movingRight) {
                        samuraiAnimation.setX(samuraiAnimation.getX() + 5);
                        if (samuraiAnimation.getX() > getResources().getDisplayMetrics().widthPixels) {
                            samuraiAnimation.setX(-samuraiAnimation.getWidth());
                        }
                    }
                });

                try {
                    Thread.sleep(16); // ~60 FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void spawnEnemies() {
        enemyHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;

                    // Crear diferentes tipos de enemigos
                    int enemyType = random.nextInt(3); // Aleatorio entre 0 y 2
                    boolean spawnFromLeft = random.nextBoolean(); // Aparecer por la izquierda o derecha

                    Enemy enemy;
                    switch (enemyType) {
                        case 1:
                            enemy = new Enemy(requireContext(), screenWidth, screenHeight, R.drawable.flying_eye_run,
                                    R.drawable.flying_eye_attack, R.drawable.flying_eye_hit, R.drawable.flying_eye_death,
                                    3, spawnFromLeft, 1, "flying_eye");
                            break;
                        case 2:
                            enemy = new Enemy(requireContext(), screenWidth, screenHeight, R.drawable.mushroom_run,
                                    R.drawable.mushroom_attack, R.drawable.mushroom_hit, R.drawable.mushroom_death,
                                    1, spawnFromLeft, 5, "mushroom");
                            break;
                        default:
                            enemy = new Enemy(requireContext(), screenWidth, screenHeight, R.drawable.goblin_run,
                                    R.drawable.goblin_attack, R.drawable.goblin_hit, R.drawable.goblin_death,
                                    2, spawnFromLeft, 3, "goblin");
                            break;
                    }

                    enemies.add(enemy);

                    // Repetir la generación de enemigos
                    enemyHandler.postDelayed(this, random.nextInt(2000) + 1000); // Generar enemigos cada 1-3 segundos
                }
            }
        }, 1000); // Comenzar a generar enemigos después de 1 segundo
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isRunning = false; // Detener el hilo cuando el Fragment se destruya
    }

    private class CustomView extends View {
        private int screenWidth, screenHeight;

        public CustomView(Context context, int screenWidth, int screenHeight) {
            super(context);
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Obtener la posición del samurái
            int samuraiX = (int) samuraiAnimation.getX();

            // Dibujar todos los enemigos
            for (int i = 0; i < enemies.size(); i++) {
                Enemy enemy = enemies.get(i);

                // No actualizar ni dibujar enemigos marcados para eliminación
                if (enemy.isMarkedForRemoval()) {
                    // Aumentar los puntos según el tipo de enemigo
                    switch (enemy.getType()) {
                        case "flying_eye":
                            scoreManager.addScore(10);
                            break;
                        case "goblin":
                            scoreManager.addScore(30);
                            break;
                        case "mushroom":
                            scoreManager.addScore(50);
                            break;
                    }
                    enemies.remove(i); // Eliminar el enemigo de la lista
                    i--; // Ajustar el índice después de eliminar un enemigo
                    continue;
                }

                enemy.update(samuraiX, samurai.getWidth()); // Actualizar la posición del enemigo
                enemy.draw(canvas); // Dibujar el enemigo

                // Eliminar enemigos que salen de la pantalla
                if (enemy.getX() + enemy.getWidth() < 0 || enemy.getX() > screenWidth) {
                    enemies.remove(i);
                    i--; // Ajustar el índice después de eliminar un enemigo
                }
            }

            // Volver a dibujar la vista
            invalidate();
        }
    }
}
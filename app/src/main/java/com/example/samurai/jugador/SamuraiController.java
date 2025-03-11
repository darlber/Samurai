package com.example.samurai.jugador;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.samurai.R;
import com.example.samurai.enemigos.Enemy;
import com.example.samurai.enemigos.EnemyManager;

import java.util.List;

public class SamuraiController {
    private Samurai samurai;
    private ImageView samuraiAnimation;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean isAttacking = false;
    private EnemyManager enemyManager;
    private boolean isGamePaused = false;
    private Context context;
    private int enemiesDefeated = 0;
    private Button btnSpecialAttack;
    private boolean canAttack = true;
    private static final int ATTACK_COOLDOWN = 150; // 150 ms
    private GestureDetector gestureDetector;


    public SamuraiController(Context context, ImageView samuraiAnimation, EnemyManager enemyManager, int screenWidth, int screenHeight) {
        this.context = context;
        this.samuraiAnimation = samuraiAnimation;
        this.enemyManager = enemyManager;

        this.samurai = new Samurai(context, screenWidth, screenHeight);
        this.samuraiAnimation.setX(samurai.getX());
        this.samuraiAnimation.setY(samurai.getY());
        gestureDetector = new GestureDetector(context, new GestureListener(this));
        new Handler(Looper.getMainLooper());
    }

    public void setupControls(ImageButton btnLeft, ImageButton btnRight, Button btnAttack, Button btnSpecialAttack, View rootView) {
        this.btnSpecialAttack = btnSpecialAttack;// Asignar el botón a la variable de instancia
        btnLeft.setOnTouchListener(this::handleLeftMovement);
        btnRight.setOnTouchListener(this::handleRightMovement);
        btnAttack.setOnClickListener(v -> handleAttack());
        btnSpecialAttack.setOnClickListener(v -> handleSpecialAttack());
        // Detectar gestos en toda la pantalla
        rootView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));


        moveSamurai();
        startSamuraiAnimation();
        btnSpecialAttack.setEnabled(false); // Deshabilitar el botón al inicio
        updateSpecialAttackButtonText(); // Inicializar el texto del botón
    }

    private boolean handleLeftMovement(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            movingLeft = true;
            samuraiAnimation.setScaleX(-1.5f);
            if (!isAttacking) { // Solo cambiar animación si no está atacando
                setSamuraiAnimation(samurai.getRunAnimation());
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            movingLeft = false;
            if (!isAttacking) { // Solo cambiar animación si no está atacando
                setSamuraiAnimation(samurai.getIdleAnimation());
            }
            v.performClick();
        }
        return true;
    }

    private boolean handleRightMovement(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            movingRight = true;
            samuraiAnimation.setScaleX(1.5f);
            if (!isAttacking) { // Solo cambiar animación si no está atacando
                setSamuraiAnimation(samurai.getRunAnimation());
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            movingRight = false;
            if (!isAttacking) { // Solo cambiar animación si no está atacando
                setSamuraiAnimation(samurai.getIdleAnimation());
            }
            v.performClick();
        }
        return true;
    }


    private void handleAttack() {
        if (isAttacking) {
            return; // Salir si ya se está realizando un ataque
        }
        isAttacking = true;
        setSamuraiAnimation(samurai.getAttackAnimation());
        boolean facingRight = samuraiAnimation.getScaleX() > 0;
        int attackWidth = samurai.getWidth() / 3;
        int attackX = facingRight ? (int) samuraiAnimation.getX() + samurai.getWidth() / 2 : (int) samuraiAnimation.getX() + samurai.getWidth() / 2 - attackWidth;
        int attackY = (int) samuraiAnimation.getY();
        int attackHeight = samurai.getHeight();
        checkCollisions(attackX, attackY, attackWidth, attackHeight, facingRight);
        resetAttackAnimation();
    }

    private void resetAttackAnimation() {
        int totalDuration = 0;
        AnimationDrawable attackAnimation = samurai.getAttackAnimation();
        for (int i = 0; i < attackAnimation.getNumberOfFrames(); i++) {
            totalDuration += attackAnimation.getDuration(i);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            isAttacking = false;
            // Solo cambiar la animación si el ataque terminó y no se está moviendo
            if (movingLeft || movingRight) {
                setSamuraiAnimation(samurai.getRunAnimation());
            } else {
                setSamuraiAnimation(samurai.getIdleAnimation());
            }
        }, totalDuration);
    }


    public void startHurtAnimation() {
        // Asegúrate de que la animación de daño esté configurada en el ImageView
        setSamuraiAnimation(samurai.getHurtAnimation());

        // Obtén la duración total de la animación de daño para después volver a la animación idle
        int hurtAnimDuration = 0;
        AnimationDrawable hurtAnimation = samurai.getHurtAnimation();
        for (int i = 0; i < hurtAnimation.getNumberOfFrames(); i++) {
            hurtAnimDuration += hurtAnimation.getDuration(i);
        }

        // Espera a que termine la animación de daño y luego vuelve a la animación idle
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Si el samurái está moviéndose, muestra la animación de correr
            if (movingLeft || movingRight) {
                setSamuraiAnimation(samurai.getRunAnimation());
            } else {
                setSamuraiAnimation(samurai.getIdleAnimation());
            }
        }, hurtAnimDuration);
    }

    private void checkCollisions(int attackX, int attackY, int attackWidth, int attackHeight, boolean facingRight) {
        List<Enemy> enemies = enemyManager.getEnemies();
        for (Enemy enemy : enemies) {
            if ((facingRight && enemy.getX() > samuraiAnimation.getX()) || (!facingRight && enemy.getX() < samuraiAnimation.getX())) {
                if (enemy.checkCollision(attackX, attackY, attackWidth, attackHeight)) {
                    enemy.takeDamage(1, this); // Aplica 1 punto de daño al enemigo
                }
            }
        }
    }

    private void updateSpecialAttackButton() {
        if (enemiesDefeated >= 10) {
            btnSpecialAttack.setEnabled(true);
            enemiesDefeated = 0; // Reiniciar el contador
        } else {
            btnSpecialAttack.setEnabled(false);
        }
        updateSpecialAttackButtonText(); // Actualizar el texto del botón
    }

    private void updateSpecialAttackButtonText() {
        int remainingEnemies = 10 - enemiesDefeated;
        btnSpecialAttack.setText("Ataque Especial (" + remainingEnemies + ")");
    }

    public void incrementEnemiesDefeated() {
        enemiesDefeated++;
        updateSpecialAttackButton();
        updateSpecialAttackButtonText();
    }

    private void handleSpecialAttack() {
        List<Enemy> enemies = enemyManager.getEnemies();
        for (Enemy enemy : enemies) {
            enemy.takeDamage(5, this); // Aplica 5 puntos de daño a cada enemigo
        }
    }


    public void setSamuraiAnimation(AnimationDrawable animation) {
        samuraiAnimation.setBackground(animation);
        animation.stop();
        animation.start();
    }

    public void startSamuraiAnimation() {
        // Cargar la animación desde el archivo XML
        samuraiAnimation.setBackgroundResource(R.drawable.idle_anim);
        // Obtener la animación como AnimationDrawable
        AnimationDrawable animation = (AnimationDrawable) samuraiAnimation.getBackground();
        // Mostrar el ImageView y comenzar la animación
        samuraiAnimation.setVisibility(View.VISIBLE);
        animation.start();
    }

    private void moveSamurai() {
        new Thread(() -> {
            while (true) {
                if (!isGamePaused) { // Solo mover si el juego NO está pausado
                    samuraiAnimation.post(() -> {
                        if (movingLeft) {
                            samurai.setX(samurai.getX() - 7);
                            samuraiAnimation.setX(samurai.getX());

                            if (samurai.getX() + samuraiAnimation.getWidth() < 0) {
                                samurai.setX(context.getResources().getDisplayMetrics().widthPixels);
                                samuraiAnimation.setX(samurai.getX());
                            }
                        } else if (movingRight) {
                            samurai.setX(samurai.getX() + 7);
                            samuraiAnimation.setX(samurai.getX());

                            if (samurai.getX() > context.getResources().getDisplayMetrics().widthPixels) {
                                samurai.setX(-samuraiAnimation.getWidth());
                                samuraiAnimation.setX(samurai.getX());
                            }
                        }
                    });
                }

                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void performDash(boolean toRight) {
        if (isAttacking) {
            return; // No realizar dash si está atacando
        }
        isAttacking = true;
        samurai.setInvulnerable(true); // El samurái es invulnerable durante el dash
        setSamuraiAnimation(samurai.getDashAnimation());

        int dashDistance = 200; // Distancia del dash
        int dashDuration = 300; // Duración del dash en milisegundos

        if (toRight) {
            // Cambiar la dirección para que el samurái mire hacia la derecha
            samurai.setX(samurai.getX() + dashDistance);
            samuraiAnimation.setScaleX(1); // Volteamos el personaje a la derecha
        } else {
            // Cambiar la dirección para que el samurái mire hacia la izquierda
            samurai.setX(samurai.getX() - dashDistance);
            samuraiAnimation.setScaleX(-1); // Volteamos el personaje a la izquierda
        }

        samuraiAnimation.setX(samurai.getX());

        // Después de que termine el dash, se vuelve a poner como vulnerable
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            isAttacking = false;
            samurai.setInvulnerable(false); // El samurái ya no es invulnerable
            // Después de que termine el dash, si el samurái está moviéndose, vuelve a la animación de correr
            if (movingLeft || movingRight) {
                setSamuraiAnimation(samurai.getRunAnimation());
            } else {
                setSamuraiAnimation(samurai.getIdleAnimation());
            }
        }, dashDuration);
    }


    public Samurai getSamurai() {
        return samurai;
    }

    public void setGamePaused(boolean paused) {
        this.isGamePaused = paused;
    }
}
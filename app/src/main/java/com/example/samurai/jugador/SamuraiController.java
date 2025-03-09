package com.example.samurai.jugador;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
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
    private Handler movementHandler;
    private EnemyManager enemyManager;
    private Context context;

    public SamuraiController(Context context, ImageView samuraiAnimation, EnemyManager enemyManager, int screenWidth, int screenHeight) {
        this.context = context;
        this.samuraiAnimation = samuraiAnimation;
        this.enemyManager = enemyManager;

        this.samurai = new Samurai(context, screenWidth, screenHeight);
        this.samuraiAnimation.setX(samurai.getX());
        this.samuraiAnimation.setY(samurai.getY());

        this.movementHandler = new Handler(Looper.getMainLooper());
    }

    public void setupControls(ImageButton btnLeft, ImageButton btnRight, Button btnAttack) {
        btnLeft.setOnTouchListener(this::handleLeftMovement);
        btnRight.setOnTouchListener(this::handleRightMovement);
        btnAttack.setOnClickListener(v -> handleAttack());
        moveSamurai();
        startSamuraiAnimation();
    }

    private boolean handleLeftMovement(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            movingLeft = true;
            samuraiAnimation.setScaleX(-1.5f);
            setSamuraiAnimation(samurai.getRunAnimation());
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            movingLeft = false;
            setSamuraiAnimation(samurai.getIdleAnimation());
            v.performClick();
        }
        return true;
    }

    private boolean handleRightMovement(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            movingRight = true;
            samuraiAnimation.setScaleX(1.5f);
            setSamuraiAnimation(samurai.getRunAnimation());
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            movingRight = false;
            setSamuraiAnimation(samurai.getIdleAnimation());
            v.performClick();
        }
        return true;
    }

    private void handleAttack() {
        if (!isAttacking) {
            isAttacking = true;
            setSamuraiAnimation(samurai.getAttackAnimation());
            boolean facingRight = samuraiAnimation.getScaleX() > 0;
            int attackWidth = samurai.getWidth() / 3;
            int attackX = facingRight ? (int) samuraiAnimation.getX() + samurai.getWidth() / 2
                    : (int) samuraiAnimation.getX() + samurai.getWidth() / 2 - attackWidth;
            int attackY = (int) samuraiAnimation.getY();
            int attackHeight = samurai.getHeight();
            checkCollisions(attackX, attackY, attackWidth, attackHeight, facingRight);
            resetAttackAnimation();
        }
    }

    private void checkCollisions(int attackX, int attackY, int attackWidth, int attackHeight, boolean facingRight) {
        List<Enemy> enemies = enemyManager.getEnemies();
        for (Enemy enemy : enemies) {
            if ((facingRight && enemy.getX() > samuraiAnimation.getX()) || (!facingRight && enemy.getX() < samuraiAnimation.getX())) {
                if (enemy.checkCollision(attackX, attackY, attackWidth, attackHeight)) {
                    enemy.takeDamage();
                }
            }
        }
    }


    private void resetAttackAnimation() {
        int totalDuration = 0;
        AnimationDrawable attackAnimation = samurai.getAttackAnimation();
        for (int i = 0; i < attackAnimation.getNumberOfFrames(); i++) {
            totalDuration += attackAnimation.getDuration(i);
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (movingLeft || movingRight) {
                setSamuraiAnimation(samurai.getRunAnimation());
            } else {
                setSamuraiAnimation(samurai.getIdleAnimation());
            }
            isAttacking = false;
        }, totalDuration);
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
                samuraiAnimation.post(() -> {
                    if (movingLeft) {
                        samurai.setX(samurai.getX() - 5);
                        samuraiAnimation.setX(samurai.getX());
                        // Verificar si el samurai sale por la izquierda
                        if (samurai.getX() + samuraiAnimation.getWidth() < 0) {
                            samurai.setX(context.getResources().getDisplayMetrics().widthPixels); // Coloca en el lado derecho
                            samuraiAnimation.setX(samurai.getX());
                        }
                    } else if (movingRight) {
                        // Mover el ImageView y actualizar la posición interna del Samurai
                        samurai.setX(samurai.getX() + 5);
                        samuraiAnimation.setX(samurai.getX());

                        // Verificar si el samurai sale por la derecha
                        if (samurai.getX() > context.getResources().getDisplayMetrics().widthPixels) {
                            samurai.setX(-samuraiAnimation.getWidth()); // Coloca en el lado izquierdo
                            samuraiAnimation.setX(samurai.getX());
                        }
                    }
                });
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Samurai getSamurai() {
        return samurai;
    }

}
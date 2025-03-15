package com.example.samurai.jugador;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import com.example.samurai.R;
import com.example.samurai.enemigos.Enemy;
import com.example.samurai.enemigos.EnemyManager;

import java.util.List;

public class SamuraiController {
    private final Samurai samurai;
    private final ImageView samuraiAnimation;
    private boolean movingLeft = false, movingRight = false, isAttacking = false, isGamePaused = false, canAttack = true;
    private final EnemyManager enemyManager;
    private final Context context;
    private int enemiesDefeated = 0;
    private int totalEnemiesDefeated = 0;
    private Button btnSpecialAttack;
    private static final int ATTACK_COOLDOWN = 150;
    private final GestureDetector gestureDetector;
    private View outerCircle, innerCircle;
    private float centerX, centerY, baseRadius, stickRadius;

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

    @SuppressLint("ClickableViewAccessibility")
    public void setupControls(View outerCircle, View innerCircle, Button btnAttack, Button btnSpecialAttack, View rootView) {
        this.outerCircle = outerCircle;
        this.innerCircle = innerCircle;
        this.btnSpecialAttack = btnSpecialAttack;
        setupJoystickControls();
        btnAttack.setOnClickListener(v -> handleAttack());
        btnSpecialAttack.setOnClickListener(v -> handleSpecialAttack());

        rootView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
        moveSamurai();
        startSamuraiAnimation();
        btnSpecialAttack.setEnabled(false);
        updateSpecialAttackButtonText();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupJoystickControls() {
        outerCircle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                centerX = outerCircle.getX() + (float) outerCircle.getWidth() / 2;
                centerY = outerCircle.getY() + (float) outerCircle.getHeight() / 2;
                baseRadius = (float) outerCircle.getWidth() / 2;
                stickRadius = (float) innerCircle.getWidth() / 2;
                outerCircle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        outerCircle.setOnTouchListener((v, event) -> {
            float x = event.getX(), y = event.getY();
            float deltaX = x - centerX, deltaY = y - centerY;

            if (Math.abs(deltaX) > (baseRadius - stickRadius)) {
                deltaX = (baseRadius - stickRadius) * Math.signum(deltaX);
            }

            if (Math.abs(deltaY) > (baseRadius - stickRadius)) {
                deltaY = (baseRadius - stickRadius) * Math.signum(deltaY);
            }
            innerCircle.setX(centerX + deltaX - stickRadius);
            innerCircle.setY(centerY + deltaY - stickRadius);

            if (event.getAction() == MotionEvent.ACTION_UP) {
                innerCircle.setX(centerX - stickRadius);
                innerCircle.setY(centerY - stickRadius);
                movingLeft = false;
                movingRight = false;
                if (!isAttacking) {
                    setSamuraiAnimation(samurai.getIdleAnimation());
                }
            } else {
                if (deltaX < 0) {
                    movingLeft = true;
                    movingRight = false;
                    samuraiAnimation.setScaleX(-1.5f);
                    if (!isAttacking && !samuraiAnimation.getBackground().equals(samurai.getRunAnimation())) {
                        setSamuraiAnimation(samurai.getRunAnimation());
                    }
                } else if (deltaX > 0) {
                    movingLeft = false;
                    movingRight = true;
                    samuraiAnimation.setScaleX(1.5f);
                    if (!isAttacking && !samuraiAnimation.getBackground().equals(samurai.getRunAnimation())) {
                        setSamuraiAnimation(samurai.getRunAnimation());
                    }
                }
            }
            return true;
        });
    }

    private void handleAttack() {
        if (isAttacking) return;
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
        new Handler(Looper.getMainLooper()).postDelayed(() -> canAttack = true, ATTACK_COOLDOWN);
    }

    private void resetAttackAnimation() {
        int totalDuration = 0;
        AnimationDrawable attackAnimation = samurai.getAttackAnimation();
        for (int i = 0; i < attackAnimation.getNumberOfFrames(); i++) {
            totalDuration += attackAnimation.getDuration(i);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            isAttacking = false;
            if (movingLeft || movingRight) {
                setSamuraiAnimation(samurai.getRunAnimation());
            } else {
                setSamuraiAnimation(samurai.getIdleAnimation());
            }
        }, totalDuration);
    }

    public void startHurtAnimation() {
        setSamuraiAnimation(samurai.getHurtAnimation());
        int hurtAnimDuration = 0;
        AnimationDrawable hurtAnimation = samurai.getHurtAnimation();
        for (int i = 0; i < hurtAnimation.getNumberOfFrames(); i++) {
            hurtAnimDuration += hurtAnimation.getDuration(i);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
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
                    enemy.takeDamage(1, this);
                }
            }
        }
    }

    private void updateSpecialAttackButton() {
        btnSpecialAttack.setEnabled(enemiesDefeated >= 10);
        updateSpecialAttackButtonText();
    }

    private void updateSpecialAttackButtonText() {
        int remainingEnemies = 10 - enemiesDefeated;
        if (remainingEnemies <= 0) {
            btnSpecialAttack.setText(R.string.ready);
        } else {
            btnSpecialAttack.setText(context.getString(R.string.remaining, remainingEnemies));
        }
    }

    public void incrementEnemiesDefeated() {
        enemiesDefeated++;
        this.totalEnemiesDefeated++;
        updateSpecialAttackButton();
        updateSpecialAttackButtonText();
    }

    private void handleSpecialAttack() {
        List<Enemy> enemies = enemyManager.getEnemies();
        for (Enemy enemy : enemies) {
            enemy.takeDamage(5, this);
        }
        enemiesDefeated = 0;
        updateSpecialAttackButton();
    }

    public void setSamuraiAnimation(AnimationDrawable animation) {
        samuraiAnimation.setBackground(animation);
        animation.stop();
        animation.start();
    }

    public void startSamuraiAnimation() {
        samuraiAnimation.setBackgroundResource(R.drawable.idle_anim);
        AnimationDrawable animation = (AnimationDrawable) samuraiAnimation.getBackground();
        samuraiAnimation.setVisibility(View.VISIBLE);
        animation.start();
    }

    public int getEnemiesDefeated() {
        return totalEnemiesDefeated;
    }

    private void moveSamurai() {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!isGamePaused) {
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
                }
                handler.postDelayed(this, 16);
            }
        };
        handler.post(runnable);
    }

    public void performDash(boolean toRight) {
        if (isAttacking) return;
        isAttacking = true;
        samurai.setInvulnerable(true);
        setSamuraiAnimation(samurai.getDashAnimation());

        int dashDistance = 200, dashDuration = 300, numAfterImages = 5;
        float transparencyStep = 0.2f;

        generateAfterImages(toRight, numAfterImages, transparencyStep);

        if (toRight) {
            samurai.setX(samurai.getX() + dashDistance);
            samuraiAnimation.setScaleX(1.5f);
        } else {
            samurai.setX(samurai.getX() - dashDistance);
            samuraiAnimation.setScaleX(-1.5f);
        }
        samuraiAnimation.setX(samurai.getX());

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            isAttacking = false;
            samurai.setInvulnerable(false);
            if (movingLeft || movingRight) {
                setSamuraiAnimation(samurai.getRunAnimation());
            } else {
                setSamuraiAnimation(samurai.getIdleAnimation());
            }
        }, dashDuration);
    }

    private void generateAfterImages(boolean toRight, int numAfterImages, float transparencyStep) {
        for (int i = 0; i < numAfterImages; i++) {
            final ImageView afterImage = new ImageView(context);
            afterImage.setBackground(samuraiAnimation.getBackground());
            afterImage.setAlpha(1 - i * transparencyStep);

            ViewGroup parentView = (ViewGroup) samuraiAnimation.getParent();

            if (parentView != null) {
                int finalI = i;
                parentView.post(() -> {
                    float afterImageX = samuraiAnimation.getX() - (toRight ? 30 : -30) * (finalI + 1);
                    float afterImageY = samuraiAnimation.getY();
                    afterImage.setX(afterImageX);
                    afterImage.setY(afterImageY);
                    parentView.addView(afterImage);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> parentView.removeView(afterImage), 100);
                });
            }
        }
    }

    public Samurai getSamurai() {
        return samurai;
    }

    public void setGamePaused(boolean paused) {
        this.isGamePaused = paused;
    }
}
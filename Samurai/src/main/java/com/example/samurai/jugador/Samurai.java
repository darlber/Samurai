package com.example.samurai.jugador;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

import com.example.samurai.GameFragment;
import com.example.samurai.R;
import com.example.samurai.SoundManager;

public class Samurai {
    private final AnimationDrawable idleAnimation, runAnimation, attackAnimation, hurtAnimation, dashAnimation;
    private int x;
    private int y;
    private final int width;
    private final int height;
    private int health;
    private boolean isInvulnerable = false;

    public Samurai(Context context, int screenWidth, int screenHeight) {
        idleAnimation = loadAnimation(context, R.drawable.idle_anim);
        runAnimation = loadAnimation(context, R.drawable.run_anim);
        attackAnimation = loadAnimation(context, R.drawable.attack_anim);
        hurtAnimation = loadAnimation(context, R.drawable.hurt_anim);
        dashAnimation = loadAnimation(context, R.drawable.run_anim);

        Drawable firstFrame = idleAnimation.getFrame(0);
        width = firstFrame.getIntrinsicWidth();
        height = firstFrame.getIntrinsicHeight();

        x = (screenWidth - width) / 2;
        y = screenHeight - height - 100;
        health = 5;
        Log.d("Samurai", "Animación cargada correctamente: " + width + "x" + height);
    }

    private AnimationDrawable loadAnimation(Context context, int resId) {
        AnimationDrawable animation = (AnimationDrawable) ResourcesCompat.getDrawable(context.getResources(), resId, null);
        if (animation == null) {
            throw new RuntimeException("No se pudo cargar la animación con ID: " + resId);
        }
        return animation;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(SamuraiController samuraiController, GameFragment gameFragment) {
        if (!isInvulnerable() && !isDead()) {
            this.health--;
            samuraiController.startHurtAnimation();
            SoundManager.playSound(R.raw.hurt_sound);
            if (gameFragment != null) {
                gameFragment.updateHealthTextView(this);

                // Verificar si el samurái ha muerto después de recibir daño
                if (isDead()) {
                    gameFragment.showGameOverDialog();
                }
            }
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    public AnimationDrawable getDashAnimation() {
        return dashAnimation;
    }

    public boolean isInvulnerable() {
        return isInvulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        isInvulnerable = invulnerable;
    }

    public AnimationDrawable getIdleAnimation() {
        return idleAnimation;
    }

    public AnimationDrawable getRunAnimation() {
        return runAnimation;
    }

    public AnimationDrawable getAttackAnimation() {
        return attackAnimation;
    }

    public AnimationDrawable getHurtAnimation() {
        return hurtAnimation;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
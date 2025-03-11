package com.example.samurai.jugador;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.content.Context;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

import com.example.samurai.R;

public class Samurai {
    private AnimationDrawable idleAnimation;
    private AnimationDrawable runAnimation;
    private AnimationDrawable attackAnimation;
    private AnimationDrawable hurtAnimation;
    private int x, y; // Posición del samurái
    private int width, height; // Tamaño del samurái
    private int health;
    private boolean isInvulnerable = false;
    private AnimationDrawable dashAnimation;



    public Samurai(Context context, int screenWidth, int screenHeight) {
        // Cargar la animación desde el archivo XML
        idleAnimation = (AnimationDrawable) ResourcesCompat.getDrawable(context.getResources(), R.drawable.idle_anim, null);
        runAnimation = (AnimationDrawable) ResourcesCompat.getDrawable(context.getResources(), R.drawable.run_anim, null);
        // Cargar animación de ataque
        attackAnimation = (AnimationDrawable) ResourcesCompat.getDrawable(context.getResources(), R.drawable.attack_anim, null);
        // Cargar animación de daño
        hurtAnimation = (AnimationDrawable) ResourcesCompat.getDrawable(context.getResources(), R.drawable.hurt_anim, null);
        dashAnimation = (AnimationDrawable) ResourcesCompat.getDrawable(context.getResources(), R.drawable.run_anim, null);
        if (dashAnimation == null) {
            throw new RuntimeException("No se pudo cargar la animación de dash del samurái.");
        }
        if (idleAnimation == null || runAnimation == null || attackAnimation == null || hurtAnimation == null) {
            throw new RuntimeException("No se pudo cargar la animación del samurái.");
        }

        // Obtener el tamaño del primer frame de la animación
        Drawable firstFrame = idleAnimation.getFrame(0);
        width = firstFrame.getIntrinsicWidth();
        height = firstFrame.getIntrinsicHeight();

        // Posicionar al samurái en la mitad inferior de la pantalla
        x = (screenWidth - width) / 2; // Centrado horizontalmente
        y = screenHeight - height - 100; // 100 píxeles desde la parte inferior
        health = 5;
        Log.d("Samurai", "Animación cargada correctamente: " + width + "x" + height);
    }

    // Métodos para manejar la vida
    public int getHealth() {
        return health;
    }

    public void takeDamage(SamuraiController samuraiController) {
        if (!isInvulnerable() && !isDead()) { // Solo reducir la vida si el samurái no está muerto y no está invulnerable
            health--;
            Log.d("Samurai", "Vida restante: " + health); // Agrega un log para depurar
            samuraiController.startHurtAnimation();
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
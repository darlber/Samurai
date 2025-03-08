package com.example.samurai.enemigos;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

public class Enemy {
    private AnimationDrawable idleAnimation;
    private AnimationDrawable runAnimation;
    private AnimationDrawable attackAnimation;
    private int x, y; // Posición del enemigo
    private int width, height; // Tamaño del enemigo

    public Enemy(Context context, int screenWidth, int screenHeight) {
        // Cargar la animación desde el archivo XML
        idleAnimation = (AnimationDrawable) ResourcesCompat.getDrawable(context.getResources(), R.drawable.enemy_idle_anim, null);
        runAnimation = (AnimationDrawable) ResourcesCompat.getDrawable(context.getResources(), R.drawable.enemy_run_anim, null);
        attackAnimation = (AnimationDrawable) ResourcesCompat.getDrawable(context.getResources(), R.drawable.enemy_attack_anim, null); // Cargar animación de ataque
        if (idleAnimation == null || runAnimation == null || attackAnimation == null) {
            throw new RuntimeException("No se pudo cargar la animación del enemigo.");
        }

        // Obtener el tamaño del primer frame de la animación
        Drawable firstFrame = idleAnimation.getFrame(0);
        width = firstFrame.getIntrinsicWidth();
        height = firstFrame.getIntrinsicHeight();

        // Posicionar al enemigo en la pantalla
        x = screenWidth; // Comienza en el borde derecho de la pantalla
        y = screenHeight - height - 100; // 100 píxeles desde la parte inferior

        Log.d("Enemy", "Animación cargada correctamente: " + width + "x" + height);
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

    public int getX() {
        return x;
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

    public void move() {
        x -= 5; // Mover el enemigo hacia la izquierda
    }
}
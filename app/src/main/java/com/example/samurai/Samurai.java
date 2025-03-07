package com.example.samurai;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.content.Context;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

public class Samurai {
    private AnimationDrawable idleAnimation; // Cambia Bitmap por AnimationDrawable
    private int x, y; // Posición del samurái
    private int width, height; // Tamaño del samurái

    public Samurai(Context context, int screenWidth, int screenHeight) {
        // Cargar la animación desde el archivo XML
        idleAnimation = (AnimationDrawable) ResourcesCompat.getDrawable(context.getResources(), R.drawable.idle_anim, null);
        if (idleAnimation == null) {
            throw new RuntimeException("No se pudo cargar la animación del samurái.");
        }

        // Obtener el tamaño del primer frame de la animación
        Drawable firstFrame = idleAnimation.getFrame(0);
        width = firstFrame.getIntrinsicWidth();
        height = firstFrame.getIntrinsicHeight();

        // Posicionar al samurái en la mitad inferior de la pantalla
        x = (screenWidth - width) / 2; // Centrado horizontalmente
        y = screenHeight - height - 100; // 100 píxeles desde la parte inferior

        Log.d("Samurai", "Animación cargada correctamente: " + width + "x" + height);
    }

    public AnimationDrawable getIdleAnimation() {
        return idleAnimation;
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
}
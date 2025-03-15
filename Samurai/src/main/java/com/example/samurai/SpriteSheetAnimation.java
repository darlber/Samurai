package com.example.samurai;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

public class SpriteSheetAnimation {
    private final Bitmap spriteSheet; // La imagen que contiene todos los frames
    private final int frameWidth;     // Ancho de cada frame
    private final int frameHeight;    // Alto de cada frame
    private final int frameCount;     // Número total de frames
    private int currentFrame;   // Frame actual
    private final long frameTime;     // Tiempo entre frames
    private long lastFrameTime; // Último tiempo de actualización

    public SpriteSheetAnimation(Bitmap spriteSheet, int frameCount, int fps) {
        this.spriteSheet = spriteSheet;
        this.frameCount = frameCount;
        this.frameWidth = spriteSheet.getWidth() / frameCount;
        this.frameHeight = spriteSheet.getHeight();
        this.currentFrame = 0;
        this.frameTime = 1000 / fps; // Tiempo entre frames en milisegundos
        this.lastFrameTime = System.currentTimeMillis();
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameTime) {
            currentFrame = (currentFrame + 1) % frameCount; // Avanzar al siguiente frame
            lastFrameTime = currentTime;
        }
    }

    public void draw(Canvas canvas, Matrix matrix) {
        // Calcular la porción de la spritesheet que corresponde al frame actual
        int srcX = currentFrame * frameWidth;
        Rect srcRect = new Rect(srcX, 0, srcX + frameWidth, frameHeight);
        RectF dstRect = new RectF(0, 0, frameWidth, frameHeight);
        // Dibujar el frame actual en el canvas usando la matriz
        canvas.save();
        canvas.concat(matrix);
        canvas.drawBitmap(spriteSheet, srcRect, dstRect, null);
        canvas.restore();
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public long getTotalDuration() {
        return frameCount * frameTime; // Duración total en milisegundos
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void reset() {
        currentFrame = 0; // Reiniciar al primer frame
        lastFrameTime = System.currentTimeMillis(); // Reiniciar el tiempo del último frame
    }

}
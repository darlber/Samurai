package com.example.samurai.enemigos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.example.samurai.SpriteSheetAnimation;

public class Enemy {
    private SpriteSheetAnimation runAnimation, attackAnimation;
    private SpriteSheetAnimation currentAnimation; // Animación activa
    private int x, y;
    private int width, height;
    private int speed;
    private boolean isFlipped;
    private boolean isAttacking = false;

    private int attackRange = 100; // Distancia para atacar

    public Enemy(Context context, int screenWidth, int screenHeight, int runSpriteSheet, int attackSpriteSheet, int frameCount, int fps, int speed, boolean spawnFromLeft) {
        Bitmap runSheet = BitmapFactory.decodeResource(context.getResources(), runSpriteSheet);
        Bitmap attackSheet = BitmapFactory.decodeResource(context.getResources(), attackSpriteSheet);

        runAnimation = new SpriteSheetAnimation(runSheet, frameCount, fps);
        attackAnimation = new SpriteSheetAnimation(attackSheet, frameCount, fps);

        currentAnimation = runAnimation; // Comienza con la animación de correr

        width = runAnimation.getFrameWidth();
        height = runAnimation.getFrameHeight();

        if (spawnFromLeft) {
            x = -width;
        } else {
            x = screenWidth;
        }
        y = screenHeight - height+50;

        this.speed = speed;
        this.isFlipped = !spawnFromLeft;
    }

    public void update(int samuraiX, int samuraiWidth) {
        currentAnimation.update();

        // Calcular distancia entre los centros del enemigo y el samurái
        int enemyCenterX = x + (width / 2);
        int samuraiCenterX = samuraiX + (samuraiWidth / 2);
        int distanceToSamurai = Math.abs(enemyCenterX - samuraiCenterX);

        if (distanceToSamurai > attackRange) {
            // PERSEGUIR
            isAttacking = false;
            currentAnimation = runAnimation;

            if (enemyCenterX < samuraiCenterX) {
                x += speed;
                isFlipped = false;
            } else {
                x -= speed;
                isFlipped = true;
            }
        } else {
            // ATACAR
            isAttacking = true;
            currentAnimation = attackAnimation;
        }
    }

    public void draw(Canvas canvas) {
        Matrix matrix = new Matrix();
        if (isFlipped) {
            matrix.preScale(-1, 1);
            matrix.postTranslate(x + width, y);
        } else {
            matrix.postTranslate(x, y);
        }

        currentAnimation.draw(canvas, matrix);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}

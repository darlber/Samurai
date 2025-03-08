package com.example.samurai.enemigos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import com.example.samurai.SpriteSheetAnimation;

public class Enemy {
    private SpriteSheetAnimation runAnimation, attackAnimation, currentAnimation, hitAnimation, deathAnimation;
    private int x, y;
    private int width, height;
    private int speed;
    private boolean isFlipped;
    private boolean isAttacking = false;
    private boolean shouldBeRemoved = false;
    private boolean isDead = false;

    private int attackRange = 100; // Distancia para atacar
    private int health = 3; // Vida del enemigo


    public Enemy(Context context, int screenWidth, int screenHeight,
                 int runSpriteSheet, int attackSpriteSheet, int hitSpriteSheet, int deathSpriteSheet,
                 int frameCount, int fps, int speed, boolean spawnFromLeft) {

        Bitmap runSheet = BitmapFactory.decodeResource(context.getResources(), runSpriteSheet);
        Bitmap attackSheet = BitmapFactory.decodeResource(context.getResources(), attackSpriteSheet);
        Bitmap hitSheet = BitmapFactory.decodeResource(context.getResources(), hitSpriteSheet);
        Bitmap deathSheet = BitmapFactory.decodeResource(context.getResources(), deathSpriteSheet);

        if (runSheet == null || attackSheet == null || hitSheet == null || deathSheet == null) {
            throw new RuntimeException("Error: No se pudieron cargar las spritesheets correctamente.");
        }

        runAnimation = new SpriteSheetAnimation(runSheet, frameCount, fps);
        attackAnimation = new SpriteSheetAnimation(attackSheet, frameCount, fps);
        hitAnimation = new SpriteSheetAnimation(hitSheet, frameCount, fps);
        deathAnimation = new SpriteSheetAnimation(deathSheet, frameCount, fps);

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
    public boolean checkCollision(int samuraiX, int samuraiY, int samuraiWidth, int samuraiHeight) {
        return (x < samuraiX + samuraiWidth &&
                x + width > samuraiX &&
                y < samuraiY + samuraiHeight &&
                y + height > samuraiY);
    }
    public void takeDamage() {
        if (!isDead) {
            health--;
            currentAnimation = hitAnimation; // Cambiar a animación de golpe
            if (health <= 0) {
                die();
            }
        }
    }

    private void die() {
        isDead = true;
        currentAnimation = deathAnimation;
        // Después de la animación de muerte, eliminar al enemigo
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
             shouldBeRemoved = true; // Marcar para eliminación en GameFragment
        }, deathAnimation.getTotalDuration());
    }

    public boolean isMarkedForRemoval() {
        return shouldBeRemoved;
    }
    public void update(int samuraiX, int samuraiWidth) {
        if (currentAnimation == null) {
            currentAnimation = runAnimation; // Asegurar que siempre tenga una animación válida
        }

        currentAnimation.update(); // Ahora estamos seguros de que no es null

        if (isDead) return;

        int enemyCenterX = x + (width / 2);
        int samuraiCenterX = samuraiX + (samuraiWidth / 2);
        int distanceToSamurai = Math.abs(enemyCenterX - samuraiCenterX);

        if (distanceToSamurai > attackRange) {
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

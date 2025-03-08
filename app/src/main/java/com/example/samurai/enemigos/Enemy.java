package com.example.samurai.enemigos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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


    public Enemy(Context context, int screenWidth, int screenHeight, int runSpriteSheet, int attackSpriteSheet, int hitSpriteSheet, int deathSpriteSheet, int runFrameCount, int attackFrameCount, int fps, int speed, boolean spawnFromLeft) {

        Bitmap runSheet = BitmapFactory.decodeResource(context.getResources(), runSpriteSheet);
        Bitmap attackSheet = BitmapFactory.decodeResource(context.getResources(), attackSpriteSheet);
        Bitmap hitSheet = BitmapFactory.decodeResource(context.getResources(), hitSpriteSheet);
        Bitmap deathSheet = BitmapFactory.decodeResource(context.getResources(), deathSpriteSheet);

        if (runSheet == null || attackSheet == null || hitSheet == null || deathSheet == null) {
            throw new RuntimeException("Error: No se pudieron cargar las spritesheets correctamente.");
        }

        runAnimation = new SpriteSheetAnimation(runSheet, runFrameCount, fps);
        attackAnimation = new SpriteSheetAnimation(attackSheet, attackFrameCount, fps);
        hitAnimation = new SpriteSheetAnimation(hitSheet, 4, fps);
        deathAnimation = new SpriteSheetAnimation(deathSheet, 4, fps);

        currentAnimation = runAnimation; // Comienza con la animación de correr

        width = runAnimation.getFrameWidth();
        height = runAnimation.getFrameHeight();

        if (spawnFromLeft) {
            x = -width;
        } else {
            x = screenWidth;
        }
        y = screenHeight - height + 40;

        this.speed = speed;
        this.isFlipped = !spawnFromLeft;
    }

    public boolean checkCollision(int samuraiX, int samuraiY, int samuraiWidth, int samuraiHeight) {
        return (x < samuraiX + samuraiWidth && x + width > samuraiX && y < samuraiY + samuraiHeight && y + height > samuraiY);
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

    public boolean isMarkedForRemoval() {
        return shouldBeRemoved;
    }

    public void update(int samuraiX, int samuraiWidth) {
        // Si el enemigo está muerto, solo actualizar la animación de muerte
        if (isDead) {
            currentAnimation.update(); // Actualizar la animación de muerte

            // Verificar si la animación de muerte ha terminado
            if (currentAnimation == deathAnimation && currentAnimation.getCurrentFrame() == deathAnimation.getFrameCount() - 1) {
                shouldBeRemoved = true; // Marcar para eliminación después de que la animación de muerte haya terminado
            }
            return; // No actualizar el comportamiento si está muerto
        }

        // Asegurar que siempre tenga una animación válida
        if (currentAnimation == null) {
            currentAnimation = runAnimation;
        }

        // Actualizar la animación actual
        currentAnimation.update();

        // Calcular la distancia al samurái
        int enemyCenterX = x + (width / 2);
        int samuraiCenterX = samuraiX + (samuraiWidth / 2);
        int distanceToSamurai = Math.abs(enemyCenterX - samuraiCenterX);

        // Comportamiento del enemigo si no está muerto
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

    public void die() {
        if (isDead) return; // Si ya está muerto, no hacer nada más

        isDead = true; // Marcar como muerto
        currentAnimation = deathAnimation; // Cambiar a la animación de muerte
        currentAnimation.reset(); // Reiniciar la animación de muerte para asegurarnos de que comience desde el primer frame
    }

    public void draw(Canvas canvas) {
        if (shouldBeRemoved) return; // No dibujar si el enemigo está marcado para eliminación

        Matrix matrix = new Matrix();
        if (isFlipped) {
            matrix.preScale(-1, 1);
            matrix.postTranslate(x + width, y);
        } else {
            matrix.postTranslate(x, y);
        }

        currentAnimation.draw(canvas, matrix);
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

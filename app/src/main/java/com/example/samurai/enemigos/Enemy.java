package com.example.samurai.enemigos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import com.example.samurai.SpriteSheetAnimation;
import com.example.samurai.jugador.Samurai;
import com.example.samurai.jugador.SamuraiController;

public class Enemy {
    private SpriteSheetAnimation runAnimation, attackAnimation, currentAnimation, hitAnimation, deathAnimation;
    private int x, y;
    private int width, height;
    private int speed;
    private boolean isFlipped;
    private boolean shouldBeRemoved = false;
    private boolean isDead = false;
    private int health;
    private String type;

    public Enemy(Context context, int screenWidth, int screenHeight, int runSpriteSheet, int attackSpriteSheet,
                 int hitSpriteSheet, int deathSpriteSheet, int speed, boolean spawnFromLeft, int health, String type) {
        Bitmap runSheet = BitmapFactory.decodeResource(context.getResources(), runSpriteSheet);
        Bitmap attackSheet = BitmapFactory.decodeResource(context.getResources(), attackSpriteSheet);
        Bitmap hitSheet = BitmapFactory.decodeResource(context.getResources(), hitSpriteSheet);
        Bitmap deathSheet = BitmapFactory.decodeResource(context.getResources(), deathSpriteSheet);

        runAnimation = new SpriteSheetAnimation(runSheet, 8, 10);
        attackAnimation = new SpriteSheetAnimation(attackSheet, 8, 10);
        hitAnimation = new SpriteSheetAnimation(hitSheet, 4, 10);
        deathAnimation = new SpriteSheetAnimation(deathSheet, 4, 10);

        this.health = health;
        this.type = type;
        currentAnimation = runAnimation;

        width = runAnimation.getFrameWidth();
        height = runAnimation.getFrameHeight();

        x = spawnFromLeft ? -width : screenWidth;
        y = screenHeight - height + 40;

        this.speed = speed;
        this.isFlipped = !spawnFromLeft;
    }

    public boolean checkCollision(int samuraiX, int samuraiY, int samuraiWidth, int samuraiHeight) {
        return (x < samuraiX + samuraiWidth && x + width > samuraiX && y < samuraiY + samuraiHeight && y + height > samuraiY);
    }

    public void takeDamage(int damage, SamuraiController samuraiController) {
        if (!isDead) {
            this.health -= damage;
            currentAnimation = hitAnimation;
            currentAnimation.reset();

            if (health <= 0) {
                die();
                samuraiController.incrementEnemiesDefeated(); // Notificar al SamuraiController
            }
        }
    }

    public boolean isMarkedForRemoval() {
        return shouldBeRemoved;
    }

    public void update(int samuraiX, int samuraiWidth, Samurai samurai, SamuraiController samuraiController) {
        if (isDead) {
            currentAnimation.update();
            if (currentAnimation == deathAnimation && currentAnimation.getCurrentFrame() == deathAnimation.getFrameCount() - 1) {
                shouldBeRemoved = true;
            }
            return;
        }

        if (currentAnimation == hitAnimation) {
            currentAnimation.update();
            if (currentAnimation.getCurrentFrame() == hitAnimation.getFrameCount() - 1) {
                currentAnimation = runAnimation;
            }
            return;
        }

        int enemyCenterX = x + (width / 2);
        int samuraiCenterX = samuraiX + (samuraiWidth / 2);
        int distanceToSamurai = Math.abs(enemyCenterX - samuraiCenterX);
        int minimumDistance = 100; // Distancia mínima para atacar

        if (distanceToSamurai > minimumDistance) {
            // Si el enemigo está en attackAnimation pero el jugador ya se alejó, cambiar a runAnimation
            if (currentAnimation == attackAnimation) {
                currentAnimation = runAnimation;
            }

            if (enemyCenterX < samuraiCenterX) {
                x += speed;
                isFlipped = false;
            } else {
                x -= speed;
                isFlipped = true;
            }
        } else {
            // Cambiar a la animación de ataque si no está atacando
            if (currentAnimation != attackAnimation) {
                currentAnimation = attackAnimation;
                currentAnimation.reset();
            }

            if (currentAnimation == attackAnimation && currentAnimation.getCurrentFrame() == attackAnimation.getFrameCount() - 1) {
                if (checkCollision(samuraiX, samurai.getY(), samuraiWidth, samurai.getHeight())) {
                    samurai.takeDamage(samuraiController);
                    currentAnimation = runAnimation;  // Asegurar que después de atacar vuelva a correr
                }
            }
        }

        currentAnimation.update();
    }



    public void die() {
        isDead = true;
        currentAnimation = deathAnimation;
        currentAnimation.reset();
    }

    public void draw(Canvas canvas) {
        if (shouldBeRemoved) return;

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

    public String getType() {
        return type;
    }
}
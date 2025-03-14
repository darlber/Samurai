package com.example.samurai.enemigos;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.example.samurai.SpriteSheetAnimation;
import com.example.samurai.jugador.Samurai;
import com.example.samurai.jugador.SamuraiController;

public class Enemy {
    private final SpriteSheetAnimation runAnimation;
    private final SpriteSheetAnimation attackAnimation;
    private SpriteSheetAnimation currentAnimation;
    private final SpriteSheetAnimation hitAnimation;
    private final SpriteSheetAnimation deathAnimation;
    private int x;
    private final int y;
    private final int width;
    private final int height;
    private final int speed;
    private int health;
    private boolean isFlipped, shouldBeRemoved = false, isDead = false;
    private final String type;

    public Enemy(Context context, int screenWidth, int screenHeight, int runSpriteSheet, int attackSpriteSheet,
                 int hitSpriteSheet, int deathSpriteSheet, int speed, boolean spawnFromLeft, int health, String type) {
        runAnimation = new SpriteSheetAnimation(BitmapFactory.decodeResource(context.getResources(), runSpriteSheet), 8, 10);
        attackAnimation = new SpriteSheetAnimation(BitmapFactory.decodeResource(context.getResources(), attackSpriteSheet), 8, 10);
        hitAnimation = new SpriteSheetAnimation(BitmapFactory.decodeResource(context.getResources(), hitSpriteSheet), 4, 10);
        deathAnimation = new SpriteSheetAnimation(BitmapFactory.decodeResource(context.getResources(), deathSpriteSheet), 4, 10);

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
            health -= damage;
            currentAnimation = hitAnimation;
            currentAnimation.reset();

            if (health <= 0) {
                die();
                samuraiController.incrementEnemiesDefeated();
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
        int minimumDistance = 100;

        if (distanceToSamurai > minimumDistance) {
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
            if (currentAnimation != attackAnimation) {
                currentAnimation = attackAnimation;
                currentAnimation.reset();
            }

            if (currentAnimation == attackAnimation && currentAnimation.getCurrentFrame() == attackAnimation.getFrameCount() - 1) {
                if (checkCollision(samuraiX, samurai.getY(), samuraiWidth, samurai.getHeight())) {
                    samurai.takeDamage(samuraiController);
                    currentAnimation = runAnimation;
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
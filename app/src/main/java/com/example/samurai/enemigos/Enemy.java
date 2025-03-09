package com.example.samurai.enemigos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import com.example.samurai.SpriteSheetAnimation;

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

    public void takeDamage() {
        if (!isDead) {
            health--;
            currentAnimation = hitAnimation;
            currentAnimation.reset();

            if (health <= 0) {
                die();
            }
        }
    }

    public boolean isMarkedForRemoval() {
        return shouldBeRemoved;
    }

    public void update(int samuraiX, int samuraiWidth) {
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

        currentAnimation.update();

        int enemyCenterX = x + (width / 2);
        int samuraiCenterX = samuraiX + (samuraiWidth / 2);
        int distanceToSamurai = Math.abs(enemyCenterX - samuraiCenterX);
        // Agregar una distancia mínima para que el enemigo se detenga antes de llegar al samurái
        int minimumDistance = 75; // Puedes ajustar esta distancia según sea necesario

        if (distanceToSamurai > minimumDistance) {
            if (enemyCenterX < samuraiCenterX) {
                x += speed;
                isFlipped = false;
            } else {
                x -= speed;
                isFlipped = true;
            }
        } else {
            currentAnimation = attackAnimation;
        }
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
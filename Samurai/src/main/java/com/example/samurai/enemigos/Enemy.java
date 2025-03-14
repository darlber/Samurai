package com.example.samurai.enemigos;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

import com.example.samurai.GameFragment;
import com.example.samurai.R;
import com.example.samurai.SpriteSheetAnimation;
import com.example.samurai.jugador.Samurai;
import com.example.samurai.jugador.SamuraiController;

public class Enemy {
    private SpriteSheetAnimation runAnimation;
    private SpriteSheetAnimation attackAnimation;
    private SpriteSheetAnimation currentAnimation;
    private SpriteSheetAnimation hitAnimation;
    private SpriteSheetAnimation deathAnimation;
    private int x;
    private int y;
    private int width;
    private int height;
    private int speed;
    private int health;
    private boolean isFlipped, shouldBeRemoved = false, isDead = false;
    private String type;
    private final Context context;
    private final int screenWidth;
    private final int screenHeight;

    public Enemy(Context context, int screenWidth, int screenHeight, int runSpriteSheet, int attackSpriteSheet,
                 int hitSpriteSheet, int deathSpriteSheet, int speed, boolean spawnFromLeft, int health, String type) {
        this.context = context;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        initialize(runSpriteSheet, attackSpriteSheet, hitSpriteSheet, deathSpriteSheet, speed, spawnFromLeft, health, type);
    }

    private void initialize(int runSpriteSheet, int attackSpriteSheet, int hitSpriteSheet, int deathSpriteSheet, int speed, boolean spawnFromLeft, int health, String type) {
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

    public void reset(int enemyType, boolean spawnFromLeft) {
        switch (enemyType) {
            case 1:
                initialize(R.drawable.flying_eye_run, R.drawable.flying_eye_attack, R.drawable.flying_eye_hit, R.drawable.flying_eye_death, 3, spawnFromLeft, 1, "flying_eye");
                break;
            case 2:
                initialize(R.drawable.mushroom_run, R.drawable.mushroom_attack, R.drawable.mushroom_hit, R.drawable.mushroom_death, 1, spawnFromLeft, 5, "mushroom");
                break;
            default:
                initialize(R.drawable.goblin_run, R.drawable.goblin_attack, R.drawable.goblin_hit, R.drawable.goblin_death, 2, spawnFromLeft, 3, "goblin");
                break;
        }
        shouldBeRemoved = false;
        isDead = false;
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

    public void update(int samuraiX, int samuraiWidth, Samurai samurai, SamuraiController samuraiController, GameFragment gameFragment) {
        if (isDead) {
            currentAnimation.update();
            if (currentAnimation == deathAnimation && currentAnimation.getCurrentFrame() == deathAnimation.getFrameCount() - 1) {
                shouldBeRemoved = true;
            }
            return;
        }

        // Solo actualiza animaciones si realmente hubo un cambio
        if (currentAnimation == hitAnimation) {
            currentAnimation.update();
            if (currentAnimation.getCurrentFrame() == hitAnimation.getFrameCount() - 1) {
                currentAnimation = runAnimation;
            }
            return;
        }

        // Calcular la distancia entre el enemigo y el samurai
        int enemyCenterX = x + (width / 2);
        int samuraiCenterX = samuraiX + (samuraiWidth / 2);
        int distanceToSamurai = Math.abs(enemyCenterX - samuraiCenterX);
        int minimumDistance = 100; // Este es el rango de detección

        // Solo realizar la verificación de colisión si el enemigo está dentro del rango de proximidad
        if (distanceToSamurai <= minimumDistance) {
            if (currentAnimation != attackAnimation) {
                currentAnimation = attackAnimation;
                currentAnimation.reset();
            }

            // Verificar colisión si el enemigo está atacando
            if (currentAnimation == attackAnimation && currentAnimation.getCurrentFrame() == attackAnimation.getFrameCount() - 1) {
                if (checkCollision(samuraiX, samurai.getY(), samuraiWidth, samurai.getHeight())) {
                    samurai.takeDamage(samuraiController, gameFragment);
                    currentAnimation = runAnimation; // Volver a la animación de correr
                }
            }
        } else {
            // Movimiento del enemigo solo si está fuera del rango de ataque
            if (currentAnimation == attackAnimation) {
                currentAnimation = runAnimation; // Volver a la animación de correr
            }

            // Moverse hacia el samurai si está lo suficientemente cerca
            if (enemyCenterX < samuraiCenterX) {
                x += speed;
                isFlipped = false;
            } else {
                x -= speed;
                isFlipped = true;
            }
        }

        // Actualizar la animación
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
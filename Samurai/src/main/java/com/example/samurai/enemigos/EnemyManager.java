package com.example.samurai.enemigos;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.samurai.R;
import com.example.samurai.jugador.Samurai;
import com.example.samurai.jugador.SamuraiController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EnemyManager {
    private final List<Enemy> enemies = new ArrayList<>();
    private final Handler enemyHandler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private final Context context;
    private final int screenWidth;
    private final int screenHeight;
    private int maxEnemies = 10;
    private int spawnInterval = 3000;
    private boolean isGamePaused = false;

    public EnemyManager(Context context, int screenWidth, int screenHeight) {
        this.context = context;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        increaseDifficultyOverTime();
    }

    public void spawnEnemies() {
        enemyHandler.postDelayed(() -> {
            if (enemies.size() < maxEnemies) {
                int enemyType = random.nextInt(3);
                boolean spawnFromLeft = random.nextBoolean();
                Enemy enemy = createEnemy(enemyType, spawnFromLeft);
                enemies.add(enemy);
            }
            enemyHandler.postDelayed(this::spawnEnemies, random.nextInt(spawnInterval) + 1000);
        }, 1000);
    }


    private Enemy createEnemy(int enemyType, boolean spawnFromLeft) {
        int[] drawables;
        int speed, health;
        String type;

        switch (enemyType) {
            case 1:
                drawables = new int[]{R.drawable.flying_eye_run, R.drawable.flying_eye_attack, R.drawable.flying_eye_hit, R.drawable.flying_eye_death};
                speed = 3;
                health = 1;
                type = "flying_eye";
                break;
            case 2:
                drawables = new int[]{R.drawable.mushroom_run, R.drawable.mushroom_attack, R.drawable.mushroom_hit, R.drawable.mushroom_death};
                speed = 1;
                health = 5;
                type = "mushroom";
                break;
            default:
                drawables = new int[]{R.drawable.goblin_run, R.drawable.goblin_attack, R.drawable.goblin_hit, R.drawable.goblin_death};
                speed = 2;
                health = 3;
                type = "goblin";
                break;
        }

        return new Enemy(context, screenWidth, screenHeight, drawables[0], drawables[1], drawables[2], drawables[3], speed, spawnFromLeft, health, type);
    }

    private void increaseDifficultyOverTime() {
        enemyHandler.postDelayed(() -> {
            if (maxEnemies < 50) maxEnemies++;
            if (spawnInterval > 1000) spawnInterval -= 500;
            enemyHandler.postDelayed(this::increaseDifficultyOverTime, 15000);
        }, 15000);
    }

    public void updateEnemies(int samuraiX, int samuraiWidth, Samurai samurai, SamuraiController samuraiController) {
        if (isGamePaused) return;
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.isMarkedForRemoval()) {
                iterator.remove();
            } else {
                enemy.update(samuraiX, samuraiWidth, samurai, samuraiController);
            }
        }
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void setGamePaused(boolean paused) {
        this.isGamePaused = paused;
    }
}
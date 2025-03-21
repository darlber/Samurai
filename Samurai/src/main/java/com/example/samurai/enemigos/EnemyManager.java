package com.example.samurai.enemigos;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.samurai.GameFragment;
import com.example.samurai.R;
import com.example.samurai.jugador.Samurai;
import com.example.samurai.jugador.SamuraiController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EnemyManager {
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Enemy> enemyPool = new ArrayList<>();
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
        startSpawningEnemies();
    }

    private void startSpawningEnemies() {
        enemyHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isGamePaused && enemies.size() < maxEnemies) {
                    spawnEnemy();
                }
                enemyHandler.postDelayed(this, spawnInterval);
            }
        }, spawnInterval);
    }

    public void spawnEnemy() {
        int enemyType = random.nextInt(3);
        boolean spawnFromLeft = random.nextBoolean();
        Enemy enemy = getEnemyFromPool(enemyType, spawnFromLeft);
        enemies.add(enemy);
    }

    private Enemy getEnemyFromPool(int enemyType, boolean spawnFromLeft) {
        if (!enemyPool.isEmpty()) {
            Enemy enemy = enemyPool.remove(enemyPool.size() - 1);
            enemy.reset(enemyType, spawnFromLeft);
            return enemy;
        }
        return createEnemy(enemyType, spawnFromLeft);
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
    enemyHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
            if (maxEnemies < 50) maxEnemies++;
            if (spawnInterval > 1000) spawnInterval -= 500;
            enemyHandler.postDelayed(this, 15000);
        }
    }, 15000);
}

    public void updateEnemies(int samuraiX, int samuraiWidth, Samurai samurai,
                              SamuraiController samuraiController, GameFragment gameFragment) {
        if (isGamePaused) return;
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.isMarkedForRemoval()) {
                iterator.remove();
                enemyPool.add(enemy);
            } else {
                enemy.update(samuraiX, samuraiWidth, samurai, samuraiController, gameFragment);
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
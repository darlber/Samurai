package com.example.samurai.enemigos;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;

import com.example.samurai.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class EnemyManager {
    private List<Enemy> enemies;
    private Handler enemyHandler;
    private Random random;
    private Context context;
    private int screenWidth, screenHeight;
    private static final int MAX_ENEMIES = 10;

    public EnemyManager(Context context, int screenWidth, int screenHeight) {
        this.context = context;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.enemies = new ArrayList<>();
        this.enemyHandler = new Handler(Looper.getMainLooper());
        this.random = new Random();
    }

    public void spawnEnemies() {
        enemyHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (enemies.size() < MAX_ENEMIES) {
                    int enemyType = random.nextInt(3);
                    boolean spawnFromLeft = random.nextBoolean();

                    Enemy enemy;
                    switch (enemyType) {
                        case 1:
                            enemy = new Enemy(context, screenWidth, screenHeight, R.drawable.flying_eye_run,
                                    R.drawable.flying_eye_attack, R.drawable.flying_eye_hit, R.drawable.flying_eye_death,
                                    3, spawnFromLeft, 1, "flying_eye");
                            break;
                        case 2:
                            enemy = new Enemy(context, screenWidth, screenHeight, R.drawable.mushroom_run,
                                    R.drawable.mushroom_attack, R.drawable.mushroom_hit, R.drawable.mushroom_death,
                                    1, spawnFromLeft, 5, "mushroom");
                            break;
                        default:
                            enemy = new Enemy(context, screenWidth, screenHeight, R.drawable.goblin_run,
                                    R.drawable.goblin_attack, R.drawable.goblin_hit, R.drawable.goblin_death,
                                    2, spawnFromLeft, 3, "goblin");
                            break;
                    }

                    enemies.add(enemy);
                    enemyHandler.postDelayed(this, random.nextInt(2000) + 1000);
                }
            }
        }, 1000);
    }

    public void updateEnemies(int samuraiX, int samuraiWidth) {
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.isMarkedForRemoval()) {
                iterator.remove();
            } else {
                enemy.update(samuraiX, samuraiWidth);
            }
        }
    }


    public void drawEnemies(Canvas canvas) {
        for (Enemy enemy : enemies) {
            enemy.draw(canvas);
        }
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}

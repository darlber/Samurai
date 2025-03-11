// EnemyManager.java
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
    private List<Enemy> enemies;
    private Handler enemyHandler;
    private Random random;
    private Context context;
    private int screenWidth, screenHeight;
    private int maxEnemies = 10; // Variable para el número máximo de enemigos
    private int spawnInterval = 3000; // Intervalo de aparición de enemigos en milisegundos
    private boolean isGamePaused = false;

    public EnemyManager(Context context, int screenWidth, int screenHeight) {
        this.context = context;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.enemies = new ArrayList<>();
        this.enemyHandler = new Handler(Looper.getMainLooper());
        this.random = new Random();
        increaseDifficultyOverTime(); // Iniciar el aumento de dificultad
    }

    public void spawnEnemies() {
        enemyHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (enemies.size() < maxEnemies) {
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
                    enemyHandler.postDelayed(this, random.nextInt(spawnInterval) + 1000);
                }
            }
        }, 1000);
    }

    //cada 15 segundos, 1 enemigo más y el intervalo de aparición disminuye en 500ms
    private void increaseDifficultyOverTime() {
        enemyHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                maxEnemies++; // Incrementar el número máximo de enemigos
                if (spawnInterval > 1000) {
                    spawnInterval -= 500; // Reducir el intervalo de aparición de enemigos
                }
                enemyHandler.postDelayed(this, 15000); // Incrementar cada 30 segundos
            }
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
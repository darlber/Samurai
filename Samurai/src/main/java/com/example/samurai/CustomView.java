package com.example.samurai;

import android.content.Context;
import android.graphics.Canvas;

import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.samurai.enemigos.Enemy;
import com.example.samurai.enemigos.EnemyManager;
import com.example.samurai.jugador.Samurai;
import com.example.samurai.jugador.SamuraiController;

import java.util.List;

public class CustomView extends View {
    private final int screenWidth;
    private final List<Enemy> enemies;
    private final Samurai samurai;
    private final ScoreManager scoreManager;
    private final EnemyManager enemyManager;
    private final GameFragment gameFragment;
    private final SamuraiController samuraiController;

    public CustomView(Context context) {
        super(context);
        this.screenWidth = 0;
        this.enemies = null;
        this.samurai = null;
        this.samuraiController = null;
        this.enemyManager = null;
        this.scoreManager = null;
        this.gameFragment = null;
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.screenWidth = 0;
        this.enemies = null;
        this.samurai = null;
        this.samuraiController = null;
        this.enemyManager = null;
        this.scoreManager = null;
        this.gameFragment = null;
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.screenWidth = 0;
        this.enemies = null;
        this.samurai = null;
        this.samuraiController = null;
        this.enemyManager = null;
        this.scoreManager = null;
        this.gameFragment = null;
    }

    public CustomView(Context context, int screenWidth, List<Enemy> enemies,
                      EnemyManager enemyManager, Samurai samurai, SamuraiController samuraiController,
                      ScoreManager scoreManager, GameFragment gameFragment) {
        super(context);
        this.screenWidth = screenWidth;
        this.enemies = enemies;
        this.samurai = samurai;
        this.samuraiController = samuraiController;
        this.enemyManager = enemyManager;
        this.scoreManager = scoreManager;
        this.gameFragment = gameFragment;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Obtener la posición del samurái
        int samuraiX = samurai.getX();
        int samuraiWidth = samurai.getWidth();

        // Actualizar la posición de los enemigos en función del samurái
        enemyManager.updateEnemies(samuraiX, samuraiWidth, samurai, samuraiController, gameFragment);

        // Dibujar todos los enemigos
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);

            if (enemy.isMarkedForRemoval()) {
                // Aumentar los puntos según el tipo de enemigo
                switch (enemy.getType()) {
                    case "flying_eye":
                        scoreManager.addScore(10);
                        break;
                    case "goblin":
                        scoreManager.addScore(30);
                        break;
                    case "mushroom":
                        scoreManager.addScore(50);
                        break;
                }

                enemies.remove(i); // Eliminar el enemigo de la lista
                i--; // Ajustar el índice después de eliminar un enemigo
                continue;
            }

            enemy.draw(canvas); // Dibujar el enemigo

            // Eliminar enemigos que salen de la pantalla
            if (enemy.getX() + enemy.getWidth() < 0 || enemy.getX() > screenWidth) {
                enemies.remove(i);
                i--; // Ajustar el índice después de eliminar un enemigo
            }
        }
        // Volver a dibujar la vista
        invalidate();
    }
}
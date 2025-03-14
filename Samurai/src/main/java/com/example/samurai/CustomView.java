package com.example.samurai;

import android.content.Context;
import android.graphics.Canvas;

import android.view.View;

import com.example.samurai.enemigos.Enemy;
import com.example.samurai.enemigos.EnemyManager;
import com.example.samurai.jugador.Samurai;
import com.example.samurai.jugador.SamuraiController;

import java.util.List;

public class CustomView extends View {
    private int screenWidth, screenHeight;
    private List<Enemy> enemies;
    private Samurai samurai;
    private ScoreManager scoreManager;
    private EnemyManager enemyManager;
    private GameFragment gameFragment;
    private SamuraiController samuraiController;

    public CustomView(Context context, int screenWidth, int screenHeight, List<Enemy> enemies,
                      EnemyManager enemyManager, Samurai samurai, SamuraiController samuraiController, ScoreManager scoreManager, GameFragment gameFragment) {
        super(context);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.enemies = enemies;
        this.samurai = samurai;
        this.samuraiController = samuraiController;
        this.enemyManager = enemyManager;
        this.scoreManager = scoreManager;
        this.gameFragment = gameFragment;
    }

    @Override
    protected void onDraw(Canvas canvas) {
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

        // Verificar si el samurái ha perdido toda su vida
        if (samurai.isDead()) {
            // Terminar el juego
            gameFragment.endGame();
        }

        // Volver a dibujar la vista
        invalidate();
    }
}
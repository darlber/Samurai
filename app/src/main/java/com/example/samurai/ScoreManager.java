package com.example.samurai;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

public class ScoreManager {
    private int score;
    private int highScore;
    private TextView scoreTextView;
    private Context context;

    public ScoreManager(Context context, TextView scoreTextView) {
        this.context = context;
        this.scoreTextView = scoreTextView;
        this.score = 0;
        this.highScore = loadHighScore();
        // Solo actualizar el texto si scoreTextView no es nulo
        if (scoreTextView != null) {
            updateScoreText();
        }
    }

    public void addScore(int points) {
        score += points;
        if (score > highScore) { // Si el score actual es mayor, actualizamos el high score
            highScore = score;
            saveHighScore(); // Guardamos el nuevo high score inmediatamente
        }
        updateScoreText();
        saveScore(); // Guardamos el score normal
    }


    private void updateScoreText() {
        // Mostrar el puntaje actual y el high score
        scoreTextView.setText("Score: " + score + " | High Score: " + highScore);
    }

    private void saveScore() {
        // Guardar el puntaje en SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("score", score);
        editor.apply();
    }
    public int getHighScore() {
        return highScore;
    }
    public void resetScore() {
        score = 0;
        updateScoreText();
    }

    public void saveHighScore() {
        SharedPreferences preferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("high_score", highScore); // Guardar highScore actualizado
        editor.apply();
    }


    private int loadHighScore() {
        SharedPreferences preferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE);
        return preferences.getInt("high_score", 0); // Devuelve 0 si no hay high score guardado
    }

}

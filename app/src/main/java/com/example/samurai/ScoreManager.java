package com.example.samurai;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

public class ScoreManager {
    private int score;
    private TextView scoreTextView;
    private Context context;

    public ScoreManager(Context context, TextView scoreTextView) {
        this.context = context;
        this.scoreTextView = scoreTextView;
        this.score = loadScore(); // Cargar el puntaje guardado
        updateScoreText();
    }

    public void addScore(int points) {
        score += points;
        updateScoreText();
        saveScore(); // Guardar el puntaje cada vez que se actualice
    }

    public int getScore() {
        return score;
    }

    private void updateScoreText() {
        scoreTextView.setText("Score: " + score);
    }

    private void saveScore() {
        // Guardar el puntaje en SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("score", score);
        editor.apply();
    }

    private int loadScore() {
        SharedPreferences preferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE);
        return preferences.getInt("score", 0); // Devuelve 0 si no hay puntaje guardado
    }
}

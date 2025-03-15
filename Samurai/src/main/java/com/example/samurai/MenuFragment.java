package com.example.samurai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class MenuFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menu_layout, container, false);
        // Obtener el TextView del high score
        TextView highScoreTextView = rootView.findViewById(R.id.highScoreTextView);

        // Cargar el high score desde ScoreManager
        ScoreManager scoreManager = new ScoreManager(requireContext(), null); // No necesitamos el scoreTextView aquí
        int highScore = scoreManager.getHighScore();
        highScoreTextView.setText(getString(R.string.high_score, highScore));
        // Configurar el botón de play
        ImageView playButton = rootView.findViewById(R.id.playButton);
        playButton.setOnClickListener(v -> {
            // Cambiar al fragmento del juego
            GameFragment gameFragment = new GameFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frame, gameFragment)
                    .addToBackStack(null)
                    .commit();
        });

    requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirmación")
                    .setMessage("¿Estás seguro de que deseas salir?")
                    .setPositiveButton("Sí", (dialog, which) -> requireActivity().finish())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setCancelable(true)
                    .show();
        }
    });
        return rootView;
    }
}
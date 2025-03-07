package com.example.samurai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MenuFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menu_layout, container, false);

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

        return rootView;
    }
}
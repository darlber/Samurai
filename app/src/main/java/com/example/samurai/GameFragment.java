package com.example.samurai;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GameFragment extends Fragment {

    private ImageView samuraiAnimation;
    private boolean isRunning = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_layout, container, false);



        samuraiAnimation = rootView.findViewById(R.id.samuraiAnimation);

        // Obtener dimensiones de la pantalla
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Crear el objeto Samurai con las dimensiones de la pantalla
        Samurai samurai = new Samurai(getContext(), screenWidth, screenHeight);

        // Configurar la posición inicial del samurái en la mitad inferior de la pantalla
        //
        samuraiAnimation.setX(samurai.getX()); // Posición X centrada
        samuraiAnimation.setY(samurai.getY()); // Posición Y en la parte inferior

        startSamuraiAnimation();
        moveSamurai();

        return rootView;
    }

    private void startSamuraiAnimation() {
        // Cargar la animación desde el archivo XML
        samuraiAnimation.setBackgroundResource(R.drawable.idle_anim);
        // Obtener la animación como AnimationDrawable
        AnimationDrawable animation = (AnimationDrawable) samuraiAnimation.getBackground();
        // Mostrar el ImageView y comenzar la animación
        samuraiAnimation.setVisibility(View.VISIBLE);
        animation.start();
    }

    //TODO: Hacer que el samurái se mueva izq y voltear, gravity
    private void moveSamurai() {
        new Thread(() -> {
            while (isRunning) {
                samuraiAnimation.post(() -> {
                    samuraiAnimation.setX(samuraiAnimation.getX() + 5);
                    if (samuraiAnimation.getX() > getResources().getDisplayMetrics().widthPixels) {
                        samuraiAnimation.setX(-samuraiAnimation.getWidth());
                    }
                });

                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isRunning = false; // Detener el hilo cuando el Fragment se destruya
    }
}

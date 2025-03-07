package com.example.samurai;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GameFragment extends Fragment {
    private Samurai samurai;
    private ImageView samuraiAnimation;
    private boolean isRunning = true;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean isAttacking = false;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_layout, container, false);
        samuraiAnimation = rootView.findViewById(R.id.samuraiAnimation);

        // Obtener dimensiones de la pantalla
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Crear el objeto Samurai con las dimensiones de la pantalla
        samurai = new Samurai(requireContext(), screenWidth, screenHeight);

        // Configurar la posición inicial del samurái en la mitad inferior de la pantalla
        samuraiAnimation.setX(samurai.getX()); // Posición X centrada
        samuraiAnimation.setY(samurai.getY()); // Posición Y en la parte inferior

        ImageButton btnLeft = rootView.findViewById(R.id.btnLeft);
        ImageButton btnRight = rootView.findViewById(R.id.btnRight);
        Button btnAttack = rootView.findViewById(R.id.btnAttack);

        btnAttack.setOnClickListener(v -> {
            if (!isAttacking) { // Solo ejecutar si no hay una animación de ataque en progreso
                isAttacking = true; // Marcar que la animación de ataque está en progreso

                // Cambiar la animación del samurái a "ataque"
                setSamuraiAnimation(samurai.getAttackAnimation());

                // Calcular la duración total de la animación de ataque
                int totalDuration = 0;
                AnimationDrawable attackAnimation = samurai.getAttackAnimation();
                for (int i = 0; i < attackAnimation.getNumberOfFrames(); i++) {
                    totalDuration += attackAnimation.getDuration(i);
                }

                // Usar un Handler para volver a la animación correcta después de que termine el ataque
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (movingLeft || movingRight) {
                        // Si el samurái está en movimiento, volver a la animación de correr
                        setSamuraiAnimation(samurai.getRunAnimation());

                    } else {
                        // Si no está en movimiento, volver a la animación idle
                        setSamuraiAnimation(samurai.getIdleAnimation());
                    }
                    isAttacking = false; // Marcar que la animación de ataque ha terminado
                }, totalDuration);
            }
        });
        btnLeft.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    movingLeft = true;
                    samuraiAnimation.setScaleX(-1.5f); // Voltear a la izquierda
                    setSamuraiAnimation(samurai.getRunAnimation()); // Cambiar a animación de caminar
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    movingLeft = false;
                    setSamuraiAnimation(samurai.getIdleAnimation()); // Cambiar a animación idle
                    v.performClick();
                    break;
            }
            return true;
        });

        btnRight.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    movingRight = true;
                    samuraiAnimation.setScaleX(1.5f); // Voltear a la derecha
                    setSamuraiAnimation(samurai.getRunAnimation()); // Cambiar a animación de caminar
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    movingRight = false;
                    setSamuraiAnimation(samurai.getIdleAnimation()); // Cambiar a animación idle
                    v.performClick();
                    break;
            }
            return true;
        });

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

    private void setSamuraiAnimation(AnimationDrawable animation) {
        samuraiAnimation.setBackground(animation);
        animation.stop();
        animation.start();
    }

    private void moveSamurai() {
        new Thread(() -> {
            while (isRunning) {
                samuraiAnimation.post(() -> {
                    if (movingLeft) {
                        samuraiAnimation.setX(samuraiAnimation.getX() - 5);
                        if (samuraiAnimation.getX() + samuraiAnimation.getWidth() < 0) {
                            samuraiAnimation.setX(getResources().getDisplayMetrics().widthPixels);
                        }
                    } else if (movingRight) {
                        samuraiAnimation.setX(samuraiAnimation.getX() + 5);
                        if (samuraiAnimation.getX() > getResources().getDisplayMetrics().widthPixels) {
                            samuraiAnimation.setX(-samuraiAnimation.getWidth());
                        }
                    }
                });

                try {
                    Thread.sleep(16); // ~60 FPS
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
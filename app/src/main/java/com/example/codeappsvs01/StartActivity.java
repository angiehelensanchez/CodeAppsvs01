package com.example.codeappsvs01;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isMusicPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//+***************************************************************************************
        MediaPlayer botonSound = MediaPlayer.create(this, R.raw.pulsar_boton);
//+***************************************************************************************


        // Inicializar el reproductor de música
        mediaPlayer = MediaPlayer.create(this, R.raw.melodia_fondo);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // Obtener referencias de las vistas
        final EditText playerNameEditText = findViewById(R.id.playerName);
        final EditText coinAmountEditText = findViewById(R.id.coinAmount);
        Button startGameButton = findViewById(R.id.startGameButton);
        Button musicToggleButton = findViewById(R.id.musicToggleButton); // Botón para activar/desactivar la música

        // Configurar el botón para iniciar el juego
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //***********************************************************
                botonSound.start(); // Reproduce el sonido al hacer clic en el botón
                //***********************************************************
                String playerName = playerNameEditText.getText().toString();
                int coinAmount;
                try {
                    coinAmount = Integer.parseInt(coinAmountEditText.getText().toString());
                } catch (NumberFormatException e) {
                    coinAmount = 0; // O maneja el error como prefieras
                }

                // Crear intent y pasar datos a la actividad principal
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra("PLAYER_NAME", playerName);
                intent.putExtra("COIN_AMOUNT", coinAmount);
                startActivity(intent);
            }
        });

        // Configurar el botón para activar/desactivar la música
        musicToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //***********************************************************
                botonSound.start(); // Reproduce el sonido al hacer clic en el botón
                //***********************************************************
                if (isMusicPlaying) {
                    // Pausar la música si está reproduciéndose
                    mediaPlayer.pause();
                    isMusicPlaying = false;
                    musicToggleButton.setText(R.string.music_off); // Cambiar el texto del botón a "Música Desactivada"
                } else {
                    // Reanudar la música si está pausada
                    mediaPlayer.start();
                    isMusicPlaying = true;
                    musicToggleButton.setText(R.string.music_on); // Cambiar el texto del botón a "Música Activada"
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar recursos del reproductor de música
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}


package com.example.codeappsvs01;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import pl.droidsonroids.gif.GifDrawable;

public class StartActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_MUSIC = 100;
    private MediaPlayer mediaPlayer;
    private boolean isMusicPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        final ImageView backgroundImageView = findViewById(R.id.backgroundImageView);

        try {
            // Cargar el GIF desde la carpeta res/raw
            InputStream inputStream = getResources().openRawResource(R.raw.videofondo2);
            GifDrawable gifDrawable = new GifDrawable(inputStream);

            // Establecer el GIF como fondo del ImageView
            backgroundImageView.setImageDrawable(gifDrawable);

            // Iniciar la animación del GIF automáticamente
            gifDrawable.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //+***************************************************************************************
        MediaPlayer botonSound = MediaPlayer.create(this, R.raw.pulsar_boton);
        //+***************************************************************************************

        // Inicializar el reproductor de música
        mediaPlayer = MediaPlayer.create(this, R.raw.melodia_fondo);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        final EditText playerNameEditText = findViewById(R.id.playerName);
        final EditText coinAmountEditText = findViewById(R.id.coinAmount);
        Button startGameButton = findViewById(R.id.startGameButton);
        Button musicToggleButton = findViewById(R.id.musicToggleButton); // Botón para activar/desactivar la música

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
                    coinAmount = 0; // Manejar el error de formato
                }

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
                // Reproducir el sonido al hacer clic en el botón
                botonSound.start();

                if (isMusicPlaying) {
                    // Pausar la música si está reproduciéndose
                    mediaPlayer.pause();
                    isMusicPlaying = false;
                    musicToggleButton.setText(R.string.music_off); // Cambiar el texto del botón a "Música OFF"
                } else {
                    // Reanudar la música si está pausada
                    mediaPlayer.start();
                    isMusicPlaying = true;
                    musicToggleButton.setText(R.string.music_on); // Cambiar el texto del botón a "Música ON"
                }
            }
        });

        // Configurar el botón para seleccionar música
        Button selectMusicButton = findViewById(R.id.selectMusicButton);
        selectMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear una intención para seleccionar archivos de audio
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*"); // Tipo de archivo: audio
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Iniciar la actividad para seleccionar un archivo de audio
                startActivityForResult(Intent.createChooser(intent, "Selecciona una melodía"), REQUEST_CODE_SELECT_MUSIC);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_MUSIC) {
            if (resultCode == RESULT_OK) {
                // El usuario concedió permiso y seleccionó un archivo de música
                if (data != null) {
                    // Obtener la URI del archivo de audio seleccionado
                    Uri selectedMusicUri = data.getData();

                    try {
                        // Reiniciar el MediaPlayer
                        if (mediaPlayer != null) {
                            mediaPlayer.reset();
                        } else {
                            mediaPlayer = new MediaPlayer();
                        }

                        mediaPlayer.setDataSource(this, selectedMusicUri);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        isMusicPlaying = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // Toast o un diálogo informando al usuario y ofreciendo otra opción.
                Toast.makeText(this, "Permiso denegado para acceder al archivo de música seleccionado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pausar la música si está reproduciéndose
        if (isMusicPlaying && mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reanudar la música si estaba en pausa y la reproducción estaba activa
        if (isMusicPlaying && mediaPlayer != null) {
            mediaPlayer.start();
        }
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
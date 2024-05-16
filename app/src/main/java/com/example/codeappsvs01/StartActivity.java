package com.example.codeappsvs01;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Locale;
import java.io.IOException;
import java.io.InputStream;
import pl.droidsonroids.gif.GifDrawable;
import android.Manifest;
import android.content.pm.PackageManager;

public class StartActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_MUSIC = 100;
    private static final int RC_SIGN_IN = 9001;

    private MediaPlayer mediaPlayer;
    private boolean isMusicPlaying = false;
    public String lenguage = "Español";
    Spinner spinner;
    public static final String[] Languages = {"Select Language", "English", "Spain", "中文"};
    private WebView webView;
    private Button toggleWebViewButton;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mAuth = FirebaseAuth.getInstance();
        configureGoogleSignIn();

        checkLocationPermission();

        webView = findViewById(R.id.webView2);
        webView.getSettings().setJavaScriptEnabled(true);
        toggleWebViewButton = findViewById(R.id.button2);
        toggleWebViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.getVisibility() == View.VISIBLE) {
                    webView.setVisibility(View.GONE);
                } else {
                    webView.setVisibility(View.VISIBLE);
                    loadWebView();
                }
            }
        });

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("UnsafeIntentLaunch")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = parent.getItemAtPosition(position).toString();
                if (selectedLang.equals("English")) {
                    setLocale("en");
                } else if (selectedLang.equals("Spain")) {
                    setLocale("es");
                } else if (selectedLang.equals("中文")) {
                    setLocale("zh");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final ImageView backgroundImageView = findViewById(R.id.backgroundImageView);
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.videofondo2);
            GifDrawable gifDrawable = new GifDrawable(inputStream);
            backgroundImageView.setImageDrawable(gifDrawable);
            gifDrawable.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaPlayer botonSound = MediaPlayer.create(this, R.raw.pulsar_boton);
        mediaPlayer = MediaPlayer.create(this, R.raw.melodia_fondo);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        final EditText playerNameEditText = findViewById(R.id.playerName);
        final EditText coinAmountEditText = findViewById(R.id.coinAmount);
        Button startGameButton = findViewById(R.id.startGameButton);
        Button googleSignInButton = findViewById(R.id.googleSignInButton);
        Button musicToggleButton = findViewById(R.id.musicToggleButton);
        musicToggleButton.setText(R.string.music_button_controller);

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botonSound.start();
                String playerName = playerNameEditText.getText().toString();
                int coinAmount;
                try {
                    coinAmount = Integer.parseInt(coinAmountEditText.getText().toString());
                } catch (NumberFormatException e) {
                    coinAmount = 0;
                }
                checkUserExists(playerName, coinAmount);
            }
        });

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        musicToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                botonSound.start();
                if (isMusicPlaying) {
                    mediaPlayer.pause();
                    isMusicPlaying = false;
                    musicToggleButton.setText(R.string.music_off);
                } else {
                    mediaPlayer.start();
                    isMusicPlaying = true;
                    musicToggleButton.setText(R.string.music_on);
                }
            }
        });

        Button selectMusicButton = findViewById(R.id.selectMusicButton);
        selectMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Selecciona una melodía"), REQUEST_CODE_SELECT_MUSIC);
            }
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            obtenerYGuardarUbicacion();
        }
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_SELECT_MUSIC) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri selectedMusicUri = data.getData();
                    try {
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
                Toast.makeText(this, "Permiso denegado para acceder al archivo de música seleccionado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                checkUserExists(user.getDisplayName(), 0);
                            }
                        } else {
                            Toast.makeText(StartActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserExists(String playerName, int coinAmount) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PlayerResults")
                .whereEqualTo("playerName", playerName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean userExists = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userExists = true;
                                break;
                            }
                            if (userExists) {
                                Toast.makeText(StartActivity.this, "Username already exists.", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                                intent.putExtra("PLAYER_NAME", playerName);
                                intent.putExtra("COIN_AMOUNT", coinAmount);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(StartActivity.this, "Error checking username.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isMusicPlaying && mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMusicPlaying && mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void setLocale(String languageCode) {
        Locale myLocale = new Locale(languageCode);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, StartActivity.class);
        finish();
        startActivity(refresh);
    }

    private void loadWebView() {
        webView.loadUrl("file:///android_asset/guia.html");
    }

    @SuppressLint("MissingPermission")
    private void obtenerYGuardarUbicacion() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LocationData locationData = new LocationData(location.getLatitude(), location.getLongitude(), System.currentTimeMillis());
                            FirebaseDatabaseHelper.getInstance().addLocationData(locationData);
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerYGuardarUbicacion();
            } else {
                Toast.makeText(this, "La funcionalidad de ubicación está limitada porque no se concedieron permisos", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

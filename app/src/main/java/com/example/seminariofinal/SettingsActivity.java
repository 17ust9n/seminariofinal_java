package com.example.seminariofinal; // <-- Debe decir EXACTAMENTE esto

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class SettingsActivity extends AppCompatActivity {

    // Componentes adaptados de la vista web
    private SwitchCompat switchEphemeralSession;
    private SwitchCompat switchReadReceipts;
    private SwitchCompat switchAppSounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbarSettings);

        // CORRECCIÓN PARA EL TÍTULO: Se asigna directamente al componente antes del soporte
        toolbar.setTitle("Ajustes y Seguridad");
        setSupportActionBar(toolbar);

        // Activa la flecha de retroceso física en la interfaz
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Refuerzo en caso de que el tema intente borrarlo
            getSupportActionBar().setTitle("Ajustes y Seguridad");
        }

        // Fuerza el color verde claro (#4CAF50) usando DrawableCompat
        Drawable navigationIcon = toolbar.getNavigationIcon();
        if (navigationIcon != null) {
            Drawable wrappedIcon = DrawableCompat.wrap(navigationIcon);
            DrawableCompat.setTintList(wrappedIcon, ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            toolbar.setNavigationIcon(wrappedIcon);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        // Vinculación de los componentes de la vista web
        switchEphemeralSession = findViewById(R.id.switchEphemeralSession);
        switchReadReceipts = findViewById(R.id.switchReadReceipts);
        switchAppSounds = findViewById(R.id.switchAppSounds);

        // Cargar ajustes almacenados
        loadSettings();

        // ==========================================
        // LISTENERS (Equivalentes a JS en la Web)
        // ==========================================

        SharedPreferences prefs = getSharedPreferences("starssenger_prefs", Context.MODE_PRIVATE);

        // 1. Sesión Efímera
        switchEphemeralSession.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("app_ephemeral_session", isChecked).apply();
            Toast.makeText(this, "Preferencia de sesión actualizada ⏳", Toast.LENGTH_SHORT).show();
        });

        // 2. Confirmación de Lectura
        switchReadReceipts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("app_read_receipts", isChecked).apply();
            Toast.makeText(this, "Confirmación de lectura modificada ✔️", Toast.LENGTH_SHORT).show();
        });

        // 3. Efectos de Sonido
        switchAppSounds.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("app_sounds", isChecked).apply();
            Toast.makeText(this, "Configuración de audio guardada 🔊", Toast.LENGTH_SHORT).show();
        });

        // 4. Botón de Restablecimiento de fábrica local (btnResetPreferences)
        findViewById(R.id.btnResetPreferences).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Restaurar ajustes")
                    .setMessage("¿Querés restaurar los ajustes por defecto de la aplicación?")
                    .setPositiveButton("Sí, restaurar", (dialog, which) -> {
                        // Valores por defecto
                        prefs.edit()
                                .putBoolean("app_ephemeral_session", false)
                                .putBoolean("app_read_receipts", true)
                                .putBoolean("app_sounds", true)
                                .apply();

                        // Actualizar la UI en consecuencia
                        switchEphemeralSession.setChecked(false);
                        switchReadReceipts.setChecked(true);
                        switchAppSounds.setChecked(true);

                        Toast.makeText(this, "Ajustes restaurados por defecto 🔄", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences("starssenger_prefs", Context.MODE_PRIVATE);

        // Cargas iniciales mapeando los valores por defecto del LocalStorage
        switchEphemeralSession.setChecked(prefs.getBoolean("app_ephemeral_session", false)); // false por defecto
        switchReadReceipts.setChecked(prefs.getBoolean("app_read_receipts", true));         // true por defecto
        switchAppSounds.setChecked(prefs.getBoolean("app_sounds", true));                 // true por defecto
    }
}

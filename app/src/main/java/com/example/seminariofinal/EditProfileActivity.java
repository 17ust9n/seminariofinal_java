package com.example.seminariofinal; // <-- Se mantiene EXACTAMENTE tu paquete

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etUserPhone;
    private EditText etPubKeyDisplay;
    private MaterialButtonToggleGroup toggleGroupSecurity;
    private MaterialButton btnSecNormal;
    private MaterialButton btnSecBlindado;

    private int currentSecureMode = 0; // 0: Normal, 1: Blindado
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        prefs = getSharedPreferences("starssenger_prefs", Context.MODE_PRIVATE);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbarEditProfile);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Campos e Inputs
        etUserPhone = findViewById(R.id.etUserPhone);
        etPubKeyDisplay = findViewById(R.id.etPubKeyDisplay);
        toggleGroupSecurity = findViewById(R.id.toggleGroupSecurity);
        btnSecNormal = findViewById(R.id.btnSecNormal);
        btnSecBlindado = findViewById(R.id.btnSecBlindado);

        // Cargar número actual e inicializar modo seguro
        etUserPhone.setText(prefs.getString("user_phone", ""));
        currentSecureMode = prefs.getInt("sp_secure", 0);
        setupSecurityToggle(currentSecureMode);

        // Inicializar criptografía / Mostrar clave pública
        initCryptoKeys();

        // Controladores de Eventos (Listeners)
        toggleGroupSecurity.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnSecNormal) {
                    currentSecureMode = 0;
                } else if (checkedId == R.id.btnSecBlindado) {
                    currentSecureMode = 1;
                }
            }
        });

        findViewById(R.id.btnCopyKey).setOnClickListener(v -> copyPublicKey());
        findViewById(R.id.btnSavePhone).setOnClickListener(v -> saveProfile());
    }

    private void initCryptoKeys() {
        String pubKey = prefs.getString("sp_pubkey", null);
        String privKey = prefs.getString("sp_privkey", null);

        // Si no existen llaves registradas, las generamos automáticamente en curva X25519
        if (pubKey == null || privKey == null) {
            try {
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("X25519");
                KeyPair kp = kpg.generateKeyPair();

                pubKey = Base64.encodeToString(kp.getPublic().getEncoded(), Base64.NO_WRAP).trim();
                privKey = Base64.encodeToString(kp.getPrivate().getEncoded(), Base64.NO_WRAP).trim();

                prefs.edit()
                        .putString("sp_pubkey", pubKey)
                        .putString("sp_privkey", privKey)
                        .apply();

            } catch (Exception e) {
                etPubKeyDisplay.setText("Error al generar llaves");
                return;
            }
        }
        etPubKeyDisplay.setText(pubKey);
    }

    private void setupSecurityToggle(int mode) {
        if (mode == 1) {
            toggleGroupSecurity.check(R.id.btnSecBlindado);
        } else {
            toggleGroupSecurity.check(R.id.btnSecNormal);
        }
    }

    private void saveProfile() {
        String rawPhone = etUserPhone.getText().toString().trim();
        if (rawPhone.isEmpty()) {
            etUserPhone.setError("Ingresa un número válido");
            return;
        }

        // Limpiar caracteres no numéricos como hacía el código original JS
        String cleanPhone = rawPhone.replaceAll("\\D", "");

        prefs.edit()
                .putString("user_phone", cleanPhone)
                .putInt("sp_secure", currentSecureMode)
                .apply();

        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void copyPublicKey() {
        String pubKey = etPubKeyDisplay.getText().toString();
        if (!pubKey.isEmpty() && !pubKey.equals("Cargando clave...")) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Public Key", pubKey);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Clave pública copiada al portapapeles.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

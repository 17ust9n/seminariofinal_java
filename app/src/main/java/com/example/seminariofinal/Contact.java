package com.example.seminariofinal;
import com.goterl.lazysodium.utils.Key;

public class Contact {
    private String name;
    private String phone;
    private String publicKey;

    // 🛠️ NUEVO: Atributo para identificar si este registro mapea un Chat Grupal o una conversación Individual
    private boolean isGroup = false;

    // 🛠️ NUEVO: Constructor vacío obligatorio requerido por GSON para deserializar SharedPreferences de forma segura
    public Contact() {
    }

    // Constructor completo
    public Contact(String name, String phone, String publicKey) {
        this.name = name;
        this.phone = phone;
        this.publicKey = publicKey;
        this.isGroup = false; // Por defecto asumimos que es un contacto normal
    }

    // Constructor sobrecargado para retrocompatibilidad
    public Contact(String name, String phone) {
        this(name, phone, "");
    }

    // 🛠️ NUEVO: Constructor sobrecargado para inicializar grupos rápidamente
    public Contact(String name, String phone, boolean isGroup) {
        this.name = name;
        this.phone = phone;
        this.publicKey = "";
        this.isGroup = isGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    // 🛠️ NUEVO: Getter para controlar el flujo de clicks en MainActivity
    public boolean isGroup() {
        return isGroup;
    }

    // 🛠️ NUEVO: Setter para marcar un contacto temporal como sala grupal
    public void setGroup(boolean group) {
        isGroup = group;
    }

    public Key getSodiumPublicKey() {
        if (publicKey != null && !publicKey.trim().isEmpty()) {
            return Key.fromHexString(publicKey);
        }
        return null;
    }
}

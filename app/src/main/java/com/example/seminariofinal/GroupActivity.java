package com.example.seminariofinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private MaterialToolbar toolbarGroup;
    private TextView tvGroupDetailName, tvGroupDetailCount;
    private LinearLayout llGroupDetailMembers;

    private Group activeGroup = null;
    private final Gson gson = new Gson();
    private String myPhone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        toolbarGroup = findViewById(R.id.toolbarGroup);
        toolbarGroup.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbarGroup.setNavigationIconTint(
                ContextCompat.getColor(this, R.color.green_accent)
        );
        toolbarGroup.setNavigationOnClickListener(v -> finish());

        tvGroupDetailName = findViewById(R.id.tvGroupDetailName);
        tvGroupDetailCount = findViewById(R.id.tvGroupDetailCount);
        llGroupDetailMembers = findViewById(R.id.llGroupDetailMembers);

        String groupName = getIntent().getStringExtra("group_name");

        showGroup(groupName);
    }

    private void showGroup(String groupName) {
        if (llGroupDetailMembers == null) return;
        llGroupDetailMembers.removeAllViews();

        SharedPreferences preferences = getSharedPreferences("starssenger_prefs", Context.MODE_PRIVATE);
        myPhone = preferences.getString("user_phone", "");

        // 1. Cargamos el repositorio completo de grupos buscando coincidencia por nombre
        List<Group> savedGroups = new ArrayList<>();
        String json = preferences.getString("groups_list", null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<Group>>() {}.getType();
            List<Group> loaded = gson.fromJson(json, type);
            if (loaded != null) savedGroups.addAll(loaded);
        }

        for (Group g : savedGroups) {
            if (g != null && g.getName() != null && g.getName().equals(groupName)) {
                activeGroup = g;
                break;
            }
        }

        tvGroupDetailName.setText(
                groupName != null && !groupName.isEmpty() ? groupName : "Grupo sin nombre"
        );

        LayoutInflater inflater = LayoutInflater.from(this);

        // 2. Inyectamos siempre al usuario operador ("Vos")
        if (myPhone != null && !myPhone.isEmpty()) {
            addCurrentUser(inflater, myPhone);
        }

        // 3. 🛠️ CAMBIO CLAVE: Listamos ÚNICAMENTE a los miembros reales que ya forman parte de la sala
        if (activeGroup != null && activeGroup.getMembers() != null) {
            for (Contact member : activeGroup.getMembers()) {
                if (member == null) continue;

                // Omitimos duplicar la fila de "Vos" si tu número ya figuraba en la lista interna
                if (myPhone != null && member.getPhone() != null && member.getPhone().equals(myPhone)) {
                    continue;
                }

                // Agrega el miembro de manera puramente informativa
                addStaticMember(inflater, member);
            }
        }

        // 4. Actualizamos el contador numérico de integrantes en tiempo real
        updateMemberCount();
    }

    private void updateMemberCount() {
        int total = 0;
        if (myPhone != null && !myPhone.isEmpty()) {
            total++; // Sumamos a "Vos"
        }
        if (activeGroup != null && activeGroup.getMembers() != null) {
            total += activeGroup.getMembers().size();
        }
        tvGroupDetailCount.setText(total + " miembro(s)");
    }

    private void addCurrentUser(LayoutInflater inflater, String myPhone) {
        View row = inflater.inflate(
                R.layout.item_group_member,
                llGroupDetailMembers,
                false
        );

        TextView tvAvatar = row.findViewById(R.id.tvMemberAvatar);
        TextView tvName = row.findViewById(R.id.tvMemberName);
        TextView tvPhone = row.findViewById(R.id.tvMemberPhone);
        CheckBox cb = row.findViewById(R.id.cbMember);

        // Nombre propio guardado en el onboarding (fallback si no existe)
        SharedPreferences prefs = getSharedPreferences("starssenger_prefs", Context.MODE_PRIVATE);
        String myName = prefs.getString("user_name", "");
        String displayName = myName.isEmpty() ? "Yo" : myName;

        tvAvatar.setText(displayName.substring(0, 1).toUpperCase());
        tvName.setText(displayName);
        tvPhone.setText(myPhone);

        // Badge "Vos" creado por código
        float density = getResources().getDisplayMetrics().density;

        android.graphics.drawable.GradientDrawable badgeStyle = new android.graphics.drawable.GradientDrawable();
        badgeStyle.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        badgeStyle.setCornerRadius(12 * density);
        badgeStyle.setColor(ContextCompat.getColor(this, R.color.green_accent));

        TextView tvBadge = new TextView(this);
        tvBadge.setText("Vos");
        tvBadge.setTextSize(12);
        tvBadge.setTypeface(tvBadge.getTypeface(), android.graphics.Typeface.BOLD);
        tvBadge.setTextColor(android.graphics.Color.parseColor("#04240f"));
        tvBadge.setBackground(badgeStyle);
        int ph = (int) (10 * density);
        int pv = (int) (3 * density);
        tvBadge.setPadding(ph, pv, ph, pv);

        // Reemplazamos tvName por un contenedor horizontal: [nombre] [Vos]
        ViewGroup parent = (ViewGroup) tvName.getParent();
        int index = parent.indexOfChild(tvName);
        ViewGroup.LayoutParams originalParams = tvName.getLayoutParams();
        parent.removeView(tvName);

        LinearLayout nameRow = new LinearLayout(this);
        nameRow.setOrientation(LinearLayout.HORIZONTAL);
        nameRow.setGravity(android.view.Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        badgeParams.setMarginStart((int) (8 * density));

        nameRow.addView(tvName, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        nameRow.addView(tvBadge, badgeParams);
        parent.addView(nameRow, index, originalParams);

        if (cb != null) cb.setVisibility(View.GONE);

        row.setOnClickListener(v -> {
            Intent intent = new Intent(GroupActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        llGroupDetailMembers.addView(row);
    }

    /**
     * 🛠️ NUEVO MÉTODO ESTÁTICO INFORMATIVO: Muestra el integrante sin caja de tildes ni interactividad.
     */
    private void addStaticMember(LayoutInflater inflater, Contact c) {
        View row = inflater.inflate(
                R.layout.item_group_member,
                llGroupDetailMembers,
                false
        );

        TextView tvAvatar = row.findViewById(R.id.tvMemberAvatar);
        TextView tvName = row.findViewById(R.id.tvMemberName);
        TextView tvPhone = row.findViewById(R.id.tvMemberPhone);
        CheckBox cb = row.findViewById(R.id.cbMember);

        String name = c.getName() != null ? c.getName() : "";
        String phone = c.getPhone() != null ? c.getPhone() : "";

        if (tvAvatar != null) tvAvatar.setText(name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase());
        if (tvName != null) tvName.setText(name.isEmpty() ? "Contacto" : name);
        if (tvPhone != null) tvPhone.setText(phone);

        // 🛠️ REMOVIDO: Se limpia el checkbox para que actúe de celda informativa pura
        if (cb != null) {
            cb.setVisibility(View.GONE);
        }

        // Al hacer click, redirige directo a su sala de chat individual clásica
        row.setOnClickListener(v -> {
            Intent intent = new Intent(GroupActivity.this, ChatActivity.class);
            intent.putExtra("contact_name", name);
            intent.putExtra("contact_phone", phone);
            intent.putExtra("contact_public_key", c.getPublicKey());
            intent.putExtra("is_group", false);
            startActivity(intent);
        });

        llGroupDetailMembers.addView(row);
    }
}

package com.example.seminariofinal;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter
        extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private final List<Contact> contactList = new ArrayList<>();
    private final OnContactActionListener listener;
    private final boolean showActions;

    public ContactAdapter(
            List<Contact> initialList,
            boolean showActions,
            OnContactActionListener listener
    ) {
        if (initialList != null) {
            this.contactList.addAll(initialList);
        }

        this.showActions = showActions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        // Inflamos la plantilla de diseño correspondiente
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_member, parent, false);

        return new ContactViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        // 🛠️ CORREGIDO: Se usa contactList que es tu colección real definida arriba
        Contact contact = contactList.get(position);
        Context ctx = holder.itemView.getContext();

        holder.tvName.setText(contact.getName());

        // 1. CONFIGURACIÓN VISUAL DEL AVATAR (Burbuja verde para grupos, diseño común para personas)
        if (contact.isGroup()) {
            holder.tvAvatar.setText("👥");
            holder.tvAvatar.setTextSize(18);
            holder.tvAvatar.setTextColor(Color.parseColor("#04240f"));

            GradientDrawable circleGreen = new GradientDrawable();
            circleGreen.setShape(GradientDrawable.OVAL);
            circleGreen.setColor(ContextCompat.getColor(ctx, R.color.green_accent));
            holder.tvAvatar.setBackground(circleGreen);

            if (holder.tvPhone != null) {
                holder.tvPhone.setText("Sala de chat grupal");
                holder.tvPhone.setTextColor(Color.parseColor("#8aa0b3"));
            }
        } else {
            String name = contact.getName() != null ? contact.getName() : "";
            holder.tvAvatar.setText(name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase());
            holder.tvAvatar.setTextSize(16);
            holder.tvAvatar.setTextColor(ContextCompat.getColor(ctx, R.color.green_accent));
            holder.tvAvatar.setBackground(null);

            if (holder.tvPhone != null) {
                holder.tvPhone.setText(contact.getPhone());
                holder.tvPhone.setTextColor(ContextCompat.getColor(ctx, android.R.color.darker_gray));
            }
        }

        // 2. 🛠️ INYECCIÓN DINÁMICA HORIZONTAL AL LADO DEL NOMBRE (✎ y ×)
        LinearLayout actionsLayout = holder.itemView.findViewById(R.id.llActionsContainer);
        if (actionsLayout != null) {
            actionsLayout.removeAllViews(); // Limpieza crítica para evitar duplicación al reciclar

            // 🛠️ CORREGIDO: Se utiliza showActions que es tu flag booleana del constructor
            if (showActions) {
                // Botón Modificar Contacto (✎)
                Button btnEdit = new Button(ctx);
                btnEdit.setText("✎");
                btnEdit.setTextSize(18);
                btnEdit.setTextColor(ContextCompat.getColor(ctx, R.color.green_accent));
                btnEdit.setBackground(null);
                btnEdit.setPadding(12, 0, 12, 0);
                btnEdit.setOnClickListener(v -> {
                    if (listener != null) listener.onEdit(contact, position);
                });

                // Botón Eliminar Contacto (×)
                Button btnDelete = new Button(ctx);
                btnDelete.setText("×");
                btnDelete.setTextSize(22);
                btnDelete.setTextColor(Color.parseColor("#e05a5a"));
                btnDelete.setBackground(null);
                btnDelete.setPadding(12, 0, 12, 0);
                btnDelete.setOnClickListener(v -> {
                    if (listener != null) listener.onDelete(contact, position);
                });

                actionsLayout.addView(btnEdit);
                actionsLayout.addView(btnDelete);
            }
        }

        // Click en la fila abre la conversación
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onContactClick(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void updateList(List<Contact> newList) {
        List<Contact> safeNewList = newList != null ? newList : new ArrayList<>();

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new ContactDiffCallback(contactList, safeNewList)
        );

        contactList.clear();
        contactList.addAll(safeNewList);
        diffResult.dispatchUpdatesTo(this);
    }

    // Clase contenedora ViewHolder adaptada a tus IDs de componentes
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvPhone;
        private final TextView tvAvatar;
        private final ImageView ivKeyBadge;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            // 🛠️ CORREGIDO: Enlazamos con los IDs que usa tu adaptador de Android Studio
            tvName = itemView.findViewById(R.id.tvMemberName);
            tvPhone = itemView.findViewById(R.id.tvMemberPhone);
            tvAvatar = itemView.findViewById(R.id.tvMemberAvatar);
            ivKeyBadge = itemView.findViewById(R.id.ivKeyBadge);
        }
    }
}

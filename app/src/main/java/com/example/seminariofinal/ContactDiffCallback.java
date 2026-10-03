package com.example.seminariofinal;

import androidx.recyclerview.widget.DiffUtil;
import java.util.List;
import java.util.Objects;

public class ContactDiffCallback extends DiffUtil.Callback {

    private final List<Contact> oldList;
    private final List<Contact> newList;

    public ContactDiffCallback(List<Contact> oldList, List<Contact> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Contact oldContact = oldList.get(oldItemPosition);
        Contact iContact = newList.get(newItemPosition);

        // Comparamos por teléfono o por nombre si es un grupo sin número único
        return Objects.equals(oldContact.getPhone(), iContact.getPhone()) &&
                Objects.equals(oldContact.getName(), iContact.getName());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Contact oldContact = oldList.get(oldItemPosition);
        Contact iContact = newList.get(newItemPosition);

        // Verifica si cambió algún atributo interno para redibujar la fila en caliente
        return Objects.equals(oldContact.getName(), iContact.getName()) &&
                Objects.equals(oldContact.getPhone(), iContact.getPhone()) &&
                Objects.equals(oldContact.getPublicKey(), iContact.getPublicKey()) &&
                oldContact.isGroup() == iContact.isGroup();
    }
}

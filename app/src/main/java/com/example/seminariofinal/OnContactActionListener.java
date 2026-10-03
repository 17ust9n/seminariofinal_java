package com.example.seminariofinal;

public interface OnContactActionListener {
    void onContactClick(Contact contact);
    void onEdit(Contact contact, int position);
    void onDelete(Contact contact, int position);
}
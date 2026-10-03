package com.example.seminariofinal;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso único a la lista de grupos guardada en SharedPreferences.
 * Usalo desde NewChatActivity, MainActivity o donde necesites leer los grupos.
 */
public class GroupRepository {

    private static final String PREFS = "starssenger_prefs";
    private static final String KEY = "groups_list";
    private static final Gson gson = new Gson();

    public static List<Group> load(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY, null);
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<List<Group>>() {}.getType();
        List<Group> list = gson.fromJson(json, type);
        return (list != null) ? list : new ArrayList<>();
    }

    public static void save(Context context, List<Group> groups) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY, gson.toJson(groups))
                .apply();
    }
}
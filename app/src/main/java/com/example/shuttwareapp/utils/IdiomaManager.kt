package com.example.shuttwareapp.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

//Object para cambiar el idioma de la aplicacion por medio de selector en inicio
object IdiomaManager {
    private const val PREF = "shuttware_prefs"
    private const val KEY = "idioma"

    //Guardar la seleccion de idioma en las preferencias
    fun guardar(context: Context, codigo: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, codigo)
            .apply()
    }

    //Obtener el idioma guardado
    fun obtener(context: Context): String {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY, "es") ?: "es"
    }

    //Aplicamos el idioma a toda la aplicacion
    fun aplicar(context: Context, codigo: String) {
        val locale = Locale(codigo)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
package com.suraksha.app.utility

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.suraksha.app.presentation.helpline.Helpline
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri

fun loadHelplines(context: Context): List<Helpline> {
    val json = context.assets.open("helpline.json").bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Helpline>>() {}.type
    return Gson().fromJson(json, type)
}

fun dialNumber(context: Context, number: String) {
    val intent = Intent(Intent.ACTION_DIAL, "tel:$number".toUri())
    context.startActivity(intent)
}

fun openWhatsApp(context: Context, number: String) {
    try {
        val formatted = number.replace("+", "").trim()
        val uri = "https://wa.me/$formatted".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
    }
}

fun openWebsite(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Helpline Number", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Number copied!", Toast.LENGTH_SHORT).show()
}

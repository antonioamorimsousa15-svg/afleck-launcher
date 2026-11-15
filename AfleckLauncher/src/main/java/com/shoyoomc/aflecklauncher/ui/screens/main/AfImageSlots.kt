package com.shoyoomc.aflecklauncher.ui.screens.main

import android.net.Uri
import java.io.File
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.shoyoomc.aflecklauncher.R
import coil3.compose.AsyncImage
import kotlin.math.roundToInt

/** Uma "vaga" de imagem ou vídeo da tela (mídia escolhida pelo usuário + opacidade), salva no aparelho. */
class AfImageSlot(private val prefs: SharedPreferences, val key: String) {
    var uri by mutableStateOf(prefs.getString("$key.uri", null))
        private set
    var isVideo by mutableStateOf(prefs.getBoolean("$key.video", false))
        private set
    var opacity by mutableIntStateOf(prefs.getInt("$key.opacity", 100))
        private set

    fun updateUri(value: String?, video: Boolean = false) {
        uri = value
        isVideo = value != null && video
        prefs.edit().apply {
            if (value == null) { remove("$key.uri"); remove("$key.video") }
            else { putString("$key.uri", value); putBoolean("$key.video", video) }
        }.apply()
    }

    fun updateOpacity(value: Int) {
        opacity = value.coerceIn(0, 100)
        prefs.edit().putInt("$key.opacity", opacity).apply()
    }
}

private val afSlotCache = HashMap<String, AfImageSlot>()

/** Uma única instância por chave: todas as telas veem a mesma imagem na hora. */
@Composable
fun rememberAfSlot(key: String): AfImageSlot {
    val ctx = LocalContext.current.applicationContext
    return remember(key) {
        synchronized(afSlotCache) {
            afSlotCache.getOrPut(key) { AfImageSlot(ctx.getSharedPreferences("afleck_ui", Context.MODE_PRIVATE), key) }
        }
    }
}

/** Copia a mídia escolhida para dentro do app (não depende de permissão do seletor). */
private fun copySlotMedia(ctx: Context, key: String, uri: Uri): Pair<String, Boolean>? = runCatching {
    val mime = ctx.contentResolver.getType(uri).orEmpty()
    val video = mime.startsWith("video/")
    val ext = when {
        mime.contains("gif") -> "gif"
        video -> "mp4"
        mime.contains("png") -> "png"
        mime.contains("webp") -> "webp"
        else -> "jpg"
    }
    val dir = File(ctx.filesDir, "af_slots").apply { mkdirs() }
    dir.listFiles()?.filter { it.name.startsWith("${key}_") }?.forEach { it.delete() }
    val out = File(dir, "${key}_${System.currentTimeMillis()}.$ext")
    ctx.contentResolver.openInputStream(uri)!!.use { input -> out.outputStream().use { input.copyTo(it) } }
    Uri.fromFile(out).toString() to video
}.getOrNull()

/** Mostra a imagem/GIF/vídeo escolhido (ou a imagem padrão, se houver) com a opacidade da vaga. */
@Composable
fun AfSlotImage(slot: AfImageSlot, defaultRes: Int?, modifier: Modifier = Modifier) {
    val a = slot.opacity / 100f
    val u = slot.uri
    if (u != null && slot.isVideo) {
        com.shoyoomc.aflecklauncher.ui.components.VideoPlayer(
            videoUri = Uri.parse(u),
            modifier = modifier.alpha(a),
            volume = 0f
        )
    } else if (u != null) {
        AsyncImage(model = u, contentDescription = null, contentScale = ContentScale.Crop, alpha = a, modifier = modifier)
    } else if (defaultRes != null) {
        Image(painterResource(defaultRes), null, contentScale = ContentScale.Crop, alpha = a, modifier = modifier)
    }
}

/** Fundo de um card: mídia escolhida cobrindo o card inteiro, com um véu escuro para o texto continuar legível. */
@Composable
fun BoxScope.AfCardBg(slot: AfImageSlot) {
    if (slot.uri != null) {
        AfSlotImage(slot, null, Modifier.matchParentSize())
        Box(Modifier.matchParentSize().background(Color(0x55000000)))
    }
}

/** Janela "Escolher / Usar padrão / Opacidade / Fechar", como no Cryogenic. */
@Composable
fun AfSlotDialog(title: String, description: String, slot: AfImageSlot, onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var copying by remember { mutableStateOf(false) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            copying = true
            scope.launch {
                val r = withContext(Dispatchers.IO) { copySlotMedia(ctx, slot.key, uri) }
                if (r != null) slot.updateUri(r.first, r.second)
                copying = false
            }
        }
    }
    Dialog(onDismissRequest = onDismiss) {
        AfPanel(Modifier.widthIn(max = 460.dp), radius = 30.dp, deep = true) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(title, color = AfText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(description, color = AfDim, fontSize = 13.sp)
                Text(
                    if (copying) "Copiando…" else if (slot.uri == null) "Usando a imagem padrão do Afleck" else if (slot.isVideo) "Usando o vídeo escolhido" else "Usando a imagem escolhida",
                    color = AfAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AfButton("Escolher", Modifier.weight(1f), R.drawable.ic_image_filled) { picker.launch(arrayOf("image/*", "video/*")) }
                    Box(Modifier.weight(1f).alpha(if (slot.uri != null) 1f else 0.45f)) {
                        AfOutlineButton("Usar padrão", Modifier.fillMaxWidth()) { if (slot.uri != null) slot.updateUri(null) }
                    }
                }
                Text("Opacidade", color = AfText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Slider(
                        value = slot.opacity / 100f,
                        onValueChange = { slot.updateOpacity((it * 100f).roundToInt()) },
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = AfAccent, activeTrackColor = AfAccent, inactiveTrackColor = AfBorder.copy(alpha = 0.35f)
                        )
                    )
                    Spacer(Modifier.padding(start = 8.dp))
                    Text("${slot.opacity}%", color = AfText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                AfOutlineButton("Fechar", Modifier.fillMaxWidth(), onClick = onDismiss)
            }
        }
    }
}

package com.shoyoomc.aflecklauncher.ui.screens.main

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.shoyoomc.aflecklauncher.R

internal class AfShortcut(val id: String, val label: String, val icon: Int)

internal val AF_SHORTCUTS = listOf(
    AfShortcut("graficos", "Gráficos", R.drawable.ic_video_settings),
    AfShortcut("hud", "Layouts de HUD", R.drawable.ic_dashboard_filled),
    AfShortcut("aparencia", "Aparência", R.drawable.ic_package_2_outlined),
    AfShortcut("contas", "Contas", R.drawable.ic_person_filled),
    AfShortcut("versoes", "Versões", R.drawable.ic_folder_filled),
    AfShortcut("download", "Baixar", R.drawable.ic_public),
    AfShortcut("texturas", "Texturas", R.drawable.ic_styler),
    AfShortcut("shaders", "Shaders", R.drawable.ic_lightbulb),
    AfShortcut("config", "Configurações", R.drawable.ic_settings_filled),
)
private val AF_DEFAULT = listOf("graficos", "hud", "aparencia", "contas")

internal fun afLoadShortcuts(ctx: Context): List<String> {
    val raw = ctx.getSharedPreferences("afleck_ui", Context.MODE_PRIVATE).getString("home.shortcuts", null)
    return raw?.split(",")?.filter { it.isNotBlank() }?.take(6) ?: AF_DEFAULT
}

internal fun afSaveShortcuts(ctx: Context, ids: List<String>) {
    ctx.getSharedPreferences("afleck_ui", Context.MODE_PRIVATE).edit().putString("home.shortcuts", ids.joinToString(",")).apply()
}

@Composable
internal fun AfShortcutsDialog(current: List<String>, onSave: (List<String>) -> Unit, onDismiss: () -> Unit) {
    var sel by remember { mutableStateOf(current) }
    Dialog(onDismissRequest = onDismiss) {
        AfPanel(Modifier.fillMaxWidth().heightIn(max = 460.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Editar atalhos", color = AfText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Escolha até 6 atalhos. A ordem do toque é a ordem na tela.", color = AfDim, fontSize = 12.sp)
                    }
                    Text("${sel.size}/6", color = AfAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                LazyColumn(Modifier.weight(1f, fill = false), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(AF_SHORTCUTS) { sc ->
                        val idx = sel.indexOf(sc.id)
                        Row(
                            Modifier.fillMaxWidth().height(46.dp).afRect(AfBorder, selected = idx >= 0)
                                .afClick {
                                    sel = if (idx >= 0) sel - sc.id else if (sel.size < 6) sel + sc.id else sel
                                }.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(painterResource(sc.icon), null, tint = AfAccent, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(sc.label, color = AfText, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            if (idx >= 0) Text("${idx + 1}", color = AfAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AfOutlineButton("Padrão", Modifier.weight(1f)) { sel = AF_DEFAULT }
                    AfButton("Salvar", Modifier.weight(1f)) { onSave(sel); onDismiss() }
                }
            }
        }
    }
}

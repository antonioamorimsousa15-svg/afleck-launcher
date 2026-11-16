package com.shoyoomc.aflecklauncher.ui.screens.main

import com.shoyoomc.aflecklauncher.ui.screens.content.elements.VersionIconImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoyoomc.aflecklauncher.R
import com.shoyoomc.aflecklauncher.game.account.AccountsManager
import com.shoyoomc.aflecklauncher.game.version.installed.Version
import com.shoyoomc.aflecklauncher.game.version.installed.VersionsManager

/** Tela Início do Afleck, igual ao esqueleto desenhado pelo usuário. */
@Composable
fun AfHome(
    onPlay: (Version) -> Unit,
    onOpenVersions: () -> Unit,
    onOpenAccounts: () -> Unit,
    onOpenDownload: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAppearance: () -> Unit,
) {
    val slot916 = rememberAfSlot("card916")
    val homeSlot = rememberAfSlot("home")
    val accSlot = rememberAfSlot("acc")
    val versSlot = rememberAfSlot("vers")
    val mcvSlot = rememberAfSlot("mcv")
    val quickSlot = rememberAfSlot("quick")
    var dialog by remember { mutableStateOf<String?>(null) }

    Box(Modifier.fillMaxSize()) {
        AfHomeContent(
            onPlay, onOpenVersions, onOpenAccounts, onOpenDownload, onOpenSettings, onOpenAppearance,
            slot916, homeSlot, accSlot, versSlot, mcvSlot, quickSlot, onEdit = { dialog = it }
        )
    }
    when (dialog) {
        "home" -> AfCardDialog("Fundo de Bem-vindo", homeSlot) { dialog = null }
        "acc" -> AfCardDialog("Fundo de Conta", accSlot) { dialog = null }
        "vers" -> AfCardDialog("Fundo de Suas versões", versSlot) { dialog = null }
        "mcv" -> AfCardDialog("Fundo de Versão do Minecraft", mcvSlot) { dialog = null }
        "quick" -> AfCardDialog("Fundo de Configurações Rápidas", quickSlot) { dialog = null }
        "card916" -> AfCardDialog("Imagem 9:16", slot916) { dialog = null }
    }
}

@Composable
private fun AfCardDialog(title: String, slot: AfImageSlot, onDismiss: () -> Unit) {
    AfSlotDialog(title, "Imagem, GIF ou vídeo para o fundo deste quadro. A opacidade daqui é separada das outras.", slot, onDismiss)
}

@Composable
private fun AfHomeContent(
    onPlay: (Version) -> Unit,
    onOpenVersions: () -> Unit,
    onOpenAccounts: () -> Unit,
    onOpenDownload: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAppearance: () -> Unit,
    slot916: AfImageSlot,
    homeSlot: AfImageSlot,
    accSlot: AfImageSlot,
    versSlot: AfImageSlot,
    mcvSlot: AfImageSlot,
    quickSlot: AfImageSlot,
    onEdit: (String) -> Unit,
) {
    val account by AccountsManager.currentAccountFlow.collectAsState()
    val current by VersionsManager.currentVersion.collectAsState()
    val allVersions by VersionsManager.versions.collectAsState()
    val userName = account?.username

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ---------- Linha de cima ----------
        Row(Modifier.fillMaxWidth().height(190.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Bem-vindo de volta
            AfPanel(Modifier.weight(2.5f).fillMaxHeight()) {
                AfCardBg(homeSlot)
                Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_package_2_outlined), null, tint = AfAccent, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Bem-vindo de volta,", color = AfText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        userName ?: "Jogador", color = AfAccent, fontSize = 30.sp, fontWeight = FontWeight.Bold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text("Sua aventura congelada continua aqui.", color = AfText, fontSize = 13.sp)
                    if (current != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val loaderName = runCatching {
                                current?.getVersionInfo()?.primaryLoader?.loader?.name
                                    ?.lowercase()?.replaceFirstChar { it.uppercase() }
                            }.getOrNull()
                            listOfNotNull(current?.getVersionName(), loaderName).forEach { chip ->
                                Text(
                                    chip, color = AfAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1,
                                    modifier = Modifier.afGlow(CircleShape, AfAccent, 0.6f).padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (current != null) {
                            AfButton("Jogar Agora", Modifier.weight(1f), R.drawable.ic_play_arrow_filled) { current?.let(onPlay) }
                        } else {
                            AfButton("BAIXAR MINECRAFT", Modifier.weight(1f), R.drawable.ic_download_2_filled, onOpenDownload)
                        }
                        Box(Modifier.afClick { onEdit("home") }) { AfIconCircle(R.drawable.ic_image_filled, size = 50.dp) }
                    }
                }
            }
            // Versão do Minecraft
            AfPanel(Modifier.weight(1.6f).fillMaxHeight()) {
                AfCardBg(mcvSlot)
                Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_sports_esports_outlined), null, tint = AfAccent, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Versão do Minecraft", color = AfText, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, modifier = Modifier.weight(1f))
                        Box(Modifier.afClick { onEdit("mcv") }) { AfIconCircle(R.drawable.ic_image_filled, size = 30.dp) }
                    }
                    Row(Modifier.weight(1f).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        VersionIconImage(current, Modifier.size(46.dp))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(current?.getVersionName() ?: "Nenhuma", color = AfText, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(
                                current?.let { runCatching { it.getVersionSummary() }.getOrDefault("") } ?: "Escolha uma versão",
                                color = AfDim, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (current != null) AfButton("JOGAR", Modifier.weight(1f), R.drawable.ic_play_arrow_filled) { current?.let(onPlay) }
                        else AfButton("ESCOLHER", Modifier.weight(1f), null, onOpenVersions)
                        Box(Modifier.afClick(onOpenVersions)) { AfIconCircle(R.drawable.ic_settings_filled, size = 40.dp) }
                    }
                }
            }
        }
        // ---------- Linha do meio ----------
        Row(Modifier.fillMaxWidth().height(190.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Suas versões
            AfPanel(Modifier.weight(1.6f).fillMaxHeight()) {
                AfCardBg(versSlot)
                Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_package_2_outlined), null, tint = AfAccent, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Suas versões", color = AfText, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("Ver todas ›", color = AfAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.afClick(onOpenVersions))
                        Spacer(Modifier.width(6.dp))
                        Box(Modifier.afClick { onEdit("vers") }) { AfIconCircle(R.drawable.ic_image_filled, size = 30.dp) }
                    }
                    val first = allVersions.firstOrNull()
                    if (first == null) {
                        Text("Nenhuma versão instalada.", color = AfDim, fontSize = 13.sp)
                        Spacer(Modifier.weight(1f))
                        AfButton("Baixar", Modifier.fillMaxWidth(), null, onOpenDownload)
                    } else {
                        allVersions.take(2).forEach { v ->
                            val shape = AfCutShape(10.dp)
                            Row(
                                Modifier.fillMaxWidth().afRect(AfBorder, radius = 12.dp).padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VersionIconImage(v, Modifier.size(34.dp))
                                Spacer(Modifier.width(8.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(v.getVersionName(), color = AfText, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(runCatching { v.getVersionSummary() }.getOrDefault(""), color = AfDim, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Box(Modifier.afClick { onPlay(v) }) { AfIconCircle(R.drawable.ic_play_arrow_filled, size = 34.dp) }
                            }
                        }
                    }
                }
            }
            // Conta
            AfPanel(Modifier.weight(1.25f).fillMaxHeight()) {
                AfCardBg(accSlot)
                Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_person_filled), null, tint = AfAccent, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Conta", color = AfText, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Row(
                            Modifier.clip(CircleShape).background(AfAccent).afClick(onOpenAccounts)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(painterResource(R.drawable.ic_checkroom), null, tint = AfNavy, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Skins", color = AfNavy, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(6.dp))
                        Box(Modifier.afClick { onEdit("acc") }) { AfIconCircle(R.drawable.ic_image_filled, size = 30.dp) }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(54.dp).clip(CircleShape).background(AfNavy).border(2.dp, AfBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (userName == null) {
                                Icon(painterResource(R.drawable.ic_add), null, tint = AfText, modifier = Modifier.size(28.dp))
                            } else {
                                Text(userName.take(1).uppercase(), color = AfAccent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                userName ?: "Adicionar Conta", color = AfText, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                            Text(if (userName == null) "● Nenhuma conta" else "● Conta", color = AfDim, fontSize = 11.sp)
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    AfOutlineButton("Gerenciar Conta", Modifier.fillMaxWidth(), R.drawable.ic_settings_filled, onOpenAccounts)
                }
            }
            // Imagem 9:16 (plano de fundo)
            Box(
                Modifier.weight(1f).fillMaxHeight().clip(AfCutShape(16.dp))
                    .border(2.dp, AfBorder, AfCutShape(16.dp))
            ) {
                AfSlotImage(slot916, R.drawable.af_bg, Modifier.fillMaxSize())
                Box(Modifier.align(Alignment.TopEnd).padding(8.dp).afClick { onEdit("card916") }) {
                    AfIconCircle(R.drawable.ic_image_filled, size = 40.dp)
                }
                Text(
                    "9:16", color = AfText, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.BottomStart).padding(10.dp).clip(AfCutShape(8.dp))
                        .background(AfNavy.copy(alpha = 0.85f)).padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
        }
        // ---------- Atalhos ----------
            AfPanel(Modifier.fillMaxWidth().height(150.dp)) {
            AfCardBg(quickSlot)
            Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.ic_settings_filled), null, tint = AfAccent, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Configurações Rápidas", color = AfText, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, modifier = Modifier.weight(1f))
                    Box(Modifier.afClick { onEdit("quick") }) { AfIconCircle(R.drawable.ic_edit_outlined, size = 34.dp) }
                }
                Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickTile(Modifier.weight(1f), R.drawable.ic_video_settings, "Gráficos", onOpenSettings)
                    QuickTile(Modifier.weight(1f), R.drawable.ic_dashboard_filled, "Layouts de HUD", onOpenSettings)
                    QuickTile(Modifier.weight(1f), R.drawable.ic_package_2_outlined, "Aparência", onOpenAppearance)
                    QuickTile(Modifier.weight(1f), R.drawable.ic_person_filled, "Contas", onOpenAccounts)
                }
            }
        }
        }
    }
}

@Composable
private fun QuickTile(modifier: Modifier, icon: Int, label: String, onClick: () -> Unit) {
    Column(
        modifier = modifier.fillMaxHeight().afRect(AfAccent, radius = 14.dp).afClick(onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(painterResource(icon), null, tint = AfAccent, modifier = Modifier.size(22.dp))
        Text(label, color = AfText, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, textAlign = TextAlign.Center)
    }
}

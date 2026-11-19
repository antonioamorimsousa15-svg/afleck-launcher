package com.shoyoomc.aflecklauncher.ui.screens.main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoyoomc.aflecklauncher.R

/** Barra de navegação embaixo (centralizada): Início, Versões, Download, Contas, Ajustes. */
@Composable
fun AfSidebar(
    inHome: Boolean,
    inVersions: Boolean,
    inDownload: Boolean,
    inAccounts: Boolean,
    inSettings: Boolean,
    inAppearance: Boolean,
    onHome: () -> Unit,
    onVersions: () -> Unit,
    onDownload: () -> Unit,
    onAccounts: () -> Unit,
    onSettings: () -> Unit,
    onAppearance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(40.dp)
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier
                .widthIn(max = 900.dp)
                .fillMaxWidth()
                .afGlassSurface(shape)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavPill(R.drawable.ic_home_filled, "Início", inHome, onHome)
            NavPill(R.drawable.ic_sports_esports_outlined, "Versões", inVersions, onVersions)
            NavPill(R.drawable.ic_download_2_filled, "Download", inDownload, onDownload)
            NavPill(R.drawable.ic_person_filled, "Contas", inAccounts, onAccounts)
            NavPill(R.drawable.ic_settings_filled, "Ajustes", inSettings, onSettings)
        }
    }
}

@Composable
private fun RowScope.NavPill(icon: Int, label: String, selected: Boolean, onClick: () -> Unit) {
    val color = if (selected) AfAccent else AfBorder
    Row(
        modifier = Modifier.weight(1f).height(58.dp)
            .afDarkGlass(color, selected, 29.dp)
            .afClick(onClick)
            .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp)
                .clip(CircleShape).border(1.2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(icon), null, tint = AfText, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(8.dp))
        Text(label, color = AfText, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

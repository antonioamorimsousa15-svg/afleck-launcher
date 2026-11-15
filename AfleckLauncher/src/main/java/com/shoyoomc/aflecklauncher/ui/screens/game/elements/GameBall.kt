/*
 * Zalith Launcher 2
 * Copyright (C) 2025-2026 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.shoyoomc.aflecklauncher.ui.screens.game.elements

import com.shoyoomc.aflecklauncher.ui.screens.main.AfCutShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import com.shoyoomc.aflecklauncher.ui.screens.main.AfAccent
import com.shoyoomc.aflecklauncher.ui.screens.main.AfNavy
import com.shoyoomc.aflecklauncher.ui.screens.main.AfText
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shoyoomc.aflecklauncher.R
import com.shoyoomc.aflecklauncher.setting.enums.FpsDisplayMode
import com.shoyoomc.aflecklauncher.ui.components.FloatingBall
import com.shoyoomc.aflecklauncher.ui.screens.content.elements.MemoryPreview

@Composable
fun DraggableGameBall(
    position: Offset,
    onPositionChanged: (Offset) -> Unit,
    onSavePos: () -> Unit,
    gameFps: () -> Int?,
    fpsDisplayMode: FpsDisplayMode,
    fpsHistory: () -> List<Int>,
    fpsMax: () -> Int,
    fpsMin: () -> Int,
    showMemory: Boolean,
    showUsage: Boolean = false,
    opened: Boolean,
    alpha: Float = 1f,
    onClick: () -> Unit = {}
) {
    FloatingBall(
        modifier = Modifier.focusProperties {
            canFocus = false
        },
        position = position,
        onPositionChanged = onPositionChanged,
        onSavePos = onSavePos,
        onClick = onClick,
        alpha = alpha,
        color = androidx.compose.ui.graphics.Color.Transparent,
        shape = RoundedCornerShape(50)
    ) {
        GameBallContent(
            gameFps = gameFps,
            fpsDisplayMode = fpsDisplayMode,
            fpsHistory = fpsHistory,
            fpsMax = fpsMax,
            fpsMin = fpsMin,
            showMemory = showMemory,
            showUsage = showUsage,
            opened = opened,
        )
    }
}

@Composable
private fun GameBallContent(
    gameFps: () -> Int?,
    fpsDisplayMode: FpsDisplayMode,
    fpsHistory: () -> List<Int>,
    fpsMax: () -> Int,
    fpsMin: () -> Int,
    showMemory: Boolean,
    showUsage: Boolean,
    opened: Boolean,
) {
    val fps = gameFps()
    val history = fpsHistory()
    val fpsMaxValue = fpsMax()
    val fpsMinValue = fpsMin()

    val showFps = remember(fps) {
        fps != null
    }

    val usage by rememberSystemUsage(showUsage)

    Row(
        modifier = Modifier
            .afGlossyPill()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val showIcon = !(showMemory || (showFps && fpsDisplayMode == FpsDisplayMode.CHART))

        AnimatedVisibility(
            visible = showIcon
        ) {
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(R.drawable.ic_af_bolt),
                    tint = if (opened) AfAccent else AfText,
                    contentDescription = null
                )
            }
        }

        AnimatedVisibility(
            visible = showFps || showMemory || showUsage
        ) {
            Spacer(Modifier.width(4.dp))
        }

        //实际内容
        Column(
            modifier = Modifier
                .wrapContentSize()
                .animateContentSize()
                .width(IntrinsicSize.Max)
        ) {
            CustomAnimatedVisibility(
                visible = showFps || showMemory || showUsage
            ) {
                Spacer(Modifier.height(4.dp))
            }
            //帧率显示
            CustomAnimatedVisibility(
                visible = showFps
            ) {
                if (fpsDisplayMode == FpsDisplayMode.CHART) {
                    //帧率图表
                    FpsChart(
                        modifier = Modifier.padding(end = 4.dp),
                        history = history,
                        fpsMax = fpsMaxValue,
                        fpsMin = fpsMinValue
                    )
                } else {
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        text = "FPS: ${fps ?: 0}",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            CustomAnimatedVisibility(
                visible = showFps && showMemory
            ) {
                Spacer(Modifier.height(4.dp))
            }
            //内存显示
            CustomAnimatedVisibility(
                visible = showMemory
            ) {
                MemoryPreview(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 168.dp)
                        .fillMaxWidth()
                        .padding(end = 4.dp),
                    mainColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    textStyle = MaterialTheme.typography.labelSmall,
                    usedText = { usedMemory, totalMemory ->
                        "${usedMemory.toInt()}MB/${totalMemory.toInt()}MB"
                    }
                )
            }
            CustomAnimatedVisibility(
                visible = showUsage && (showFps || showMemory)
            ) {
                Spacer(Modifier.height(4.dp))
            }
            //CPU
            CustomAnimatedVisibility(
                visible = showUsage
            ) {
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = "CPU ${usage.cpu}%",
                    style = MaterialTheme.typography.labelMedium
                )
            }
            CustomAnimatedVisibility(
                visible = showFps || showMemory || showUsage
            ) {
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun ColumnScope.CustomAnimatedVisibility(
    visible: Boolean,
    content: @Composable (AnimatedVisibilityScope.() -> Unit)
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandIn(expandFrom = Alignment.CenterStart) + fadeIn(),
        exit = shrinkOut(shrinkTowards = Alignment.CenterStart) + fadeOut(),
        content = content
    )
}

/** Pílula preta de vidro: topo mais claro, brilho em diagonal e borda metálica fina. */
private fun Modifier.afGlossyPill(): Modifier = this.drawWithContent {
    val w = size.width
    val h = size.height
    val outline = Path().apply {
        addRoundRect(RoundRect(0f, 0f, w, h, CornerRadius(h / 2f, h / 2f)))
    }
    clipPath(outline) {
        drawRect(
            Brush.verticalGradient(
                0f to Color(0xFF2E3338), 0.5f to Color(0xFF0F1316), 1f to Color(0xFF050809)
            )
        )
        // parte à direita da diagonal fica escura e lisa
        val diag = Path().apply {
            moveTo(w * 0.78f, 0f); lineTo(w, 0f); lineTo(w, h); lineTo(w * 0.42f, h); close()
        }
        drawPath(diag, Color(0xFF050809).copy(alpha = 0.92f))
        // fio de luz na diagonal
        drawLine(Color.White.copy(alpha = 0.10f), Offset(w * 0.78f, 0f), Offset(w * 0.42f, h), 1.dp.toPx())
    }
    drawContent()
    drawPath(
        outline,
        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.75f), Color(0xFF8A9096).copy(alpha = 0.35f), Color.White.copy(alpha = 0.45f))),
        style = Stroke(1.2.dp.toPx())
    )
}

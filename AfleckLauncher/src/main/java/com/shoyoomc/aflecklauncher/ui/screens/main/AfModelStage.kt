package com.shoyoomc.aflecklauncher.ui.screens.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.cos

private const val NODES = 48

/**
 * Palco animado do HUD: fundo preto com uma rede de pontos e linhas que se move devagar,
 * mais densa e brilhante no centro. Usa a cor de destaque do tema (muda junto com ele).
 */
@Composable
fun AfModelStage(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            androidx.compose.runtime.withFrameNanos { time = it / 1_000_000_000f }
        }
    }
    val accent = AfAccent
    val border = AfBorder
    // pontos fixos (base + fase), gerados uma vez
    val base = remember {
        FloatArray(NODES * 5).also { a ->
            for (i in 0 until NODES) {
                val s1 = ((i * 7919) % 1000) / 1000f
                val s2 = ((i * 104729) % 1000) / 1000f
                val s3 = ((i * 15485863L) % 1000L) / 1000f
                val hub = i < 16
                a[i * 5] = if (hub) 0.5f + (s1 - 0.5f) * 0.22f else s1
                a[i * 5 + 1] = if (hub) 0.5f + (s2 - 0.5f) * 0.22f else s2
                a[i * 5 + 2] = s3 * 6.28f
                a[i * 5 + 3] = 0.25f + s2 * 0.45f
                a[i * 5 + 4] = if (hub) 0.03f else 0.05f + s1 * 0.05f
            }
        }
    }
    Box(modifier) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawRect(Color.Black.copy(alpha = 0.92f))
            // brilho do centro
            drawCircle(
                Brush.radialGradient(
                    listOf(accent.copy(alpha = 0.30f), Color.Transparent),
                    center = Offset(w / 2f, h / 2f), radius = minOf(w, h) * 0.55f
                ),
                radius = minOf(w, h) * 0.55f, center = Offset(w / 2f, h / 2f)
            )
            val px = FloatArray(NODES)
            val py = FloatArray(NODES)
            for (i in 0 until NODES) {
                val ph = base[i * 5 + 2]
                val sp = base[i * 5 + 3]
                val amp = base[i * 5 + 4]
                px[i] = (base[i * 5] + amp * sin(time * sp + ph)) * w
                py[i] = (base[i * 5 + 1] + amp * cos(time * sp * 0.8f + ph)) * h
            }
            val maxD = minOf(w, h) * 0.38f
            val lw = 0.8.dp.toPx()
            for (i in 0 until NODES) {
                for (j in i + 1 until NODES) {
                    val d = hypot(px[i] - px[j], py[i] - py[j])
                    if (d < maxD) {
                        val k = 1f - d / maxD
                        drawLine(accent.copy(alpha = k * k * 0.9f), Offset(px[i], py[i]), Offset(px[j], py[j]), lw)
                    }
                }
            }
            for (i in 0 until NODES) {
                val tw = 0.55f + 0.45f * sin(time * 1.6f + base[i * 5 + 2])
                drawCircle(Color.White.copy(alpha = 0.85f * tw), radius = 1.3.dp.toPx(), center = Offset(px[i], py[i]))
            }
            // cantos de mira
            val m = 8.dp.toPx(); val l = 16.dp.toPx(); val sw = Stroke(2.dp.toPx())
            fun corner(x: Float, y: Float, dx: Float, dy: Float) {
                val pth = Path().apply { moveTo(x, y + dy * l); lineTo(x, y); lineTo(x + dx * l, y) }
                drawPath(pth, border, style = sw)
            }
            corner(m, m, 1f, 1f); corner(w - m, m, -1f, 1f); corner(m, h - m, 1f, -1f); corner(w - m, h - m, -1f, -1f)
        }
        content()
    }
}

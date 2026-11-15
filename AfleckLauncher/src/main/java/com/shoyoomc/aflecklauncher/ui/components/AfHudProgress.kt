package com.shoyoomc.aflecklauncher.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shoyoomc.aflecklauncher.ui.screens.main.AfAccent
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/*
 * Indicadores de progresso do Afleck (estilo HUD): barra segmentada em moldura de cantos cortados
 * e anel de marcas. Têm os mesmos nomes dos componentes do Material para substituí-los no app todo.
 */

/** Barra de progresso HUD. [progress] nulo = indeterminada (janela de segmentos andando). */
@Composable
fun LinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: (() -> Float)? = null
) {
    val accent = AfAccent
    val sweep = if (progress == null) {
        val t = rememberInfiniteTransition(label = "afbar")
        val v by t.animateFloat(
            0f, 1f,
            infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
            label = "afbarv"
        )
        v
    } else 0f

    Canvas(modifier.height(14.dp)) {
        val w = size.width
        val h = size.height
        val cut = h * 0.45f
        val frame = Path().apply {
            moveTo(cut, 0f); lineTo(w, 0f); lineTo(w, h - cut); lineTo(w - cut, h); lineTo(0f, h); lineTo(0f, cut); close()
        }
        drawPath(frame, accent.copy(alpha = 0.07f))
        drawPath(frame, accent.copy(alpha = 0.9f), style = Stroke(1.dp.toPx()))

        val pad = 3.dp.toPx()
        val segW = 5.dp.toPx()
        val gap = 2.dp.toPx()
        val n = floor((w - pad * 2 + gap) / (segW + gap)).toInt().coerceAtLeast(1)
        val value = progress?.invoke()?.coerceIn(0f, 1f)
        val filled = if (value != null) (value * n + 0.0001f).toInt() else 0
        val head = (sweep * (n + 6)).toInt() - 3
        for (i in 0 until n) {
            val on = if (value != null) i < filled else (i in head until head + 5)
            val alpha = if (value != null) {
                if (on) 1f else 0.16f
            } else if (on) 0.4f + 0.12f * (i - head) else 0.12f
            drawRect(
                accent.copy(alpha = alpha.coerceIn(0.08f, 1f)),
                topLeft = Offset(pad + i * (segW + gap), pad),
                size = Size(segW, h - pad * 2)
            )
        }
    }
}

/** No Afleck não existe onda: usa a mesma barra HUD. */
@Composable
fun LinearWavyProgressIndicator(
    modifier: Modifier = Modifier,
    progress: (() -> Float)? = null,
    wavelength: Dp = 0.dp
) = LinearProgressIndicator(modifier = modifier, progress = progress)

/** Anel de marcas. [progress] nulo = girando. */
@Composable
fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 4.dp,
    progress: (() -> Float)? = null
) {
    val accent = AfAccent
    val spin = if (progress == null) {
        val t = rememberInfiniteTransition(label = "afring")
        val v by t.animateFloat(
            0f, 12f,
            infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Restart),
            label = "afringv"
        )
        v
    } else 0f

    Canvas(modifier.defaultMinSize(40.dp, 40.dp)) {
        val ticks = 12
        val r = size.minDimension / 2f
        val sw = (size.minDimension * 0.09f).coerceAtLeast(strokeWidth.toPx() * 0.6f).coerceAtMost(strokeWidth.toPx() * 2f)
        val inner = r - sw * 2.6f
        val value = progress?.invoke()?.coerceIn(0f, 1f)
        val center = Offset(size.width / 2f, size.height / 2f)
        for (i in 0 until ticks) {
            val a = (i / ticks.toFloat()) * 2f * PI.toFloat() - (PI / 2).toFloat()
            val alpha = if (value != null) {
                if (i < (value * ticks + 0.0001f).toInt()) 1f else 0.18f
            } else {
                val d = ((floor(spin).toInt() - i) % ticks + ticks) % ticks // 0 = ponta
                (1f - d / ticks.toFloat()).coerceIn(0.15f, 1f)
            }
            drawLine(
                accent.copy(alpha = alpha),
                Offset(center.x + cos(a) * inner, center.y + sin(a) * inner),
                Offset(center.x + cos(a) * (r - sw / 2f), center.y + sin(a) * (r - sw / 2f)),
                strokeWidth = sw, cap = StrokeCap.Round
            )
        }
    }
}

/** Indicador de carregamento grande (substitui o LoadingIndicator do Material). */
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier.defaultMinSize(48.dp, 48.dp), strokeWidth = 5.dp)
}

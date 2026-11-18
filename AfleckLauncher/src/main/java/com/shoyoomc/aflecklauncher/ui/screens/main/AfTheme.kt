package com.shoyoomc.aflecklauncher.ui.screens.main

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import android.content.SharedPreferences
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import com.shoyoomc.aflecklauncher.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Definição de um tema aurora. */
class AfThemeDef(
    val id: String,
    val name: String,
    val bg: Int,
    val accent: Color,
    val accent2: Color,
    val base: Color,
    val border: Color,
    val dim: Color
)

/** Estado do tema (cores, vibração, contraste, opacidade), salvo no aparelho. */
object AfThemeState {
    val themes = listOf(
        AfThemeDef("branca", "Aurora Branca", R.drawable.af_bg_branca, Color(0xFFFFFFFF), Color(0xFFBFC8D2), Color(0xFF0B0B0D), Color(0xFFE6ECF2), Color(0xFFB4B9C2)),
        AfThemeDef("ciano", "Aurora Ciano", R.drawable.af_bg_ciano, Color(0xFF22E3F7), Color(0xFF4DF2D8), Color(0xFF0A3558), Color(0xFF1ED8F5), Color(0xFFB9D6E6)),
        AfThemeDef("verde", "Aurora Verde", R.drawable.af_bg_verde, Color(0xFF3DFF8A), Color(0xFFB6FF5E), Color(0xFF07261A), Color(0xFF2BE67A), Color(0xFFB4DCC4)),
        AfThemeDef("roxa", "Aurora Roxa", R.drawable.af_bg_roxa, Color(0xFFB388FF), Color(0xFFFF7AE0), Color(0xFF1E0B3A), Color(0xFFB388FF), Color(0xFFD0C2E8)),
        AfThemeDef("amarela", "Aurora Amarela", R.drawable.af_bg_amarela, Color(0xFFFFD740), Color(0xFFFF9F1C), Color(0xFF2A1E05), Color(0xFFFFC93C), Color(0xFFE6D8B0)),
        AfThemeDef("rosa", "Aurora Rosa", R.drawable.af_bg_rosa, Color(0xFFFF6EA8), Color(0xFFFFB36E), Color(0xFF3A0B22), Color(0xFFFF5C9A), Color(0xFFE8C2D2))
    )

    var themeId by mutableStateOf("branca")
        private set
    var vibrance by mutableIntStateOf(0)
        private set
    var contrast by mutableIntStateOf(0)
        private set
    var panelOpacity by mutableIntStateOf(45)
        private set
    var animate by mutableStateOf(false)
        private set
    var uiScale by mutableIntStateOf(100)
        private set
    var glowOn by mutableStateOf(false)
        private set
    var glowLevel by mutableIntStateOf(5)
        private set
    var glowStrength by mutableIntStateOf(70)
        private set
    var neonAnimated by mutableStateOf(false)
        private set
    var neonThickness by mutableIntStateOf(3)
        private set

    private var prefs: SharedPreferences? = null

    fun load(ctx: Context) {
        if (prefs != null) return
        val p = ctx.getSharedPreferences("afleck_ui", Context.MODE_PRIVATE)
        prefs = p
        themeId = p.getString("theme.id", "branca") ?: "branca"
        vibrance = p.getInt("theme.vibrance", 0)
        contrast = p.getInt("theme.contrast", 0)
        panelOpacity = p.getInt("theme.panelOpacityGlass", 45)
        animate = p.getBoolean("theme.animate", false)
        uiScale = p.getInt("theme.uiScale100", 100)
        glowOn = p.getBoolean("theme.glowOn", false)
        glowLevel = p.getInt("theme.glowLevel", 5)
        glowStrength = p.getInt("theme.glowStrength", 70)
        neonAnimated = p.getBoolean("theme.neonAnimated", false)
        neonThickness = p.getInt("theme.neonThickness", 3)
    }

    fun chooseTheme(id: String) { themeId = id; saveAll() }
    fun changeVibrance(v: Int) { vibrance = v.coerceIn(-100, 100); saveAll() }
    fun changeContrast(v: Int) { contrast = v.coerceIn(-100, 100); saveAll() }
    fun changePanelOpacity(v: Int) { panelOpacity = v.coerceIn(0, 100); saveAll() }
    fun changeAnimate(v: Boolean) { animate = v; saveAll() }
    fun changeGlowOn(v: Boolean) { glowOn = v; saveAll() }
    fun changeGlowLevel(v: Int) { glowLevel = v.coerceIn(1, 10); saveAll() }
    fun changeGlowStrength(v: Int) { glowStrength = v.coerceIn(5, 100); saveAll() }
    fun changeNeonAnimated(v: Boolean) { neonAnimated = v; saveAll() }
    fun changeNeonThickness(v: Int) { neonThickness = v.coerceIn(1, 10); saveAll() }
    fun changeUiScale(v: Int) { uiScale = v.coerceIn(60, 110); saveAll() }
    fun resetAdjustments() { vibrance = 0; contrast = 0; panelOpacity = 45; animate = false; uiScale = 100; saveAll() }

    private fun saveAll() {
        prefs?.edit()
            ?.putString("theme.id", themeId)
            ?.putInt("theme.vibrance", vibrance)
            ?.putInt("theme.contrast", contrast)
            ?.putInt("theme.panelOpacityGlass", panelOpacity)
            ?.putBoolean("theme.animate", animate)
            ?.putInt("theme.uiScale100", uiScale)
            ?.putBoolean("theme.glowOn", glowOn)
            ?.putInt("theme.glowLevel", glowLevel)
            ?.putInt("theme.glowStrength", glowStrength)
            ?.putBoolean("theme.neonAnimated", neonAnimated)
            ?.putInt("theme.neonThickness", neonThickness)
            ?.apply()
    }

    val current: AfThemeDef get() = themes.firstOrNull { it.id == themeId } ?: themes[0]

    private fun vivid(c: Color): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(c.toArgb(), hsv)
        hsv[1] = (hsv[1] * (1f + vibrance / 100f)).coerceIn(0f, 1f)
        return Color(android.graphics.Color.HSVToColor(hsv))
    }

    private fun contrasted(c: Color): Color =
        if (contrast >= 0) lerp(c, Color.Black, 0.45f * contrast / 100f)
        else lerp(c, Color.White, 0.25f * -contrast / 100f)

    val accent: Color get() = vivid(current.accent)
    val accent2: Color get() = vivid(current.accent2)
    val border: Color get() = vivid(current.border)
    val base: Color get() = contrasted(current.base)
}

internal val AfPanel: Color get() = AfThemeState.base.copy(alpha = AfThemeState.panelOpacity / 100f)
internal val AfPanelDeep: Color get() = AfThemeState.base.copy(alpha = (AfThemeState.panelOpacity / 100f + 0.05f).coerceAtMost(1f))
internal val AfBorder: Color get() = AfThemeState.border
internal val AfAccent: Color get() = AfThemeState.accent
internal val AfAccent2: Color get() = AfThemeState.accent2
internal val AfNavy: Color get() = lerp(AfThemeState.base, Color.Black, 0.55f).copy(alpha = 1f)
internal val AfText = Color.White
internal val AfDim: Color get() = AfThemeState.current.dim

/** Degradê dos botões; se "Animar degradê" estiver ligado, as cores se movem. */
@Composable
internal fun afAccentBrush(): Brush {
    if (!AfThemeState.animate) return Brush.horizontalGradient(listOf(AfAccent, AfAccent2))
    val t = rememberInfiniteTransition(label = "afgrad")
    val off by t.animateFloat(0f, 800f, infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "afoff")
    return Brush.horizontalGradient(listOf(AfAccent, AfAccent2), startX = off, endX = off + 400f, tileMode = TileMode.Mirror)
}

internal fun Modifier.afClick(onClick: () -> Unit): Modifier = composed {
    clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
}

/** Painel de vidro azul-marinho com borda ciano. */
@Composable
internal fun AfPanel(
    modifier: Modifier = Modifier,
    radius: Dp = 28.dp,
    deep: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = AfCutShape(radius.coerceIn(12.dp, 24.dp))
    Box(
        modifier = modifier.afGlassSurface(shape),
        content = content
    )
}


/** Vidro: fundo translúcido, brilho nas bordas e um reflexo fino no topo. */
internal fun Modifier.afGlassSurface(shape: androidx.compose.ui.graphics.Shape, selected: Boolean = false): Modifier = composed {
    val neon = AfThemeState.glowOn && AfThemeState.neonAnimated
    val pulse = if (neon) rememberInfiniteTransition(label = "afneon").animateFloat(
        initialValue = 0.45f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), androidx.compose.animation.core.RepeatMode.Reverse),
        label = "afneonv"
    ) else null
    val k = (AfThemeState.panelOpacity / 45f).coerceIn(0.3f, 1.8f)
    val accent = AfAccent
    val accent2 = AfAccent2
    val base = AfThemeState.base
    val fill = remember(selected, k, accent, accent2, base) {
        if (selected)
            Brush.horizontalGradient(listOf(accent.copy(alpha = 0.55f), accent2.copy(alpha = 0.55f)))
        else
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = (0.20f * k).coerceAtMost(0.6f)),
                    Color.White.copy(alpha = (0.06f * k).coerceAtMost(0.4f)),
                    base.copy(alpha = (0.28f * k).coerceAtMost(0.8f))
                )
            )
    }
    val borderBrush = remember(accent) {
        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.22f), accent.copy(alpha = 0.55f)))
    }
    val glintBrush = remember { Brush.horizontalGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.95f), Color.Transparent)) }
    val glowOn = AfThemeState.glowOn
    val glowLevel = AfThemeState.glowLevel.coerceAtMost(4)
    val glowStr = AfThemeState.glowStrength / 100f
    val glowTh = AfThemeState.neonThickness / 3f
    this@composed
        .clip(shape)
        .background(fill, shape)
        .drawWithCache {
            val o = shape.createOutline(size, layoutDirection, this)
            val borderStroke = androidx.compose.ui.graphics.drawscope.Stroke(1.2.dp.toPx())
            val y = 1.dp.toPx()
            val p1 = androidx.compose.ui.geometry.Offset(size.width * 0.18f, y)
            val p2 = androidx.compose.ui.geometry.Offset(size.width * 0.82f, y)
            val glintW = 1.6.dp.toPx()
            val frame = shape is AfCutShape && shape.cut >= 16.dp && size.minDimension > 70.dp.toPx()
            val c = if (frame) (shape as AfCutShape).cut.toPx().coerceAtMost(size.minDimension / 3f) else 0f
            val sc = c * 0.4f
            val inset = 7.dp.toPx()
            val len = 16.dp.toPx()
            val bw = 1.8.dp.toPx()
            val node = 3.dp.toPx()
            val nodeS = androidx.compose.ui.graphics.drawscope.Stroke(1.2.dp.toPx())
            val barA = androidx.compose.ui.geometry.Offset(size.width * 0.35f, size.height - 1.5.dp.toPx())
            val barB = androidx.compose.ui.geometry.Offset(size.width * 0.65f, size.height - 1.5.dp.toPx())
            val barBrush = Brush.horizontalGradient(listOf(Color.Transparent, accent2.copy(alpha = 0.9f), Color.Transparent))
            val bracket = Path().apply {
                // canto superior esquerdo
                moveTo(inset + c * 0.35f, inset + len); lineTo(inset + c * 0.35f, inset + c * 0.35f)
                // (usa o chanfro: sobe e segue pela diagonal até o topo)
                lineTo(inset + c * 0.35f + len, inset + c * 0.35f)
                // canto inferior direito
                moveTo(size.width - inset - c * 0.35f, size.height - inset - len)
                lineTo(size.width - inset - c * 0.35f, size.height - inset - c * 0.35f)
                lineTo(size.width - inset - c * 0.35f - len, size.height - inset - c * 0.35f)
            }
            onDrawWithContent {
                drawContent()
                afOutline(o, borderBrush, style = borderStroke)
                drawLine(glintBrush, p1, p2, strokeWidth = glintW)
                if (frame) {
                    drawPath(bracket, accent2.copy(alpha = 0.95f), style = androidx.compose.ui.graphics.drawscope.Stroke(bw, cap = androidx.compose.ui.graphics.StrokeCap.Square))
                    drawLine(barBrush, barA, barB, strokeWidth = 2.dp.toPx())
                    drawCircle(accent, node, Offset(size.width - inset - sc, inset + sc), style = nodeS)
                    drawCircle(accent, node, Offset(inset + sc, size.height - inset - sc), style = nodeS)
                }
                if (glowOn) {
                    val pv = pulse?.value ?: 1f
                    for (i in 1..glowLevel) {
                        val al = (0.34f * glowStr * pv / i).coerceIn(0f, 1f)
                        afOutline(
                            o,
                            Brush.verticalGradient(listOf(accent.copy(alpha = al), accent2.copy(alpha = al))),
                            style = androidx.compose.ui.graphics.drawscope.Stroke((i * 1.5f * glowTh).dp.toPx())
                        )
                    }
                }
            }
        }
}

/** Retângulo arredondado só com borda (sem fundo, sem sombra). */
internal fun Modifier.afRect(color: Color, selected: Boolean = false, radius: Dp = 14.dp): Modifier =
    this.afGlassSurface(androidx.compose.foundation.shape.RoundedCornerShape(radius.coerceAtMost(28.dp)), selected)

/** Vidro bem escuro com a cor do tema (marca, menu lateral): fundo translúcido, mas denso, como no Cryo. */
internal fun Modifier.afDarkGlass(color: Color, selected: Boolean = false, radius: Dp = 28.dp): Modifier =
    this.afGlassSurface(androidx.compose.foundation.shape.RoundedCornerShape(radius), selected)

/** Botão principal: degradê ciano, texto escuro. */
@Composable
internal fun AfButton(text: String, modifier: Modifier = Modifier, icon: Int? = null, onClick: () -> Unit) {
    Row(
        modifier = modifier.height(50.dp)
            .afRect(AfAccent, selected = true, radius = 14.dp)
            .afClick(onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(painterResource(icon), null, tint = AfAccent, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, color = AfText, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

/** Botão secundário: só contorno ciano. */
@Composable
internal fun AfOutlineButton(text: String, modifier: Modifier = Modifier, icon: Int? = null, onClick: () -> Unit) {
    Row(
        modifier = modifier.height(44.dp)
            .afRect(AfBorder, radius = 14.dp)
            .afClick(onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(painterResource(icon), null, tint = AfAccent, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, color = AfText, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

/** Círculo com ícone (preenchido em ciano ou escuro com contorno). */
@Composable
internal fun AfIconCircle(icon: Int, size: Dp = 40.dp, filled: Boolean = false, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(size)
            .afGlow(CircleShape, if (filled) AfAccent else AfBorder, if (filled) 1.2f else 0.7f)
            .clip(CircleShape)
            .background(if (filled) AfAccent.copy(alpha = 0.16f) else AfNavy.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(painterResource(icon), null, tint = if (filled) AfAccent else AfText, modifier = Modifier.size(size * 0.5f))
    }
}

/** Painel "vidro HUD": degradê de vidro, brilho no topo e cantoneiras neon nos cantos. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.afOutline(
    outline: androidx.compose.ui.graphics.Outline,
    color: Color,
    style: androidx.compose.ui.graphics.drawscope.DrawStyle = androidx.compose.ui.graphics.drawscope.Fill
) = drawPath(outline.afPath(), color, style = style)

private fun androidx.compose.ui.graphics.drawscope.DrawScope.afOutline(
    outline: androidx.compose.ui.graphics.Outline,
    brush: Brush,
    style: androidx.compose.ui.graphics.drawscope.DrawStyle = androidx.compose.ui.graphics.drawscope.Fill
) = drawPath(outline.afPath(), brush, style = style)

private fun androidx.compose.ui.graphics.Outline.afPath(): androidx.compose.ui.graphics.Path = when (this) {
    is androidx.compose.ui.graphics.Outline.Rectangle -> androidx.compose.ui.graphics.Path().apply { addRect(rect) }
    is androidx.compose.ui.graphics.Outline.Rounded -> androidx.compose.ui.graphics.Path().apply { addRoundRect(roundRect) }
    is androidx.compose.ui.graphics.Outline.Generic -> path
}

/**
 * Vidro fino (pílulas, botões, linhas): fundo claro translúcido, borda fina clara,
 * reflexo no canto de cima e brilho no meio da base. [strength] >= 1.2 = selecionado (tingido com [color]).
 */
internal fun Modifier.afGlow(shape: androidx.compose.ui.graphics.Shape, color: Color, strength: Float = 1f): Modifier =
    this.drawWithContent {
        drawContent()
        val o = shape.createOutline(size, layoutDirection, this)
        val sel = strength >= 1.2f
        val tint = lerp(Color.White, color, if (sel) 0.85f else 0.35f)
        val w = size.width
        val h = size.height
        val thin = 1.dp.toPx()
        clipPath(o.afPath()) {
            if (sel) drawRect(color.copy(alpha = 0.16f))
        }
        // borda fina: clara em cima, mais suave embaixo
        afOutline(
            o,
            Brush.verticalGradient(listOf(tint.copy(alpha = 0.85f), tint.copy(alpha = 0.28f), tint.copy(alpha = 0.5f))),
            style = androidx.compose.ui.graphics.drawscope.Stroke(thin)
        )
    }

/** Molde HUD neon: cantos recortados (chanfro grande em cima-esquerda e embaixo-direita, pequeno nos outros). */
class AfCutShape(val cut: Dp = 14.dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val c = with(density) { cut.toPx() }.coerceAtMost(minOf(size.width, size.height) / 3f)
        val s = c * 0.4f
        val w = size.width
        val h = size.height
        val p = Path()
        p.moveTo(c, 0f)
        p.lineTo(w - s, 0f)
        p.lineTo(w, s)
        p.lineTo(w, h - c)
        p.lineTo(w - c, h)
        p.lineTo(s, h)
        p.lineTo(0f, h - s)
        p.lineTo(0f, c)
        p.close()
        return Outline.Generic(p)
    }
}

/** Vidro grande (cartões e painéis): degradê escuro na diagonal, luz nas bordas, borda branca fina. */
@Composable
internal fun Modifier.afHud(shape: androidx.compose.ui.graphics.Shape): Modifier = this.afGlassSurface(shape)

package com.shoyoomc.aflecklauncher.ui.screens.game.elements

import android.os.Process
import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

/** Uso aproximado de CPU (do próprio app) e de GPU (se o sistema deixar ler). */
class SystemUsage(val cpu: Int, val gpu: Int?)

private val GPU_PATHS = listOf(
    "/sys/class/kgsl/kgsl-3d0/gpu_busy_percentage",   // Adreno
    "/sys/kernel/gpu/gpu_busy",                       // Mali (alguns aparelhos)
    "/sys/kernel/ged/hal/gpu_utilization",            // Mediatek
    "/sys/class/misc/mali0/device/utilisation",       // Mali (alguns aparelhos)
    "/sys/devices/platform/mali/utilization"
)

private fun readGpuPercent(): Int? {
    for (path in GPU_PATHS) {
        val value = runCatching {
            val text = File(path).readText().trim()
            Regex("\\d+").find(text)?.value?.toInt()
        }.getOrNull()
        if (value != null) return value.coerceIn(0, 100)
    }
    return null
}

/** Atualiza a cada 1 segundo, só enquanto [enabled] estiver ligado. */
@Composable
fun rememberSystemUsage(enabled: Boolean): State<SystemUsage> =
    produceState(initialValue = SystemUsage(0, null), enabled) {
        if (!enabled) return@produceState
        val cores = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
        var lastCpu = Process.getElapsedCpuTime()
        var lastWall = SystemClock.elapsedRealtime()
        while (true) {
            delay(1000)
            val cpuNow = Process.getElapsedCpuTime()
            val wallNow = SystemClock.elapsedRealtime()
            val dWall = (wallNow - lastWall).coerceAtLeast(1)
            val cpu = ((cpuNow - lastCpu) * 100f / (dWall * cores)).toInt().coerceIn(0, 100)
            lastCpu = cpuNow
            lastWall = wallNow
            val gpu = withContext(Dispatchers.IO) { readGpuPercent() }
            value = SystemUsage(cpu, gpu)
        }
    }

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

package com.shoyoomc.aflecklauncher.ui.screens.content.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.Color
import com.shoyoomc.aflecklauncher.ui.screens.main.AfAccent
import com.shoyoomc.aflecklauncher.ui.screens.main.AfBorder
import com.shoyoomc.aflecklauncher.ui.screens.main.AfButton
import com.shoyoomc.aflecklauncher.ui.screens.main.AfCutShape
import com.shoyoomc.aflecklauncher.ui.screens.main.AfNavy
import com.shoyoomc.aflecklauncher.ui.screens.main.AfOutlineButton
import com.shoyoomc.aflecklauncher.ui.screens.main.afClick
import com.shoyoomc.aflecklauncher.ui.screens.main.afGlow
import com.shoyoomc.aflecklauncher.ui.screens.main.afHud
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shoyoomc.aflecklauncher.R
import com.shoyoomc.aflecklauncher.bridge.CursorShape
import com.shoyoomc.aflecklauncher.context.copyLocalFile
import com.shoyoomc.aflecklauncher.contract.MediaPickerContract
import com.shoyoomc.aflecklauncher.coroutine.Task
import com.shoyoomc.aflecklauncher.coroutine.TaskSystem
import com.shoyoomc.aflecklauncher.setting.AllSettings
import com.shoyoomc.aflecklauncher.setting.enums.GestureActionType
import com.shoyoomc.aflecklauncher.setting.enums.MouseControlMode
import com.shoyoomc.aflecklauncher.setting.enums.MouseTouchBlockMode
import com.shoyoomc.aflecklauncher.setting.unit.ParcelableSettingUnit
import com.shoyoomc.aflecklauncher.setting.unit.floatRange
import com.shoyoomc.aflecklauncher.ui.androidText
import com.shoyoomc.aflecklauncher.ui.base.BaseScreen
import com.shoyoomc.aflecklauncher.ui.components.AnimatedColumn
import com.shoyoomc.aflecklauncher.ui.components.IconTextButton
import com.shoyoomc.aflecklauncher.ui.components.LittleTextLabel
import com.shoyoomc.aflecklauncher.ui.components.MarqueeText
import com.shoyoomc.aflecklauncher.ui.components.SimpleAlertDialog
import com.shoyoomc.aflecklauncher.ui.components.TitleAndSummary
import com.shoyoomc.aflecklauncher.ui.components.TooltipIconButton
import com.shoyoomc.aflecklauncher.ui.components.animated
import com.shoyoomc.aflecklauncher.ui.components.infiniteShimmer
import com.shoyoomc.aflecklauncher.ui.components.verticalScrollWithBar
import com.shoyoomc.aflecklauncher.ui.control.gyroscope.isGyroscopeAvailable
import com.shoyoomc.aflecklauncher.ui.control.mouse.CursorHotspot
import com.shoyoomc.aflecklauncher.ui.control.mouse.MouseHotspotEditorDialog
import com.shoyoomc.aflecklauncher.ui.control.mouse.MousePointer
import com.shoyoomc.aflecklauncher.ui.control.mouse.arrowPointerFile
import com.shoyoomc.aflecklauncher.ui.control.mouse.crossHairPointerFile
import com.shoyoomc.aflecklauncher.ui.control.mouse.iBeamPointerFile
import com.shoyoomc.aflecklauncher.ui.control.mouse.linkPointerFile
import com.shoyoomc.aflecklauncher.ui.control.mouse.notAllowedPointerFile
import com.shoyoomc.aflecklauncher.ui.control.mouse.resizeAllPointerFile
import com.shoyoomc.aflecklauncher.ui.control.mouse.resizeEWPointerFile
import com.shoyoomc.aflecklauncher.ui.control.mouse.resizeNSPointerFile
import com.shoyoomc.aflecklauncher.ui.screens.NestedNavKey
import com.shoyoomc.aflecklauncher.ui.screens.NormalNavKey
import com.shoyoomc.aflecklauncher.ui.screens.TitledNavKey
import com.shoyoomc.aflecklauncher.ui.screens.content.settings.layouts.CardPosition
import com.shoyoomc.aflecklauncher.ui.screens.content.settings.layouts.IntSliderSettingsCard
import com.shoyoomc.aflecklauncher.ui.screens.content.settings.layouts.ListSettingsCard
import com.shoyoomc.aflecklauncher.ui.screens.content.settings.layouts.SettingsCard
import com.shoyoomc.aflecklauncher.ui.screens.content.settings.layouts.SettingsCardColumn
import com.shoyoomc.aflecklauncher.ui.screens.content.settings.layouts.SwitchSettingsCard
import com.shoyoomc.aflecklauncher.utils.formatKeyCode
import com.shoyoomc.aflecklauncher.utils.image.isImageFile
import com.shoyoomc.aflecklauncher.utils.string.getMessageOrToString
import com.shoyoomc.aflecklauncher.viewmodel.ErrorViewModel
import com.shoyoomc.aflecklauncher.viewmodel.EventViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.withContext
import org.apache.commons.io.FileUtils
import java.io.File

@Composable
fun ControlSettingsScreen(
    key: NestedNavKey.Settings,
    settingsScreenKey: TitledNavKey?,
    mainScreenKey: TitledNavKey?,
    eventViewModel: EventViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    BaseScreen(
        Triple(key, mainScreenKey, false),
        Triple(NormalNavKey.Settings.Control, settingsScreenKey, false)
    ) { isVisible ->
        AnimatedColumn(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScrollWithBar(state = rememberScrollState())
                .padding(all = 12.dp),
            isVisible = isVisible
        ) {
            AnimatedItem { ctx ->
                SettingsCardColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animated(ctx)
                ) {
                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Top,
                        unit = AllSettings.physicalMouseMode,
                        title = stringResource(R.string.settings_control_mouse_physical_mouse_mode_title),
                        summary = stringResource(R.string.settings_control_mouse_physical_mouse_mode_summary),
                        trailingIcon = {
                            TooltipIconButton(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(horizontal = 8.dp),
                                tooltipTitle = stringResource(R.string.generic_warning),
                                tooltipMessage = stringResource(R.string.settings_control_mouse_physical_mouse_warning)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_warning_filled),
                                    contentDescription = stringResource(R.string.generic_warning),
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                )
                            }
                        }
                    )

                    var operation by remember { mutableStateOf<PhysicalKeyOperation>(PhysicalKeyOperation.None) }
                    SettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Bottom,
                        onClick = { operation = PhysicalKeyOperation.Bind }
                    ) {
                        PhysicalKeyImeTrigger(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 16.dp),
                            operation = operation,
                            changeOperation = { operation = it },
                            eventViewModel = eventViewModel
                        )
                    }
                }
            }

            AnimatedItem { ctx ->
                SettingsCardColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animated(ctx)
                ) {
                    val mouseSize = AllSettings.mouseSize.state

                    var mouseStyleOpen by remember { mutableStateOf(false) }
                    SettingsCard(
                        position = CardPosition.Top,
                        modifier = Modifier.fillMaxWidth(),
                        title = "Estilo do mouse virtual",
                        summary = "Troque cada tipo de setinha do mouse virtual com um guia passo a passo.",
                        onClick = { mouseStyleOpen = true },
                        trailingIcon = {
                            Text("›", style = MaterialTheme.typography.titleLarge)
                        }
                    )
                    if (mouseStyleOpen) {
                        AfMouseStyleDialog(
                            mouseSize = mouseSize,
                            submitError = submitError,
                            onClose = { mouseStyleOpen = false }
                        )
                    }

                    IntSliderSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Bottom,
                        unit = AllSettings.mouseSize,
                        title = stringResource(R.string.settings_control_mouse_size_title),
                        valueRange = AllSettings.mouseSize.floatRange,
                        suffix = "Dp",
                        fineTuningControl = true
                    )
                }
            }

            AnimatedItem { ctx ->
                SettingsCardColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animated(ctx)
                ) {
                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Top,
                        unit = AllSettings.hideMouse,
                        title = stringResource(R.string.settings_control_mouse_hide_title),
                        summary = stringResource(R.string.settings_control_mouse_hide_summary),
                        enabled = AllSettings.mouseControlMode.state == MouseControlMode.CLICK //仅点击模式下可更改设置
                    )

                    SwitchSettingsCard(
                        unit = AllSettings.enableMouseClick,
                        position = CardPosition.Middle,
                        title = stringResource(R.string.settings_control_mouse_enable_click_title),
                        summary = stringResource(R.string.settings_control_mouse_enable_click_summary),
                        enabled = AllSettings.mouseControlMode.state == MouseControlMode.SLIDE //仅滑动模式下可更改设置
                    )

                    ListSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.mouseControlMode,
                        items = MouseControlMode.entries,
                        title = stringResource(R.string.settings_control_mouse_control_mode_title),
                        summary = stringResource(R.string.settings_control_mouse_control_mode_summary),
                        getItemText = { stringResource(it.nameRes) }
                    )

                    ListSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.mouseTouchBlockMode,
                        items = MouseTouchBlockMode.entries,
                        title = stringResource(R.string.settings_control_mouse_block_touch_title),
                        summary = stringResource(R.string.settings_control_mouse_block_touch_summary),
                        getItemText = { stringResource(it.nameRes) }
                    )

                    IntSliderSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.cursorSensitivity,
                        title = stringResource(R.string.settings_control_mouse_sensitivity_title),
                        summary = stringResource(R.string.settings_control_mouse_sensitivity_summary),
                        valueRange = AllSettings.cursorSensitivity.floatRange,
                        suffix = "%",
                        fineTuningControl = true
                    )

                    IntSliderSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.mouseCaptureSensitivity,
                        title = stringResource(R.string.settings_control_mouse_capture_sensitivity_title),
                        summary = stringResource(R.string.settings_control_mouse_capture_sensitivity_summary),
                        valueRange = AllSettings.mouseCaptureSensitivity.floatRange,
                        suffix = "%",
                        fineTuningControl = true
                    )

                    IntSliderSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Bottom,
                        unit = AllSettings.mouseLongPressDelay,
                        title = stringResource(R.string.settings_control_mouse_long_press_delay_title),
                        summary = stringResource(R.string.settings_control_mouse_long_press_delay_summary),
                        valueRange = AllSettings.mouseLongPressDelay.floatRange,
                        suffix = "ms",
                        fineTuningControl = true
                    )
                }
            }

            AnimatedItem { ctx ->
                SettingsCardColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animated(ctx)
                ) {
                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Top,
                        unit = AllSettings.gestureControl,
                        title = stringResource(R.string.settings_control_gesture_control_title),
                        summary = stringResource(R.string.settings_control_gesture_control_summary)
                    )

                    ListSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.gestureTapMouseAction,
                        items = GestureActionType.entries,
                        title = stringResource(R.string.settings_control_gesture_tap_action_title),
                        summary = stringResource(R.string.settings_control_gesture_tap_action_summary),
                        getItemText = { stringResource(it.nameRes) },
                        enabled = AllSettings.gestureControl.state
                    )

                    ListSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.gestureLongPressMouseAction,
                        items = GestureActionType.entries,
                        title = stringResource(R.string.settings_control_gesture_long_press_action_title),
                        summary = stringResource(R.string.settings_control_gesture_long_press_action_summary),
                        getItemText = { stringResource(it.nameRes) },
                        enabled = AllSettings.gestureControl.state
                    )

                    IntSliderSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Bottom,
                        unit = AllSettings.gestureLongPressDelay,
                        title = stringResource(R.string.settings_control_gesture_long_press_delay_title),
                        summary = stringResource(R.string.settings_control_mouse_long_press_delay_summary),
                        valueRange = AllSettings.gestureLongPressDelay.floatRange,
                        suffix = "ms",
                        enabled = AllSettings.gestureControl.state,
                        fineTuningControl = true
                    )
                }
            }

            AnimatedItem { ctx ->
                SettingsCardColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animated(ctx)
                ) {
                    //检查陀螺仪是否可用
                    val context = LocalContext.current
                    val isGyroscopeAvailable = remember(context) {
                        isGyroscopeAvailable(context = context)
                    }

                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Top,
                        unit = AllSettings.gyroscopeControl,
                        title = stringResource(R.string.settings_control_gyroscope_title),
                        summary = stringResource(R.string.settings_control_gyroscope_summary),
                        enabled = isGyroscopeAvailable,
                        trailingIcon = if (!isGyroscopeAvailable) {
                            @Composable {
                                TooltipIconButton(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(horizontal = 8.dp),
                                    tooltipTitle = stringResource(R.string.generic_warning),
                                    tooltipMessage = stringResource(R.string.settings_control_gyroscope_unsupported)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_warning_filled),
                                        contentDescription = stringResource(R.string.generic_warning),
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        } else null
                    )

                    IntSliderSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.gyroscopeSensitivity,
                        title = stringResource(R.string.settings_control_gyroscope_sensitivity_title),
                        valueRange = AllSettings.gyroscopeSensitivity.floatRange,
                        suffix = "%",
                        enabled = isGyroscopeAvailable && AllSettings.gyroscopeControl.state,
                        fineTuningControl = true
                    )

                    IntSliderSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.gyroscopeSampleRate,
                        title = stringResource(R.string.settings_control_gyroscope_sample_rate_title),
                        summary = stringResource(R.string.settings_control_gyroscope_sample_rate_summary),
                        valueRange = AllSettings.gyroscopeSampleRate.floatRange,
                        suffix = "ms",
                        enabled = isGyroscopeAvailable && AllSettings.gyroscopeControl.state,
                        fineTuningControl = true
                    )

                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.gyroscopeSmoothing,
                        title = stringResource(R.string.settings_control_gyroscope_smoothing_title),
                        summary = stringResource(R.string.settings_control_gyroscope_smoothing_summary),
                        enabled = isGyroscopeAvailable && AllSettings.gyroscopeControl.state
                    )

                    IntSliderSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.gyroscopeSmoothingWindow,
                        title = stringResource(R.string.settings_control_gyroscope_smoothing_window_title),
                        summary = stringResource(R.string.settings_control_gyroscope_smoothing_window_summary),
                        valueRange = AllSettings.gyroscopeSmoothingWindow.floatRange,
                        enabled = isGyroscopeAvailable && AllSettings.gyroscopeControl.state && AllSettings.gyroscopeSmoothing.state
                    )

                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.gyroscopeInvertX,
                        title = stringResource(R.string.settings_control_gyroscope_invert_x_title),
                        summary = stringResource(R.string.settings_control_gyroscope_invert_x_summary),
                        enabled = isGyroscopeAvailable && AllSettings.gyroscopeControl.state
                    )

                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Bottom,
                        unit = AllSettings.gyroscopeInvertY,
                        title = stringResource(R.string.settings_control_gyroscope_invert_y_title),
                        summary = stringResource(R.string.settings_control_gyroscope_invert_y_summary),
                        enabled = isGyroscopeAvailable && AllSettings.gyroscopeControl.state
                    )
                }
            }
        }
    }
}

private sealed interface PhysicalKeyOperation {
    data object None: PhysicalKeyOperation
    data object Bind: PhysicalKeyOperation
}

@Composable
private fun PhysicalKeyImeTrigger(
    modifier: Modifier = Modifier,
    operation: PhysicalKeyOperation,
    changeOperation: (PhysicalKeyOperation) -> Unit,
    eventViewModel: EventViewModel
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .animateContentSize()
        ) {
            TitleAndSummary(
                title = stringResource(R.string.settings_control_physical_key_bind_ime_title),
                summary = stringResource(R.string.settings_control_physical_key_bind_ime_summary)
            )
            when (operation) {
                PhysicalKeyOperation.None -> {}
                PhysicalKeyOperation.Bind -> {
                    LaunchedEffect(Unit) {
                        eventViewModel.sendEvent(EventViewModel.Event.Key.StartKeyCapture)
                        //接收Activity发送的按键事件
                        eventViewModel.events
                            .filterIsInstance<EventViewModel.Event.Key.OnKeyDown>()
                            .collect { event ->
                                changeOperation(PhysicalKeyOperation.None)
                                AllSettings.physicalKeyImeCode.save(event.key.keyCode)
                            }
                    }

                    DisposableEffect(Unit) {
                        onDispose {
                            eventViewModel.sendEvent(EventViewModel.Event.Key.StopKeyCapture)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LittleTextLabel(text = stringResource(R.string.control_keyboard_bind_title))
                        MarqueeText(
                            modifier = Modifier
                                .weight(1f)
                                .infiniteShimmer(
                                    initialValue = 0.5f,
                                    targetValue = 1f
                                ),
                            text = stringResource(R.string.control_keyboard_bind_summary),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            val code = AllSettings.physicalKeyImeCode.state
            when {
                code == null -> {
                    Text(
                        modifier = Modifier.padding(end = 12.dp),
                        text = stringResource(R.string.settings_control_physical_key_bind_ime_un_bind),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                else -> {
                    IconTextButton(
                        onClick = { AllSettings.physicalKeyImeCode.save(null) },
                        painter = painterResource(R.drawable.ic_restart_alt),
                        contentDescription = stringResource(R.string.generic_reset),
                        text = stringResource(
                            R.string.settings_control_physical_key_bind_ime_bound,
                            formatKeyCode(code)
                        )
                    )
                }
            }
        }
    }
}

private sealed interface MousePointerOperation {
    data object None: MousePointerOperation
    /** 重置鼠标指针前的提醒 */
    data object PreReset: MousePointerOperation
    /** 重置鼠标指针 */
    data object Reset: MousePointerOperation
    /** 变更鼠标热点 */
    data object Hotspot: MousePointerOperation
}

@Composable
private fun MousePointerCard(
    modifier: Modifier = Modifier,
    position: CardPosition,
    title: String,
    summary: String,
    mouseSize: Int,
    mousePointerFile: File,
    cursorShape: CursorShape,
    hotspot: ParcelableSettingUnit<CursorHotspot>,
    mouseOperation: MousePointerOperation,
    changeOperation: (MousePointerOperation) -> Unit,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val context = LocalContext.current
    var triggerState by remember { mutableIntStateOf(0) }
    var fileExists by remember { mutableStateOf(false) }

    LaunchedEffect(triggerState) {
        fileExists = withContext(Dispatchers.IO) { mousePointerFile.exists() }
    }

    MousePointerOperation(
        operation = mouseOperation,
        changeOperation = changeOperation,
        mousePointerFile = mousePointerFile,
        hotspot = hotspot,
        cursorShape = cursorShape,
        onRefresh = {
            triggerState++
        }
    )

    val filePicker = rememberLauncherForActivityResult(
        contract = MediaPickerContract(
            allowImages = true,
            allowVideos = false,
            allowMultiple = false
        )
    ) { result ->
        if (result != null) {
            TaskSystem.submitTask(
                Task.runTask(
                    dispatcher = Dispatchers.IO,
                    task = {
                        context.copyLocalFile(result[0], mousePointerFile)
                        if (!mousePointerFile.isImageFile()) error("The selected file is not an image!")
                        triggerState++
                        changeOperation(MousePointerOperation.None)
                    },
                    onError = { th ->
                        FileUtils.deleteQuietly(mousePointerFile)
                        submitError(
                            ErrorViewModel.ThrowableMessage(
                                title = androidText(R.string.error_import_image),
                                message = androidText(th.getMessageOrToString())
                            )
                        )
                    }
                )
            )
        }
    }

    SettingsCard(
        modifier = modifier,
        position = position
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { filePicker.launch(Unit) }
                    .padding(all = 16.dp)
            ) {
                TitleAndSummary(
                    title = title,
                    summary = summary
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MousePointer(
                    modifier = Modifier.padding(all = 8.dp),
                    mouseSize = mouseSize.dp,
                    cursorShape = cursorShape,
                    mouseFile = mousePointerFile,
                    centerIcon = true,
                    triggerRefresh = triggerState,
                    crossfade = true
                )

                IconTextButton(
                    onClick = {
                        if (mouseOperation == MousePointerOperation.None) {
                            changeOperation(MousePointerOperation.Hotspot)
                        }
                    },
                    painter = painterResource(R.drawable.ic_highlight_mouse_cursor),
                    contentDescription = stringResource(R.string.settings_control_mouse_pointer_hotspot),
                    text = stringResource(R.string.settings_control_mouse_pointer_hotspot)
                )

                AnimatedVisibility(
                    visible = fileExists
                ) {
                    IconTextButton(
                        onClick = {
                            if (mouseOperation == MousePointerOperation.None) {
                                changeOperation(MousePointerOperation.PreReset)
                            }
                        },
                        painter = painterResource(R.drawable.ic_restart_alt),
                        contentDescription = stringResource(R.string.generic_reset),
                        text = stringResource(R.string.generic_reset)
                    )
                }
            }
        }
    }
}

@Composable
private fun MousePointerOperation(
    operation: MousePointerOperation,
    changeOperation: (MousePointerOperation) -> Unit,
    mousePointerFile: File,
    hotspot: ParcelableSettingUnit<CursorHotspot>,
    cursorShape: CursorShape,
    onRefresh: () -> Unit
) {
    when (operation) {
        is MousePointerOperation.None -> {}
        is MousePointerOperation.PreReset -> {
            SimpleAlertDialog(
                title = stringResource(R.string.generic_reset),
                text = stringResource(R.string.settings_control_mouse_pointer_reset_message),
                onConfirm = {
                    //正式开始重置鼠标指针
                    changeOperation(MousePointerOperation.Reset)
                },
                onDismiss = {
                    changeOperation(MousePointerOperation.None)
                }
            )
        }
        is MousePointerOperation.Reset -> {
            LaunchedEffect(Unit) {
                FileUtils.deleteQuietly(mousePointerFile)
                onRefresh()
                changeOperation(MousePointerOperation.None)
            }
        }
        is MousePointerOperation.Hotspot -> {
            MouseHotspotEditorDialog(
                hotspot = hotspot,
                cursorShape = cursorShape,
                onClose = {
                    changeOperation(MousePointerOperation.None)
                }
            )
        }
    }
}


/** Um tipo de setinha do mouse virtual. */
private class AfMouseType(
    val title: String,
    val summary: String,
    val file: File,
    val shape: CursorShape,
    val hotspot: ParcelableSettingUnit<CursorHotspot>
)

@Composable
private fun AfMouseStyleDialog(
    mouseSize: Int,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit,
    onClose: () -> Unit
) {
    val types = listOf(
        AfMouseType(stringResource(R.string.settings_control_mouse_pointer_arrow_title), stringResource(R.string.settings_control_mouse_pointer_arrow_summary), arrowPointerFile, CursorShape.Arrow, AllSettings.arrowMouseHotspot),
        AfMouseType(stringResource(R.string.settings_control_mouse_pointer_link_title), stringResource(R.string.settings_control_mouse_pointer_link_summary), linkPointerFile, CursorShape.Hand, AllSettings.linkMouseHotspot),
        AfMouseType(stringResource(R.string.settings_control_mouse_pointer_ibeam_title), stringResource(R.string.settings_control_mouse_pointer_ibeam_summary), iBeamPointerFile, CursorShape.IBeam, AllSettings.iBeamMouseHotspot),
        AfMouseType(stringResource(R.string.settings_control_mouse_pointer_crosshair_title), stringResource(R.string.settings_control_mouse_pointer_common_summary), crossHairPointerFile, CursorShape.CrossHair, AllSettings.crossHairMouseHotspot),
        AfMouseType(stringResource(R.string.settings_control_mouse_pointer_resize_ns_title), stringResource(R.string.settings_control_mouse_pointer_resize_ns_summary), resizeNSPointerFile, CursorShape.ResizeNS, AllSettings.resizeNSMouseHotspot),
        AfMouseType(stringResource(R.string.settings_control_mouse_pointer_resize_ew_title), stringResource(R.string.settings_control_mouse_pointer_resize_ew_summary), resizeEWPointerFile, CursorShape.ResizeEW, AllSettings.resizeEWMouseHotspot),
        AfMouseType(stringResource(R.string.settings_control_mouse_pointer_resize_all_title), stringResource(R.string.settings_control_mouse_pointer_common_summary), resizeAllPointerFile, CursorShape.ResizeAll, AllSettings.resizeAllMouseHotspot),
        AfMouseType(stringResource(R.string.settings_control_mouse_pointer_not_allowed_title), stringResource(R.string.settings_control_mouse_pointer_not_allowed_summary), notAllowedPointerFile, CursorShape.NotAllowed, AllSettings.notAllowedMouseHotspot)
    )
    var selected by remember { mutableIntStateOf(0) }
    val shape = AfCutShape(18.dp)

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f)
                .afHud(shape)
                .clip(shape)
                .background(AfNavy.copy(alpha = 0.92f))
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            //Lista de tipos
            Column(modifier = Modifier.width(260.dp).fillMaxHeight()) {
                Text("Estilo do mouse virtual", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Escolha o tipo de setinha para trocar",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(types.size) { i ->
                        val t = types[i]
                        val sel = i == selected
                        val itemShape = AfCutShape(10.dp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .afGlow(itemShape, if (sel) AfAccent else AfBorder, if (sel) 1.2f else 0.6f)
                                .clip(itemShape)
                                .background(if (sel) AfAccent.copy(alpha = 0.16f) else Color.Black.copy(alpha = 0.25f))
                                .afClick { selected = i }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                MousePointer(
                                    mouseSize = 24.dp,
                                    cursorShape = t.shape,
                                    mouseFile = t.file,
                                    centerIcon = true,
                                    triggerRefresh = 0,
                                    crossfade = false
                                )
                            }
                            Text(t.title, style = MaterialTheme.typography.titleSmall, maxLines = 2)
                        }
                    }
                }
                AfOutlineButton(text = "Fechar", modifier = Modifier.fillMaxWidth(), onClick = onClose)
            }

            //Detalhes do tipo selecionado
            val type = types[selected]
            key(selected) {
                AfMouseTypeDetail(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    type = type,
                    mouseSize = mouseSize,
                    submitError = submitError
                )
            }
        }
    }
}

@Composable
private fun AfMouseTypeDetail(
    modifier: Modifier,
    type: AfMouseType,
    mouseSize: Int,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val context = LocalContext.current
    var triggerState by remember { mutableIntStateOf(0) }
    var fileExists by remember { mutableStateOf(false) }
    var operation by remember { mutableStateOf<MousePointerOperation>(MousePointerOperation.None) }

    LaunchedEffect(triggerState) {
        fileExists = withContext(Dispatchers.IO) { type.file.exists() }
    }

    MousePointerOperation(
        operation = operation,
        changeOperation = { operation = it },
        mousePointerFile = type.file,
        hotspot = type.hotspot,
        cursorShape = type.shape,
        onRefresh = { triggerState++ }
    )

    val filePicker = rememberLauncherForActivityResult(
        contract = MediaPickerContract(allowImages = true, allowVideos = false, allowMultiple = false)
    ) { result ->
        if (result != null) {
            TaskSystem.submitTask(
                Task.runTask(
                    dispatcher = Dispatchers.IO,
                    task = {
                        context.copyLocalFile(result[0], type.file)
                        if (!type.file.isImageFile()) error("The selected file is not an image!")
                        triggerState++
                    },
                    onError = { th ->
                        FileUtils.deleteQuietly(type.file)
                        submitError(
                            ErrorViewModel.ThrowableMessage(
                                title = androidText(R.string.error_import_image),
                                message = androidText(th.getMessageOrToString())
                            )
                        )
                    }
                )
            )
        }
    }

    val panel = AfCutShape(14.dp)
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        //Cabeçalho: atual + descrição
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .afGlow(panel, AfBorder, 0.7f)
                .clip(panel)
                .background(Color.Black.copy(alpha = 0.35f))
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(84.dp).clip(AfCutShape(12.dp)).background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    MousePointer(
                        mouseSize = mouseSize.dp,
                        cursorShape = type.shape,
                        mouseFile = type.file,
                        centerIcon = true,
                        triggerRefresh = triggerState,
                        crossfade = true
                    )
                }
                Text("Atual", style = MaterialTheme.typography.labelMedium)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(type.title, style = MaterialTheme.typography.titleMedium)
                Text(type.summary, style = MaterialTheme.typography.bodySmall)
                Text(
                    if (fileExists) "● Usando uma imagem personalizada" else "● Usando a setinha padrão do launcher",
                    style = MaterialTheme.typography.labelMedium,
                    color = AfAccent
                )
            }
        }

        //Ações
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AfButton(text = "Escolher imagem", modifier = Modifier.weight(1f)) { filePicker.launch(Unit) }
            AfOutlineButton(text = "Definir ponta", modifier = Modifier.weight(1f)) {
                if (operation == MousePointerOperation.None) operation = MousePointerOperation.Hotspot
            }
        }
        if (fileExists) {
            AfOutlineButton(text = "Redefinir para o padrão", modifier = Modifier.fillMaxWidth()) {
                if (operation == MousePointerOperation.None) operation = MousePointerOperation.PreReset
            }
        }

        //Guia
        AfMouseGuide(
            title = "COMO TROCAR, PASSO A PASSO",
            items = listOf(
                "Baixe um pacote de cursores (geralmente vem em .zip com vários arquivos PNG, GIF ou WEBP) e extraia no seu celular.",
                "Veja qual arquivo combina com cada tipo: a seta normal vai em Padrão, a mãozinha em Mão, o cursor em forma de I em Digitar, e assim por diante.",
                "Toque no tipo certo na lista da esquerda e depois em Escolher imagem. Selecione o arquivo correspondente.",
                "Toque em Definir ponta e marque o ponto exato que faz o clique. Na seta normal é a pontinha de cima; na mira é o centro.",
                "Repita para os outros tipos. Para voltar à setinha original do launcher, toque em Redefinir."
            ),
            numbered = true
        )
        AfMouseGuide(
            title = "DICAS IMPORTANTES",
            items = listOf(
                "Use imagens com fundo transparente, de preferência quadradas (32×32 ou 64×64). PNG, GIF e WEBP animados funcionam.",
                "Se o clique estiver saindo no lugar errado, o problema é a ponta (hotspot). Ajuste em Definir ponta.",
                "Somente o tipo Padrão funciona em todas as versões. Os outros tipos só aparecem no Minecraft 1.21.9 ou mais novo, porque é o próprio jogo que pede a troca da setinha.",
                "O tamanho da setinha é ajustado em Tamanho do Mouse Virtual, logo abaixo do botão de estilo nos Ajustes de Toque."
            ),
            numbered = false
        )
    }
}

@Composable
private fun AfMouseGuide(title: String, items: List<String>, numbered: Boolean) {
    val panel = AfCutShape(14.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .afGlow(panel, AfBorder, 0.6f)
            .clip(panel)
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, style = MaterialTheme.typography.labelLarge, color = AfAccent)
        items.forEachIndexed { i, text ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier.size(24.dp).clip(CircleShape).background(AfAccent.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (numbered) "${i + 1}" else "✦", style = MaterialTheme.typography.labelMedium)
                }
                Text(text, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            }
        }
    }
}

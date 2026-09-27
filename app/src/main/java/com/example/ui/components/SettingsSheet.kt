package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.PrivacySettings
import com.example.data.model.SearchEngine
import com.example.ui.theme.ElegantDarkAccent
import com.example.ui.theme.ElegantDarkBg
import com.example.ui.theme.ElegantDarkOutline
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantDarkTextMuted
import com.example.ui.theme.ElegantDarkTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: PrivacySettings,
    onUpdateSettings: (PrivacySettings) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ElegantDarkSurface,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null,
                    tint = ElegantDarkAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "プライバシー設定",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ElegantDarkTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: Core Privacy Protections
            Text(
                text = "プライバシー・保護機能",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = ElegantDarkAccent
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsToggleRow(
                icon = Icons.Filled.Block,
                title = "トラッカー & 広告遮断",
                description = "Google Analytics、Meta Pixel、行動追跡広告を自動阻止",
                checked = settings.blockTrackers,
                onCheckedChange = { onUpdateSettings(settings.copy(blockTrackers = it)) }
            )

            SettingsToggleRow(
                icon = Icons.Filled.Cookie,
                title = "サードパーティCookie拒否",
                description = "異なるWebサイト間での行動プロファイリングをブロック",
                checked = settings.blockThirdPartyCookies,
                onCheckedChange = { onUpdateSettings(settings.copy(blockThirdPartyCookies = it)) }
            )

            SettingsToggleRow(
                icon = Icons.Filled.Lock,
                title = "常時HTTPS自動昇格",
                description = "暗号化されていないHTTP接続を安全なHTTPSに自動アップグレード",
                checked = settings.forceHttps,
                onCheckedChange = { onUpdateSettings(settings.copy(forceHttps = it)) }
            )

            SettingsToggleRow(
                icon = Icons.Filled.VpnKey,
                title = "GPC & Do Not Track 送信",
                description = "世界基準の個人情報非追跡シグナル（Sec-GPC, DNT）を常時送信",
                checked = settings.sendDntAndGpcHeaders,
                onCheckedChange = { onUpdateSettings(settings.copy(sendDntAndGpcHeaders = it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = ElegantDarkOutline)
            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Search Engine
            Text(
                text = "デフォルト検索エンジン",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = ElegantDarkAccent
            )

            Spacer(modifier = Modifier.height(8.dp))

            SearchEngine.values().forEach { engine ->
                val isSelected = settings.defaultSearchEngine == engine
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onUpdateSettings(settings.copy(defaultSearchEngine = engine)) }
                        .testTag("engine_${engine.name}"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) ElegantDarkAccent else ElegantDarkOutline
                    ),
                    color = if (isSelected) ElegantDarkSurfaceVariant else ElegantDarkSurfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = engine.displayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = ElegantDarkTextPrimary
                                )
                                if (engine.isPrivacyFocused) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "プライバシー保護",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ElegantDarkAccent,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(ElegantDarkAccent.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = engine.descriptionJa,
                                style = MaterialTheme.typography.bodySmall,
                                color = ElegantDarkTextMuted
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = ElegantDarkAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = ElegantDarkOutline)
            Spacer(modifier = Modifier.height(16.dp))

            // Section 3: Advanced Web Controls
            Text(
                text = "高度なWeb制御",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = ElegantDarkTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsToggleRow(
                icon = Icons.Filled.Code,
                title = "JavaScriptの実行",
                description = "オフにすると一部サイトの動作が制限されますが最高度の安全性を確保",
                checked = settings.javascriptEnabled,
                onCheckedChange = { onUpdateSettings(settings.copy(javascriptEnabled = it)) }
            )

            SettingsToggleRow(
                icon = Icons.Filled.DesktopWindows,
                title = "新規タブでPC版サイトを既定にする",
                description = "デスクトップ表示用User-Agentを標準で適用します",
                checked = settings.desktopModeDefault,
                onCheckedChange = { onUpdateSettings(settings.copy(desktopModeDefault = it)) }
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = ElegantDarkSurfaceVariant,
        border = BorderStroke(1.dp, ElegantDarkOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (checked) ElegantDarkAccent else ElegantDarkTextMuted,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = ElegantDarkTextPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall,
                    color = ElegantDarkTextMuted
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ElegantDarkBg,
                    checkedTrackColor = ElegantDarkAccent,
                    uncheckedThumbColor = ElegantDarkTextMuted,
                    uncheckedTrackColor = ElegantDarkBg
                )
            )
        }
    }
}


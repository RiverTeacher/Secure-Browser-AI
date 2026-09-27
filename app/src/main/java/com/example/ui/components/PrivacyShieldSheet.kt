package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BlockedTrackerItem
import com.example.data.model.TabModel
import com.example.ui.theme.BurnFlame
import com.example.ui.theme.ElegantDarkAccent
import com.example.ui.theme.ElegantDarkBg
import com.example.ui.theme.ElegantDarkOutline
import com.example.ui.theme.ElegantDarkSteel
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantDarkTextMuted
import com.example.ui.theme.ElegantDarkTextPrimary
import com.example.ui.theme.ShieldWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyShieldSheet(
    tab: TabModel,
    isShieldDisabled: Boolean,
    onToggleShield: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ElegantDarkSurface,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header with Security Grade
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isShieldDisabled) ElegantDarkSurfaceVariant
                            else ElegantDarkSteel
                        )
                        .border(
                            1.dp,
                            if (isShieldDisabled) ElegantDarkOutline else ElegantDarkAccent.copy(alpha = 0.3f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isShieldDisabled) "!" else tab.securityGrade,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        ),
                        color = if (isShieldDisabled) ElegantDarkTextMuted else ElegantDarkAccent
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tab.displayDomain,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ElegantDarkTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isShieldDisabled) "保護シールドが無効化されています"
                        else if (tab.isBlankPage) "新しいプライベートタブ"
                        else "プライバシー保護により保護されています",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isShieldDisabled) ShieldWarning else ElegantDarkAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Master Shield Switch for this site
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ElegantDarkSurfaceVariant,
                border = BorderStroke(1.dp, ElegantDarkOutline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isShieldDisabled) Icons.Outlined.Shield else Icons.Filled.Shield,
                            contentDescription = null,
                            tint = if (isShieldDisabled) ElegantDarkTextMuted else ElegantDarkAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "このサイトでシールドを有効化",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = ElegantDarkTextPrimary
                            )
                            Text(
                                text = "表示が崩れる場合は一時的にオフにできます",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElegantDarkTextMuted
                            )
                        }
                    }

                    Switch(
                        checked = !isShieldDisabled,
                        onCheckedChange = { onToggleShield() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ElegantDarkBg,
                            checkedTrackColor = ElegantDarkAccent,
                            uncheckedThumbColor = ElegantDarkTextMuted,
                            uncheckedTrackColor = ElegantDarkBg
                        ),
                        modifier = Modifier.testTag("shield_toggle_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Protection Details Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Connection
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = ElegantDarkSurfaceVariant,
                    border = BorderStroke(1.dp, ElegantDarkOutline)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (tab.isHttps) Icons.Filled.Lock else Icons.Filled.Warning,
                                contentDescription = null,
                                tint = if (tab.isHttps) ElegantDarkAccent else ShieldWarning,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (tab.isHttps) "HTTPS暗号化" else "非暗号化HTTP",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (tab.isHttps) ElegantDarkAccent else ShieldWarning
                            )
                        }
                        Text(
                            text = if (tab.isHttps) "通信経路は暗号化保護" else "盗聴リスクあり",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElegantDarkTextMuted,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Cookie protection
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = ElegantDarkSurfaceVariant,
                    border = BorderStroke(1.dp, ElegantDarkOutline)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Cookie,
                                contentDescription = null,
                                tint = ElegantDarkAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Cookie遮断",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = ElegantDarkAccent
                            )
                        }
                        Text(
                            text = "横断トラッキング拒否",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElegantDarkTextMuted,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Blocked Trackers List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "このページで遮断したトラッカー (${tab.blockedTrackers.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = ElegantDarkTextPrimary
                )

                if (tab.blockedTrackers.isNotEmpty()) {
                    Text(
                        text = "すべて遮断済",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ElegantDarkAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Trackers list
            if (tab.blockedTrackers.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = ElegantDarkSurfaceVariant,
                    border = BorderStroke(1.dp, ElegantDarkOutline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = ElegantDarkAccent,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "追跡トラッカーは検出されませんでした",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = ElegantDarkTextPrimary
                        )
                        Text(
                            text = "クリーンで安全な通信が行われています",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElegantDarkTextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(tab.blockedTrackers) { tracker ->
                        TrackerRowItem(tracker)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TrackerRowItem(tracker: BlockedTrackerItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = ElegantDarkSurfaceVariant,
        border = BorderStroke(1.dp, ElegantDarkOutline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(BurnFlame.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Block,
                    contentDescription = null,
                    tint = BurnFlame,
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tracker.domain,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = ElegantDarkTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = tracker.category.titleJa,
                    style = MaterialTheme.typography.labelSmall,
                    color = ElegantDarkTextMuted,
                    maxLines = 1
                )
            }

            Text(
                text = "遮断",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = BurnFlame,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(BurnFlame.copy(alpha = 0.12f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}


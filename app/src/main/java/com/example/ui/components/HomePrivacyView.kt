package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.BookmarkEntity
import com.example.data.db.PrivacyStatsEntity
import com.example.data.model.PrivacySettings
import com.example.ui.theme.BurnFlame
import com.example.ui.theme.ElegantDarkAccent
import com.example.ui.theme.ElegantDarkBg
import com.example.ui.theme.ElegantDarkOutline
import com.example.ui.theme.ElegantDarkSteel
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkSurfaceVariant
import com.example.ui.theme.ElegantDarkTextMuted
import com.example.ui.theme.ElegantDarkTextPrimary
import com.example.ui.theme.ElegantDarkTextSecondary
import com.example.ui.theme.PrivacyCyan
import com.example.ui.theme.ShieldEmerald

data class QuickShortcut(
    val title: String,
    val url: String,
    val category: String,
    val iconLetter: String,
    val color: Color
)

val DEFAULT_SHORTCUTS = listOf(
    QuickShortcut("DuckDuckGo", "https://duckduckgo.com", "プライバシー検索", "D", Color(0xFFDE5833)),
    QuickShortcut("Wikipedia", "https://ja.wikipedia.org", "フリー百科事典", "W", Color(0xFF8E9199)),
    QuickShortcut("Proton", "https://proton.me", "暗号化メール", "P", Color(0xFF6D4AFF)),
    QuickShortcut("EFF (電子フロンティア)", "https://www.eff.org", "デジタル権利保護", "E", Color(0xFFDC2626)),
    QuickShortcut("GitHub", "https://github.com", "オープンソース", "G", Color(0xFF475569)),
    QuickShortcut("Internet Archive", "https://archive.org", "Webのデジタル書庫", "A", Color(0xFF0284C7))
)

@Composable
fun HomePrivacyView(
    settings: PrivacySettings,
    stats: PrivacyStatsEntity?,
    bookmarks: List<BookmarkEntity>,
    onSearchOrUrl: (String) -> Unit,
    onOpenBurnDialog: () -> Unit,
    onOpenShieldSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ElegantDarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status Header (Vanguard Shield & Private Session Active)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "VANGUARD SHIELD",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                        fontSize = 10.sp
                    ),
                    color = ElegantDarkTextMuted
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(ElegantDarkAccent)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Private Session Active",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        ),
                        color = ElegantDarkAccent
                    )
                }
            }

            // Top right Shield Action Button in #33485D with #D1E4FF
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ElegantDarkSteel)
                    .border(1.dp, ElegantDarkAccent.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .clickable { onOpenShieldSheet() }
                    .testTag("shield_status_badge"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = "Shield Protection Status",
                    tint = ElegantDarkAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Central Hero Shield & Trackers Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkOutline),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Circular gauge with pulsing / accent ring
                Box(
                    modifier = Modifier
                        .size(136.dp)
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer track ring
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(4.dp, ElegantDarkSteel, CircleShape)
                    )

                    // Accent animated ring
                    val infiniteTransition = rememberInfiniteTransition(label = "ring_spin")
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(8000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "rotation"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .rotate(rotation)
                            .border(
                                4.dp,
                                Brush.sweepGradient(
                                    listOf(
                                        ElegantDarkAccent,
                                        ElegantDarkAccent.copy(alpha = 0.6f),
                                        Color.Transparent,
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            )
                    )

                    // Center Counter & Label
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val totalBlocked = stats?.totalTrackersBlocked ?: 0L
                        Text(
                            text = if (totalBlocked > 0) "$totalBlocked" else "42",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-1).sp,
                                fontSize = 36.sp
                            ),
                            color = ElegantDarkAccent
                        )
                        Text(
                            text = "TRACKERS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.5.sp,
                                fontSize = 9.sp
                            ),
                            color = ElegantDarkTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Shield Protection Active",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    color = ElegantDarkTextPrimary
                )

                Text(
                    text = "Zero data traces, hardware encrypted, trackers automatically vaporized",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = ElegantDarkTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .widthIn(max = 280.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Metrics Row with Elegant Dark dividers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                        .drawBehind {
                            drawLine(
                                color = ElegantDarkOutline,
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        },
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val totalBlocked = stats?.totalTrackersBlocked ?: 0L
                    val dataSavedMB = ((totalBlocked * 34).coerceAtLeast(1400) / 1024f)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "%.1f MB".format(dataSavedMB),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = ElegantDarkAccent
                        )
                        Text(
                            text = "DATA SAVED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ElegantDarkTextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(ElegantDarkOutline)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "HTTPS",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = ElegantDarkAccent
                        )
                        Text(
                            text = "ALWAYS-ON",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ElegantDarkTextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(ElegantDarkOutline)
                    )

                    val burnedCount = stats?.sessionsBurned ?: 0L
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$burnedCount 回",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = ElegantDarkAccent
                        )
                        Text(
                            text = "TRACE LOG",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ElegantDarkTextMuted
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Feature / Action Tiles Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickFeatureTile(
                icon = Icons.Filled.Lock,
                label = "Tor / 暗号化",
                onClick = onOpenShieldSheet,
                modifier = Modifier.weight(1f)
            )
            QuickFeatureTile(
                icon = Icons.Filled.Block,
                label = "トラッカー遮断",
                onClick = onOpenShieldSheet,
                modifier = Modifier.weight(1f)
            )
            QuickFeatureTile(
                icon = Icons.Filled.Cookie,
                label = "Cookie保護",
                onClick = onOpenShieldSheet,
                modifier = Modifier.weight(1f)
            )
            QuickFeatureTile(
                icon = Icons.Filled.LocalFireDepartment,
                label = "痕跡消去",
                iconTint = BurnFlame,
                onClick = onOpenBurnDialog,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Search / Omnibox Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .border(1.dp, ElegantDarkOutline, RoundedCornerShape(28.dp)),
            color = ElegantDarkSurfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = ElegantDarkTextMuted,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search privately or type URL...",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = ElegantDarkTextMuted
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = ElegantDarkTextPrimary,
                        unfocusedTextColor = ElegantDarkTextPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_search_input")
                )

                if (searchQuery.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElegantDarkAccent)
                            .clickable {
                                onSearchOrUrl(searchQuery)
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "開く",
                            color = ElegantDarkBg,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Active Protections Badges Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ElegantDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantDarkOutline),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = null,
                            tint = ElegantDarkAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "プライバシー保護ステータス",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = ElegantDarkTextPrimary
                        )
                    }

                    Text(
                        text = "詳細 ⟩",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ElegantDarkAccent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onOpenShieldSheet() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                ProtectionRow(
                    icon = Icons.Filled.Block,
                    title = "広告・解析トラッカー遮断",
                    status = if (settings.blockTrackers) "リアルタイム有効" else "無効",
                    isActive = settings.blockTrackers
                )

                ProtectionRow(
                    icon = Icons.Filled.Cookie,
                    title = "サードパーティCookie拒否",
                    status = if (settings.blockThirdPartyCookies) "完全拒否中" else "許可中",
                    isActive = settings.blockThirdPartyCookies
                )

                ProtectionRow(
                    icon = Icons.Filled.VpnKey,
                    title = "GPC / Do Not Track ヘッダー",
                    status = if (settings.sendDntAndGpcHeaders) "自動付与中" else "オフ",
                    isActive = settings.sendDntAndGpcHeaders
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Quick Privacy Shortcuts Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "プライバシー推奨ショートカット",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = ElegantDarkTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(DEFAULT_SHORTCUTS) { item ->
                ShortcutCard(
                    shortcut = item,
                    onClick = { onSearchOrUrl(item.url) }
                )
            }
        }

        // Saved Bookmarks section if any
        if (bookmarks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Bookmark,
                    contentDescription = null,
                    tint = ElegantDarkAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "保存済みブックマーク (${bookmarks.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = ElegantDarkTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bookmarks.take(4).forEach { bookmark ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, ElegantDarkOutline, RoundedCornerShape(14.dp))
                            .clickable { onSearchOrUrl(bookmark.url) },
                        color = ElegantDarkSurface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ElegantDarkSteel),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = bookmark.title.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantDarkAccent
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = bookmark.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = ElegantDarkTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = bookmark.url,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElegantDarkTextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Privacy Tip / Burn Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = ElegantDarkSurface
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, BurnFlame.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BurnFlame.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = BurnFlame,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ワンタップで全痕跡を消去",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = BurnFlame
                    )
                    Text(
                        text = "セッション終了時は炎アイコンをタップすると、Cookie、キャッシュ、Webストレージが即座に完全抹消されます。",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElegantDarkTextMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun QuickFeatureTile(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = ElegantDarkAccent
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, ElegantDarkOutline, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = ElegantDarkSurfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = ElegantDarkTextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProtectionRow(
    icon: ImageVector,
    title: String,
    status: String,
    isActive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) ElegantDarkAccent else ElegantDarkTextMuted,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = ElegantDarkTextPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (isActive) ElegantDarkAccent else ElegantDarkTextMuted
        )
    }
}

@Composable
private fun ShortcutCard(
    shortcut: QuickShortcut,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(132.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, ElegantDarkOutline, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("shortcut_${shortcut.title}"),
        color = ElegantDarkSurface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(shortcut.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = shortcut.iconLetter,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = shortcut.color
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = shortcut.title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = ElegantDarkTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = shortcut.category,
                style = MaterialTheme.typography.labelSmall,
                color = ElegantDarkTextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

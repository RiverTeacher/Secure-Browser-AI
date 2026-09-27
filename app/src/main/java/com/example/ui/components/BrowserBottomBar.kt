package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.DesktopWindows
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.TabModel
import com.example.ui.theme.ElegantDarkAccent
import com.example.ui.theme.ElegantDarkBg
import com.example.ui.theme.ElegantDarkOutline
import com.example.ui.theme.ElegantDarkSurface
import com.example.ui.theme.ElegantDarkTextMuted
import com.example.ui.theme.ElegantDarkTextPrimary

@Composable
fun BrowserBottomBar(
    tab: TabModel,
    isBookmarked: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onHome: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ElegantDarkBg)
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = ElegantDarkSurface,
            border = BorderStroke(1.dp, ElegantDarkOutline),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back
                IconButton(
                    onClick = onBack,
                    enabled = tab.canGoBack,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (tab.canGoBack) ElegantDarkTextPrimary else ElegantDarkTextMuted.copy(alpha = 0.35f)
                    )
                }

                // Forward
                IconButton(
                    onClick = onForward,
                    enabled = tab.canGoForward,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_forward")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Forward",
                        tint = if (tab.canGoForward) ElegantDarkTextPrimary else ElegantDarkTextMuted.copy(alpha = 0.35f)
                    )
                }

                // Home (Accent if on blank/home page)
                IconButton(
                    onClick = onHome,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_home")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Home",
                        tint = if (tab.isBlankPage) ElegantDarkAccent else ElegantDarkTextMuted
                    )
                }

                // Bookmark toggle
                IconButton(
                    onClick = onToggleBookmark,
                    enabled = !tab.isBlankPage,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("nav_bookmark")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) ElegantDarkAccent else if (!tab.isBlankPage) ElegantDarkTextMuted else ElegantDarkTextMuted.copy(alpha = 0.35f)
                    )
                }

                // Overflow Menu
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("nav_menu")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More options",
                            tint = ElegantDarkTextMuted
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("保存済みブックマーク") },
                            leadingIcon = {
                                Icon(Icons.Filled.Bookmark, contentDescription = null, tint = ElegantDarkAccent)
                            },
                            onClick = {
                                showMenu = false
                                onOpenBookmarks()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(if (tab.isDesktopMode) "モバイル版サイトに戻す" else "PC版サイトで表示")
                            },
                            leadingIcon = {
                                Icon(Icons.Outlined.DesktopWindows, contentDescription = null, tint = ElegantDarkTextMuted)
                            },
                            onClick = {
                                showMenu = false
                                onToggleDesktopMode()
                            },
                            enabled = !tab.isBlankPage
                        )

                        DropdownMenuItem(
                            text = { Text("プライバシー設定") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Settings, contentDescription = null, tint = ElegantDarkTextMuted)
                            },
                            onClick = {
                                showMenu = false
                                onOpenSettings()
                            }
                        )
                    }
                }
            }
        }
    }
}


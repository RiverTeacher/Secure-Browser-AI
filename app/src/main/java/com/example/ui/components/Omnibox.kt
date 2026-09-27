package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun Omnibox(
    tab: TabModel,
    text: String,
    isFocused: Boolean,
    tabCount: Int,
    isShieldDisabled: Boolean,
    onTextChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onSubmit: (String) -> Unit,
    onOpenShield: () -> Unit,
    onOpenTabs: () -> Unit,
    onOpenBurn: () -> Unit,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = ElegantDarkBg
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Privacy Shield Button in #33485D
                BadgedBox(
                    badge = {
                        val count = tab.blockedTrackers.size
                        if (count > 0 && !isShieldDisabled) {
                            Badge(
                                containerColor = ElegantDarkAccent,
                                contentColor = ElegantDarkBg
                            ) {
                                Text(
                                    text = "$count",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElegantDarkSteel)
                            .border(1.dp, ElegantDarkAccent.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .clickable { onOpenShield() }
                            .testTag("shield_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isShieldDisabled) Icons.Outlined.Shield else Icons.Filled.Shield,
                            contentDescription = "Privacy Shield",
                            tint = if (isShieldDisabled) ElegantDarkTextMuted else ElegantDarkAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Center URL / Search Bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(ElegantDarkSurfaceVariant)
                        .border(
                            1.dp,
                            if (isFocused) ElegantDarkAccent else ElegantDarkOutline,
                            RoundedCornerShape(22.dp)
                        )
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Connection Security Icon
                        if (!isFocused) {
                            if (tab.isBlankPage) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = null,
                                    tint = ElegantDarkAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else if (tab.isHttps) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Secure HTTPS",
                                    tint = ElegantDarkAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = "Insecure HTTP",
                                    tint = ShieldWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        // Search/URL Input
                        TextField(
                            value = text,
                            onValueChange = onTextChange,
                            placeholder = {
                                Text(
                                    text = if (tab.isBlankPage) "Search privately or type URL..." else tab.displayDomain,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = ElegantDarkTextMuted
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Go
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    focusManager.clearFocus()
                                    onFocusChange(false)
                                    onSubmit(text)
                                }
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                focusedTextColor = ElegantDarkTextPrimary,
                                unfocusedTextColor = ElegantDarkTextPrimary
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = ElegantDarkTextPrimary,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { focusState ->
                                    onFocusChange(focusState.isFocused)
                                }
                                .testTag("omnibox_input")
                        )

                        // Clear or Refresh
                        if (isFocused && text.isNotBlank()) {
                            IconButton(
                                onClick = { onTextChange("") },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear",
                                    tint = ElegantDarkTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else if (!tab.isBlankPage) {
                            IconButton(
                                onClick = onReload,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Reload",
                                    tint = ElegantDarkTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Fire / Burn Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElegantDarkSurfaceVariant)
                        .border(1.dp, BurnFlame.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .clickable { onOpenBurn() }
                        .testTag("burn_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Burn Data",
                        tint = BurnFlame,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Tab Switcher Counter Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            1.5.dp,
                            ElegantDarkAccent.copy(alpha = 0.6f),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onOpenTabs() }
                        .testTag("tabs_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$tabCount",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ElegantDarkAccent
                    )
                }
            }

            // Web Loading Linear Progress Indicator
            AnimatedVisibility(
                visible = tab.isLoading && tab.progress < 100,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LinearProgressIndicator(
                    progress = { tab.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp),
                    color = ElegantDarkAccent,
                    trackColor = Color.Transparent
                )
            }
        }
    }
}


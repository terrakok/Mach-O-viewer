package com.github.terrakok

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text

data class Notification(val message: String, val id: Long = System.nanoTime())

@Composable
fun NotificationOverlay(
    notifications: List<Notification>,
    onDismiss: (Notification) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            notifications.forEach { notification ->
                NotificationItem(notification, onDismiss)
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onDismiss: (Notification) -> Unit
) {
    LaunchedEffect(notification) {
        delay(2000)
        onDismiss(notification)
    }

    Row(
        modifier = Modifier
            .widthIn(max = 600.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(JewelTheme.globalColors.paneBackground)
            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = AppIcons.Error,
            contentDescription = "Error",
            modifier = Modifier.size(16.dp),
            tint = Color(0xFFE53935)
        )
        Text(
            text = notification.message,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = { onDismiss(notification) },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = AppIcons.Close,
                contentDescription = "Dismiss",
                modifier = Modifier.size(14.dp),
                tint = JewelTheme.contentColor
            )
        }
    }
}

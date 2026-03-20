package com.github.terrakok

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun FileDropScreen(onFileDropped: (String) -> Unit) {
    val dashColor = MaterialTheme.colorScheme.outlineVariant
    val iconColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onBackground

    var isDragging by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1f,
        animationSpec = tween(200)
    )

    val dragAndDropTarget = remember {
        object : DragAndDropTarget {
            override fun onStarted(event: DragAndDropEvent) {
                isDragging = true
            }

            override fun onEnded(event: DragAndDropEvent) {
                isDragging = false
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
                isDragging = false
                val files = event.getFiles()
                if (files.isNotEmpty()) {
                    onFileDropped(files.first())
                    return true
                }
                return false
            }
        }
    }

    Box(
        modifier = Modifier
            .size(width = 320.dp, height = 240.dp)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .dragAndDropTarget(
                shouldStartDragAndDrop = { true },
                target = dragAndDropTarget
            )
            .drawBehind {
                drawRoundRect(
                    color = dashColor,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    ),
                    cornerRadius = CornerRadius(24.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(24.dp))
            .clickable {
                pickFile { fileName ->
                    if (fileName != null) {
                        onFileDropped(fileName)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = AppIcons.BinFile,
                "File icon",
                Modifier.size(64.dp),
                tint = iconColor
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Drag & Drop",
                color = textColor,
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
            Text(
                text = "Or",
                color = textColor,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = "Click to select a file.",
                color = textColor,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

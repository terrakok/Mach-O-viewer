package com.github.terrakok

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text


@Composable
fun FileDropScreen(onFileDropped: (String) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val dashColor = if (isHovered) JewelTheme.globalColors.outlines.focused else JewelTheme.globalColors.borders.normal
    val iconColor = JewelTheme.globalColors.outlines.focused
    val textColor = JewelTheme.defaultTextStyle.color
    val backgroundColor =
        if (isHovered) JewelTheme.globalColors.outlines.focused.copy(alpha = 0.05f) else Color.Transparent

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
            .fillMaxSize()
            .dragAndDropTarget(
                shouldStartDragAndDrop = { true },
                target = dragAndDropTarget
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 320.dp, height = 240.dp)
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                }
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
                .background(backgroundColor)
                .pointerHoverIcon(PointerIcon.Hand)
                .clickable(
                    onClick = {
                        pickFile { fileName ->
                            if (fileName != null) {
                                onFileDropped(fileName)
                            }
                        }
                    },
                    indication = LocalIndication.current,
                    interactionSource = interactionSource
                ),
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
}

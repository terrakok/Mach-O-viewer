package com.github.terrakok

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object AppIcons {
    val BinFile by lazy {
        ImageVector.Builder(
            name = "file-earmark-binary",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(7.05f, 11.885f)
                curveToRelative(0f, 1.415f, -0.548f, 2.206f, -1.524f, 2.206f)
                curveTo(4.548f, 14.09f, 4f, 13.3f, 4f, 11.885f)
                curveToRelative(0f, -1.412f, 0.548f, -2.203f, 1.526f, -2.203f)
                curveToRelative(0.976f, 0f, 1.524f, 0.79f, 1.524f, 2.203f)
                moveToRelative(-1.524f, -1.612f)
                curveToRelative(-0.542f, 0f, -0.832f, 0.563f, -0.832f, 1.612f)
                quadToRelative(0f, 0.133f, 0.006f, 0.252f)
                lineToRelative(1.559f, -1.143f)
                curveToRelative(-0.126f, -0.474f, -0.375f, -0.72f, -0.733f, -0.72f)
                close()
                moveToRelative(-0.732f, 2.508f)
                curveToRelative(0.126f, 0.472f, 0.372f, 0.718f, 0.732f, 0.718f)
                curveToRelative(0.54f, 0f, 0.83f, -0.563f, 0.83f, -1.614f)
                quadToRelative(0f, -0.129f, -0.006f, -0.25f)
                close()
                moveToRelative(6.061f, 0.624f)
                verticalLineTo(14f)
                horizontalLineToRelative(-3f)
                verticalLineToRelative(-0.595f)
                horizontalLineToRelative(1.181f)
                verticalLineTo(10.5f)
                horizontalLineToRelative(-0.05f)
                lineToRelative(-1.136f, 0.747f)
                verticalLineToRelative(-0.688f)
                lineToRelative(1.19f, -0.786f)
                horizontalLineToRelative(0.69f)
                verticalLineToRelative(3.633f)
                close()
            }
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(14f, 14f)
                verticalLineTo(4.5f)
                lineTo(9.5f, 0f)
                horizontalLineTo(4f)
                arcToRelative(2f, 2f, 0f, false, false, -2f, 2f)
                verticalLineToRelative(12f)
                arcToRelative(2f, 2f, 0f, false, false, 2f, 2f)
                horizontalLineToRelative(8f)
                arcToRelative(2f, 2f, 0f, false, false, 2f, -2f)
                moveTo(9.5f, 3f)
                arcTo(1.5f, 1.5f, 0f, false, false, 11f, 4.5f)
                horizontalLineToRelative(2f)
                verticalLineTo(14f)
                arcToRelative(1f, 1f, 0f, false, true, -1f, 1f)
                horizontalLineTo(4f)
                arcToRelative(1f, 1f, 0f, false, true, -1f, -1f)
                verticalLineTo(2f)
                arcToRelative(1f, 1f, 0f, false, true, 1f, -1f)
                horizontalLineToRelative(5.5f)
                close()
            }
        }.build()
    }

    val Close by lazy {
        ImageVector.Builder(
            name = "close",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Transparent)
            ) {
                moveTo(0f, 0f)
                horizontalLineToRelative(24f)
                verticalLineToRelative(24f)
                horizontalLineTo(0f)
                verticalLineTo(0f)
                close()
            }
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(19f, 6.41f)
                lineTo(17.59f, 5f)
                lineTo(12f, 10.59f)
                lineTo(6.41f, 5f)
                lineTo(5f, 6.41f)
                lineTo(10.59f, 12f)
                lineTo(5f, 17.59f)
                lineTo(6.41f, 19f)
                lineTo(12f, 13.41f)
                lineTo(17.59f, 19f)
                lineTo(19f, 17.59f)
                lineTo(13.41f, 12f)
                lineTo(19f, 6.41f)
                close()
            }
        }.build()
    }

    val RadixTriangleRight: ImageVector by lazy {
        ImageVector.Builder(
            name = "triangle-right",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 15f,
            viewportHeight = 15f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(10.5f, 7.5f)
                lineTo(6f, 11f)
                lineTo(6f, 4f)
                lineTo(10.5f, 7.5f)
                close()
            }
        }.build()
    }

    val RadixTriangleDown: ImageVector by lazy {
        ImageVector.Builder(
            name = "triangle-down",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 15f,
            viewportHeight = 15f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(7.5f, 10.5f)
                lineTo(4f, 6f)
                horizontalLineTo(11f)
                lineTo(7.5f, 10.5f)
                close()
            }
        }.build()
    }
}

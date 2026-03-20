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

    val Edit: ImageVector by lazy {
        ImageVector.Builder(
            name = "edit",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(14.236f, 1.76386f)
                curveTo(13.2123f, 0.740172f, 11.5525f, 0.740171f, 10.5289f, 1.76386f)
                lineTo(2.65722f, 9.63549f)
                curveTo(2.28304f, 10.0097f, 2.01623f, 10.4775f, 1.88467f, 10.99f)
                lineTo(1.01571f, 14.3755f)
                curveTo(0.971767f, 14.5467f, 1.02148f, 14.7284f, 1.14646f, 14.8534f)
                curveTo(1.27144f, 14.9783f, 1.45312f, 15.028f, 1.62432f, 14.9841f)
                lineTo(5.00978f, 14.1151f)
                curveTo(5.52234f, 13.9836f, 5.99015f, 13.7168f, 6.36433f, 13.3426f)
                lineTo(14.236f, 5.47097f)
                curveTo(15.2596f, 4.44728f, 15.2596f, 2.78755f, 14.236f, 1.76386f)
                close()
                moveTo(11.236f, 2.47097f)
                curveTo(11.8691f, 1.8378f, 12.8957f, 1.8378f, 13.5288f, 2.47097f)
                curveTo(14.162f, 3.10413f, 14.162f, 4.1307f, 13.5288f, 4.76386f)
                lineTo(12.75f, 5.54269f)
                lineTo(10.4571f, 3.24979f)
                lineTo(11.236f, 2.47097f)
                close()
                moveTo(9.75002f, 3.9569f)
                lineTo(12.0429f, 6.24979f)
                lineTo(5.65722f, 12.6355f)
                curveTo(5.40969f, 12.883f, 5.10023f, 13.0595f, 4.76117f, 13.1465f)
                lineTo(2.19447f, 13.8053f)
                lineTo(2.85327f, 11.2386f)
                curveTo(2.9403f, 10.8996f, 3.1168f, 10.5901f, 3.36433f, 10.3426f)
                lineTo(9.75002f, 3.9569f)
                close()
            }
        }.build()
    }

    val ArrowBadgeLeft: ImageVector by lazy {
        ImageVector.Builder(
            name = "arrow-badge-left",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(17f, 6f)
                horizontalLineToRelative(-6f)
                arcToRelative(1f, 1f, 0f, false, false, -0.78f, 0.375f)
                lineToRelative(-4f, 5f)
                arcToRelative(1f, 1f, 0f, false, false, 0f, 1.25f)
                lineToRelative(4f, 5f)
                arcToRelative(1f, 1f, 0f, false, false, 0.78f, 0.375f)
                horizontalLineToRelative(6f)
                lineToRelative(0.112f, -0.006f)
                arcToRelative(1f, 1f, 0f, false, false, 0.669f, -1.619f)
                lineToRelative(-3.501f, -4.375f)
                lineToRelative(3.5f, -4.375f)
                arcToRelative(1f, 1f, 0f, false, false, -0.78f, -1.625f)
                close()
            }
        }.build()
    }
}

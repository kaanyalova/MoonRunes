package com.kaanb.moonrunes.dictionary.ui.dictionary_entry.KanjiStrokeRenderer

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaanb.moonrunes.dictionary.dao.PathPiece
import com.kaanb.moonrunes.dictionary.util.kanjiStrokesToComposePaths
import com.kaanb.moonrunes.dictionary.util.scalePaths
import kotlinx.serialization.json.Json

const val TAG = "KanjiStrokeRenderer"
const val KANJIVG_VIEWBOX_SIZE = 40

// cut the lines based on the animation progresses
fun samplePaths(
    strokes: List<Path>,
    animations: List<Animatable<Float, AnimationVector1D>>
): List<Path> {
    val pathMeasure = PathMeasure()

    return strokes.mapIndexed { i, stroke ->

        pathMeasure.setPath(stroke, false)
        val currentAnimation = animations[i]

        val newPath = Path()
        pathMeasure.getSegment(
            0.0f,
            currentAnimation.value * pathMeasure.length,
            newPath
        )

        newPath
    }
}

@Composable
fun KanjiStrokeAnimatedRenderer(
    modifier: Modifier = Modifier,
    strokes: List<Path>,
    scale: Float = 1.0f,
    onAnimationEnd: () -> Unit = {}
) {
    val animations = remember {
        strokes.map { it ->
            Animatable(0f)
        }
    }

    val density = LocalDensity.current.density
    val scaleBasedOnDensity = scale * density * 1 / 3

    val scaledStrokes = remember(strokes, scaleBasedOnDensity) {
        scalePaths(strokes, scaleBasedOnDensity)
    }

    LaunchedEffect(
        key1 = Unit
    ) {
        (0..<strokes.count()).forEach { strokeIndex ->
            val currentAnimation = animations[strokeIndex]
            currentAnimation.animateTo(
                1.0f,
                animationSpec = tween(durationMillis = 500, easing = EaseIn)
            )

            Log.d(TAG, "start animation with idx $strokeIndex")
        }

        onAnimationEnd()
    }


    Canvas(modifier = modifier.size((KANJIVG_VIEWBOX_SIZE * scale).dp)) {
        samplePaths(scaledStrokes, animations).forEach { stroke ->
            drawPath(
                stroke,
                color = androidx.compose.ui.graphics.Color.Black,
                style = Stroke(
                    width = (2 * scaleBasedOnDensity),
                    cap = StrokeCap.Round
                )
            )
        }

    }


}

@Composable
fun KanjiStrokeRenderer(modifier: Modifier = Modifier, strokes: List<Path>, scale: Float = 1.0f) {
    val density = LocalDensity.current.density
    val scaleBasedOnDensity = scale * density * 1 / 3

    val scaledStrokes = remember(strokes, scaleBasedOnDensity) {
        scalePaths(strokes, scaleBasedOnDensity)
    }

    Canvas(modifier = modifier.size((KANJIVG_VIEWBOX_SIZE * scale).dp)) {
        scaledStrokes.forEach { stroke ->
            drawPath(
                stroke,
                color = androidx.compose.ui.graphics.Color.Black,
                style = Stroke(
                    width = (2 * scaleBasedOnDensity),
                    cap = StrokeCap.Round
                )
            )
        }

    }


}

@Preview
@Composable
private fun KanjiStrokeAnimatedRendererPreview() {
    val json = """
        [[{"type":"MoveTo","x":23.38,"y":21.68},{"type":"CubicTo","x1":26.37,"y1":22.33,"x2":29.36,"y2":22.26,"x":32.39,"y":22.01},{"type":"CubicTo","x1":45.5,"y1":20.97,"x2":62.51,"y2":19.28,"x":78.38,"y":18.86},{"type":"CubicTo","x1":81.19,"y1":18.79,"x2":84.11,"y2":18.79,"x":86.88,"y":19.39}],[{"type":"MoveTo","x":20.0,"y":43.14},{"type":"CubicTo","x1":21.19,"y1":44.1,"x2":21.48,"y2":44.84,"x":21.82,"y":46.24},{"type":"CubicTo","x1":23.13,"y1":51.63,"x2":24.26,"y2":59.2,"x":25.25,"y":65.75},{"type":"CubicTo","x1":25.55,"y1":67.75,"x2":25.82,"y2":69.31,"x":26.06,"y":70.75}],[{"type":"MoveTo","x":22.75,"y":45.54},{"type":"CubicTo","x1":45.75,"y1":42.75,"x2":64.29,"y2":41.08,"x":83.64,"y":39.7},{"type":"CubicTo","x1":87.12,"y1":39.45,"x2":90.38,"y2":40.15,"x":89.03,"y":45.24},{"type":"CubicTo","x1":87.77,"y1":50.01,"x2":85.62,"y2":56.25,"x":83.76,"y":62.0}],[{"type":"MoveTo","x":26.5,"y":67.0},{"type":"CubicTo","x1":46.14,"y1":65.34,"x2":57.84,"y2":64.33,"x":76.75,"y":63.2},{"type":"CubicTo","x1":79.38,"y1":62.95,"x2":82.01,"y2":62.92,"x":84.64,"y":63.11}],[{"type":"MoveTo","x":42.12,"y":24.0},{"type":"CubicTo","x1":43.21,"y1":25.56,"x2":43.69,"y2":27.23,"x":43.58,"y":29.01},{"type":"CubicTo","x1":43.58,"y1":36.23,"x2":43.61,"y2":79.44,"x":43.61,"y":88.0}],[{"type":"MoveTo","x":62.62,"y":22.75},{"type":"CubicTo","x1":63.53,"y1":24.38,"x2":63.94,"y2":25.84,"x":63.83,"y":27.63},{"type":"CubicTo","x1":63.83,"y1":35.92,"x2":63.86,"y2":79.8,"x":63.86,"y":86.51}],[{"type":"MoveTo","x":15.88,"y":89.23},{"type":"CubicTo","x1":19.5,"y1":89.88,"x2":23.13,"y2":89.94,"x":26.5,"y":89.72},{"type":"CubicTo","x1":45.11,"y1":88.51,"x2":69.0,"y2":87.38,"x":86.62,"y":87.16},{"type":"CubicTo","x1":89.83,"y1":87.12,"x2":92.96,"y2":87.39,"x":96.12,"y":88.07}]]
    """.trimIndent()

    val value = Json.decodeFromString<List<List<PathPiece>>>(json)

    val paths = kanjiStrokesToComposePaths(value)

    KanjiStrokeAnimatedRenderer(strokes = paths, scale = 4.0f)

    Log.d(TAG, value.toString())
}
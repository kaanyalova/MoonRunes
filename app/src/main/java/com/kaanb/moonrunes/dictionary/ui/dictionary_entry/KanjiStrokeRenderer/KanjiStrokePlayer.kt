package com.kaanb.moonrunes.dictionary.ui.dictionary_entry.KanjiStrokeRenderer

import android.icu.number.Scale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.dictionary.dao.PathPiece
import com.kaanb.moonrunes.dictionary.util.kanjiStrokesToComposePaths
import kotlinx.serialization.json.Json

@Composable
fun KanjiStrokePlayer(
    modifier: Modifier = Modifier,
    strokes: List<Path>,
    scale: Float
) {
    ElevatedCard(modifier = modifier) {

        var canReplay by remember(strokes) { mutableStateOf(true) }

        if (canReplay) {
            Box {
                KanjiStrokeRenderer(
                    modifier = Modifier.clickable { canReplay = false },
                    strokes = strokes,
                    scale = scale
                )


                Icon(
                    painter = painterResource(R.drawable.play_circle_24px),
                    contentDescription = null,
                    modifier = modifier
                        .size((12 * scale).dp)
                        .align(alignment = Alignment.BottomEnd)
                )
            }

        } else {
            KanjiStrokeAnimatedRenderer(
                strokes = strokes,
                scale = scale,
                onAnimationEnd = { canReplay = true }
            )



        }
    }

}

@Preview
@Composable
private fun KanjiStrokePlayerPreview() {
    val json = """
        [[{"type":"MoveTo","x":23.38,"y":21.68},{"type":"CubicTo","x1":26.37,"y1":22.33,"x2":29.36,"y2":22.26,"x":32.39,"y":22.01},{"type":"CubicTo","x1":45.5,"y1":20.97,"x2":62.51,"y2":19.28,"x":78.38,"y":18.86},{"type":"CubicTo","x1":81.19,"y1":18.79,"x2":84.11,"y2":18.79,"x":86.88,"y":19.39}],[{"type":"MoveTo","x":20.0,"y":43.14},{"type":"CubicTo","x1":21.19,"y1":44.1,"x2":21.48,"y2":44.84,"x":21.82,"y":46.24},{"type":"CubicTo","x1":23.13,"y1":51.63,"x2":24.26,"y2":59.2,"x":25.25,"y":65.75},{"type":"CubicTo","x1":25.55,"y1":67.75,"x2":25.82,"y2":69.31,"x":26.06,"y":70.75}],[{"type":"MoveTo","x":22.75,"y":45.54},{"type":"CubicTo","x1":45.75,"y1":42.75,"x2":64.29,"y2":41.08,"x":83.64,"y":39.7},{"type":"CubicTo","x1":87.12,"y1":39.45,"x2":90.38,"y2":40.15,"x":89.03,"y":45.24},{"type":"CubicTo","x1":87.77,"y1":50.01,"x2":85.62,"y2":56.25,"x":83.76,"y":62.0}],[{"type":"MoveTo","x":26.5,"y":67.0},{"type":"CubicTo","x1":46.14,"y1":65.34,"x2":57.84,"y2":64.33,"x":76.75,"y":63.2},{"type":"CubicTo","x1":79.38,"y1":62.95,"x2":82.01,"y2":62.92,"x":84.64,"y":63.11}],[{"type":"MoveTo","x":42.12,"y":24.0},{"type":"CubicTo","x1":43.21,"y1":25.56,"x2":43.69,"y2":27.23,"x":43.58,"y":29.01},{"type":"CubicTo","x1":43.58,"y1":36.23,"x2":43.61,"y2":79.44,"x":43.61,"y":88.0}],[{"type":"MoveTo","x":62.62,"y":22.75},{"type":"CubicTo","x1":63.53,"y1":24.38,"x2":63.94,"y2":25.84,"x":63.83,"y":27.63},{"type":"CubicTo","x1":63.83,"y1":35.92,"x2":63.86,"y2":79.8,"x":63.86,"y":86.51}],[{"type":"MoveTo","x":15.88,"y":89.23},{"type":"CubicTo","x1":19.5,"y1":89.88,"x2":23.13,"y2":89.94,"x":26.5,"y":89.72},{"type":"CubicTo","x1":45.11,"y1":88.51,"x2":69.0,"y2":87.38,"x":86.62,"y":87.16},{"type":"CubicTo","x1":89.83,"y1":87.12,"x2":92.96,"y2":87.39,"x":96.12,"y":88.07}]]
    """.trimIndent()

    val value = Json.decodeFromString<List<List<PathPiece>>>(json)

    val paths = kanjiStrokesToComposePaths(value)

    val json2 = """
        [[{"type":"MoveTo","x":40.96,"y":12.0},{"type":"CubicTo","x1":40.88,"y1":12.88,"x2":40.22,"y2":13.85,"x":39.68,"y":14.31},{"type":"CubicTo","x1":36.74,"y1":16.8,"x2":30.79,"y2":20.74,"x":19.56,"y":23.92}],[{"type":"MoveTo","x":13.97,"y":36.53},{"type":"CubicTo","x1":15.64,"y1":36.97,"x2":18.04,"y2":36.83,"x":19.75,"y":36.71},{"type":"CubicTo","x1":28.71,"y1":36.06,"x2":38.37,"y2":34.5,"x":47.25,"y":33.3},{"type":"CubicTo","x1":48.81,"y1":33.09,"x2":50.23,"y2":33.08,"x":51.78,"y":33.33}],[{"type":"MoveTo","x":34.32,"y":21.82},{"type":"CubicTo","x1":35.06,"y1":22.56,"x2":35.27,"y2":23.87,"x":35.27,"y":25.03},{"type":"CubicTo","x1":35.27,"y1":26.1,"x2":35.22,"y2":44.56,"x":35.13,"y":48.24}],[{"type":"MoveTo","x":22.36,"y":49.34},{"type":"CubicTo","x1":23.08,"y1":49.88,"x2":23.39,"y2":50.63,"x":23.62,"y":51.5},{"type":"CubicTo","x1":24.63,"y1":55.37,"x2":25.38,"y2":59.48,"x":26.08,"y":63.69},{"type":"CubicTo","x1":26.26,"y1":64.77,"x2":26.42,"y2":65.8,"x":26.56,"y":66.74}],[{"type":"MoveTo","x":24.59,"y":50.44},{"type":"CubicTo","x1":31.86,"y1":49.32,"x2":37.88,"y2":48.25,"x":43.37,"y":47.46},{"type":"CubicTo","x1":46.29,"y1":47.04,"x2":47.79,"y2":47.75,"x":46.82,"y":51.05},{"type":"CubicTo","x1":45.94,"y1":54.07,"x2":45.41,"y2":57.05,"x":44.15,"y":61.27}],[{"type":"MoveTo","x":27.39,"y":64.69},{"type":"CubicTo","x1":33.48,"y1":63.86,"x2":36.03,"y2":63.48,"x":42.77,"y":62.62},{"type":"CubicTo","x1":43.62,"y1":62.51,"x2":44.54,"y2":62.41,"x":45.53,"y":62.3}],[{"type":"MoveTo","x":73.17,"y":10.67},{"type":"CubicTo","x1":73.54,"y1":10.82,"x2":72.83,"y2":13.37,"x":72.6,"y":13.82},{"type":"CubicTo","x1":71.08,"y1":16.8,"x2":69.52,"y2":20.85,"x":65.93,"y":24.77}],[{"type":"MoveTo","x":60.1,"y":24.31},{"type":"CubicTo","x1":61.08,"y1":25.29,"x2":61.22,"y2":26.25,"x":61.22,"y":27.74},{"type":"CubicTo","x1":61.22,"y1":28.66,"x2":61.13,"y2":49.75,"x":61.16,"y":58.99},{"type":"CubicTo","x1":61.17,"y1":61.4,"x2":61.19,"y2":63.0,"x":61.22,"y":63.24}],[{"type":"MoveTo","x":61.98,"y":25.92},{"type":"CubicTo","x1":64.32,"y1":25.79,"x2":80.31,"y2":23.83,"x":85.01,"y":23.34},{"type":"CubicTo","x1":87.94,"y1":23.04,"x2":89.4,"y2":23.58,"x":89.43,"y":26.75},{"type":"CubicTo","x1":89.47,"y1":31.41,"x2":89.41,"y2":48.87,"x":89.41,"y":57.51},{"type":"CubicTo","x1":89.41,"y1":59.79,"x2":89.53,"y2":61.27,"x":89.5,"y":61.44}],[{"type":"MoveTo","x":62.19,"y":36.92},{"type":"CubicTo","x1":68.13,"y1":36.25,"x2":84.12,"y2":34.32,"x":88.25,"y":34.32}],[{"type":"MoveTo","x":62.4,"y":48.39},{"type":"CubicTo","x1":69.23,"y1":47.9,"x2":80.0,"y2":46.5,"x":88.2,"y":46.15}],[{"type":"MoveTo","x":62.39,"y":60.5},{"type":"CubicTo","x1":68.5,"y1":60.0,"x2":81.75,"y2":58.7,"x":88.38,"y":58.7}],[{"type":"MoveTo","x":22.63,"y":80.62},{"type":"CubicTo","x1":22.94,"y1":82.9,"x2":19.55,"y2":92.94,"x":17.08,"y":96.73}],[{"type":"MoveTo","x":31.25,"y":76.0},{"type":"CubicTo","x1":38.87,"y1":87.62,"x2":47.25,"y2":99.18,"x":80.33,"y":99.18},{"type":"CubicTo","x1":90.3,"y1":99.18,"x2":90.17,"y2":95.98,"x":83.41,"y":91.23}],[{"type":"MoveTo","x":53.27,"y":74.75},{"type":"CubicTo","x1":55.5,"y1":80.0,"x2":58.5,"y2":86.5,"x":60.89,"y":78.46}],[{"type":"MoveTo","x":77.7,"y":70.23},{"type":"CubicTo","x1":82.62,"y1":72.25,"x2":88.12,"y2":75.75,"x":91.98,"y":80.84}]]
    """.trimIndent()

    val value2 = Json.decodeFromString<List<List<PathPiece>>>(json2)
    val paths2 = kanjiStrokesToComposePaths(value2)

    Column() {
        KanjiStrokePlayer(modifier = Modifier.padding(8.dp), strokes = paths, scale = 4.0f)
        KanjiStrokePlayer(modifier = Modifier.padding(8.dp), strokes = paths2, scale = 4.0f)
    }
}

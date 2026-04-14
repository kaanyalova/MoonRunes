package com.kaanb.moonrunes.dictionary.util

import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.copy
import com.kaanb.moonrunes.dictionary.dao.PathPiece

fun kanjiStrokesToComposePaths(strokes: List<List<PathPiece>>): List<Path> {
    return strokes.map { stroke ->
        val path = Path()

        stroke.forEach { piece ->
            when (piece) {
                PathPiece.Close -> path.close()
                is PathPiece.CubicTo -> path.cubicTo(
                    piece.x1,
                    piece.y1,
                    piece.x2,
                    piece.y2,
                    piece.x,
                    piece.y
                )

                is PathPiece.LineTo -> path.lineTo(piece.x, piece.y)
                is PathPiece.MoveTo -> path.moveTo(piece.x, piece.y)
                is PathPiece.QuadTo -> path.quadraticTo(piece.x1, piece.y1, piece.x, piece.y)
            }
        }

        path
    }
}

fun transformPaths(strokes: List<Path>, matrix: Matrix): List<Path> {
    return strokes.map { stroke ->
        val copy = stroke.copy()
        copy.transform(matrix)
        copy
        //stroke.transform(matrix)
        //stroke
    }
}

fun scalePaths(strokes: List<Path>, scale: Float): List<Path> {
    val matrix = Matrix()
    matrix.scale(scale, scale)
    return transformPaths(strokes, matrix)
}




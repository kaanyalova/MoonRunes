package com.kaanb.moonrunes.dictionary.dao

import androidx.compose.ui.graphics.Path
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KanjiDicCharacter(
    val literal: String,
    val codepoints: KanjiCodepointGroup,
    val radicals: KanjiRadicalGroup,
    val misc: KanjiMisc,
    @SerialName("dictionary_references")
    val dictionaryReferences: KanjiDictionaryReferenceGroup = KanjiDictionaryReferenceGroup(),
    @SerialName("query_codes")
    val queryCodes: KanjiQueryCodeGroup = KanjiQueryCodeGroup(),
    @SerialName("reading_meaning")
    val readingMeaning: KanjiReadingMeaning = KanjiReadingMeaning()
)


@Serializable
data class KanjiDicCharacterWithStrokes(
    val kanjiDicEntry: KanjiDicCharacter,
    val strokes: List<List<PathPiece>>
)

@Serializable
data class KanjiDicCharacterWithPaths(
    val kanjiDicEntry: KanjiDicCharacter,
    val paths: List<Path>
)

@Serializable
data class KanjiRadicalGroup(
    val values: List<KanjiRadical>
)

@Serializable
data class KanjiCodepointGroup(
    val values: List<KanjiCodePoint> = emptyList()
)

@Serializable
data class KanjiCodePoint(
    @SerialName("codepoint_type")
    val codepointType: String,
    val body: String
)

@Serializable
data class KanjiRadical(
    @SerialName("radical_type")
    val radicalType: String,
    val body: String
)

@Serializable
data class KanjiMiscVariant(
    @SerialName("variant_type")
    val variantType: String,
    val body: String
)

@Serializable
data class KanjiDictionaryReferenceGroup(
    val entries: List<KanjiDictionaryReference> = emptyList()
)

@Serializable
data class KanjiDictionaryReference(
    @SerialName("dictionary_type")
    val dictionaryType: String,
    val volume: String = "",
    val page: String = "",
    val body: String
)

@Serializable
data class KanjiMisc(
    val grade: String = "",
    @SerialName("stroke_count")
    val strokeCount: List<String>,
    val variants: List<KanjiMiscVariant> = emptyList(),
    val frequency: String = "",
    val jlpt: String = ""
)

@Serializable
data class KanjiQueryCode(
    @SerialName("query_code_type")
    val queryCodeType: String,
    val body: String
)

@Serializable
data class KanjiQueryCodeGroup(
    val entries: List<KanjiQueryCode> = emptyList()
)

@Serializable
data class KanjiReadingMeaning(
    val group: KanjiReadingMeaningGroup = KanjiReadingMeaningGroup()
)

@Serializable
data class KanjiReadingMeaningGroup(
    val readings: List<KanjiReading> = emptyList(),
    val meanings: List<KanjiMeaning> = emptyList()
)

@Serializable
data class KanjiReading(
    @SerialName("reading_type")
    val readingType: String,
    val body: String
)

@Serializable
data class KanjiMeaning(
    val language: String = "",
    val body: String
)

@Serializable
data class KanjiDictEntries(
    val entries: List<KanjiDicCharacter>
)


@Serializable
sealed class PathPiece {
    @Serializable
    @SerialName("MoveTo")
    data class MoveTo(val x: Float, val y: Float) : PathPiece()

    @Serializable
    @SerialName("LineTo")
    data class LineTo(val x: Float, val y: Float) : PathPiece()

    @Serializable
    @SerialName("QuadTo")
    data class QuadTo(
        val x1: Float, val y1: Float,
        val x: Float, val y: Float
    ) : PathPiece()

    @Serializable
    @SerialName("CubicTo")
    data class CubicTo(
        val x1: Float, val y1: Float,
        val x2: Float, val y2: Float,
        val x: Float, val y: Float
    ) : PathPiece()

    @Serializable
    @SerialName("Close")
    data object Close : PathPiece()
}

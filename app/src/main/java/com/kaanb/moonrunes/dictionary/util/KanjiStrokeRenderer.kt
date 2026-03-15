package com.kaanb.moonrunes.dictionary.util

import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilderFactory

class KanjiStrokeRenderer(private val literal: String, private val xmlInput: String) {

    data class Coordinate(
        val x: Double, val y: Double
    )

    data class Stroke(
        val coords: List<Coordinate>,
    )

    private val document = createDocument(xmlInput)
    private val literalAsHex = createHexString(literal)


    fun createHexString(input: String): String {
        val char = input.first()
        val unicodeHex = char.code.toHexString()
        return unicodeHex
    }


    fun createDocument(input: String): Document {
        val documentBuilderFactory = DocumentBuilderFactory.newInstance()
        val documentBuilder = documentBuilderFactory.newDocumentBuilder()
        val document = documentBuilder.parse(input)
        return document
    }

    fun extractStrokePaths(): List<String> {
        val strokes = mutableListOf<String>()

        val i = 1

        while (true) {
            val id = "kvg:$literalAsHex-s$i"
            val element = document.getElementById(id)

            if (element == null) {
                break
            }

            strokes.add(element.getAttribute("d"))
        }

        return strokes
    }

    fun parseStrokePath(input: String): List<Stroke> {
        val split = input.split("c")
        var startPoint = split[0]
        val points = split[1]

        startPoint = startPoint.removePrefix("M")
        startPoint = startPoint.trim()
        val startPointSplit = startPoint.split(",")
        val startX = startPointSplit[0].toDouble()
        val startY = startPointSplit[1].toDouble()
        val start = Coordinate(startX, startY)

        val pointsSplit = points.split(",")
        



    }

}
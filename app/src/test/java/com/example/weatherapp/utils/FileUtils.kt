package com.example.weatherapp.utils

import java.io.BufferedReader
import java.io.InputStreamReader

object FileUtils {
    fun readFromResourceFile(resourcePath: String): String {
        val stringBuilder = StringBuilder()
        val readFileLineByLine: (BufferedReader) -> Unit = { reader ->
            var line = reader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = reader.readLine()
            }
        }
        val readFromFile: (InputStreamReader) -> Unit = { inputStreamReader ->
            val reader = BufferedReader(inputStreamReader)
            doSafely { readFileLineByLine(reader) }
            doSafely { reader.close() }
        }

        javaClass.getResourceAsStream(resourcePath)?.also {
            readFromFile(it.reader())
        }
        return stringBuilder.toString()
    }
}

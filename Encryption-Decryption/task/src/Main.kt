package encryptdecrypt

import java.io.File
import kotlin.system.exitProcess

const val LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz"
const val UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

fun main(args: Array<String>) {

    val mode = getArgument("-mode", args, "enc")
    val alg = getArgument("-alg", args, "shift")
    val key = getArgument("-key", args, "0").toInt()

    val data = getData(args)
    val processMethod = selectProcessMethod(alg, mode)
    val outputData = processMethod(data, key)

    val outputFile = getArgument("-out", args, "")
    writeOutput(outputFile, outputData)
}

private fun getData(args: Array<String>): String {
    var data = getArgument("-data", args, "")
    if (data.isEmpty()) {
        val inputFile = getArgument("-in", args, "")
        val dataFile = File(inputFile)
        if (dataFile.exists()) {
            data = dataFile.readText()
        } else {
            println("Error: Input file doesn't exists")
            exitProcess(0)
        }
    }
    return data
}

private fun selectProcessMethod(
    alg: String,
    mode: String
) = when (alg) {
    "shift" -> when (mode) {
        "enc" -> ::encryptShift
        "dec" -> ::decryptShift
        else -> identity
    }
    "unicode" -> when (mode) {
        "enc" -> ::encryptUnicode
        "dec" -> ::decryptUnicode
        else -> identity
    }
    else -> identity
}

private fun writeOutput(fileName: String, outputData: String) {
    if (fileName.isEmpty()) {
        println(outputData)
    } else {
        val outputFile = File(fileName)
        outputFile.writeText(outputData)
    }
}

private fun getArgument(name: String, args: Array<String>, defaultValue: String): String {
    for (i in 0..args.lastIndex step 2) if (name == args[i] && i + 1 < args.size ) {
        return args[i + 1]
    }
    return defaultValue
}

val identity = {x: String, k:Int -> x}

private fun encryptShift(text: String, key: Int): String {
    var cypher = ""
    for (textChar in text) {
        var index = LOWERCASE_CHARS.indexOf(textChar)
        if( -1 != index){
            cypher += LOWERCASE_CHARS.get((index + key) % LOWERCASE_CHARS.length)
        } else {
            index = UPPERCASE_CHARS.indexOf(textChar)
            if( -1 != index) {
                cypher += UPPERCASE_CHARS.get((index + key) % UPPERCASE_CHARS.length)
            } else {
                cypher += textChar
            }
        }
    }
    return cypher
}

private fun decryptShift(cypher: String, key: Int): String {
    var text = ""
    for (textChar in cypher) {
        var index = LOWERCASE_CHARS.indexOf(textChar)
        if( -1 != index){
            text += LOWERCASE_CHARS.get((UPPERCASE_CHARS.length + index - key) % LOWERCASE_CHARS.length)
        } else {
            index = UPPERCASE_CHARS.indexOf(textChar)
            if( -1 != index) {
                text += UPPERCASE_CHARS.get((UPPERCASE_CHARS.length + index - key) % UPPERCASE_CHARS.length)
            } else {
                text += textChar
            }
        }
    }
    return text
}
private fun encryptUnicode(text: String, key: Int): String {
    var cypher = ""
    for (textChar in text) {
        val i = textChar.code
        val index = (i + key) % Char.MAX_VALUE.code
        cypher += index.toChar()
    }
    return cypher
}

private fun decryptUnicode(cypher: String, key: Int): String {
    var text = ""
    for (textChar in cypher) {
        val i = textChar.code
        val index = (Char.MAX_VALUE.code + i - key) % Char.MAX_VALUE.code
        text += index.toChar()
    }
    return text
}
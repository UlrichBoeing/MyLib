@file:Suppress("NOTHING_TO_INLINE")

package de.ulrich_boeing.mylib.extensions

import java.awt.Color
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


// colors are defined as long-values converted to Int

const val COLOR_WHITE = 0xFFFFFFFF.toInt()
const val COLOR_BLACK = 0xFF000000.toInt()
const val COLOR_GRAY = 0xFF7F7F7F.toInt()
const val COLOR_RED = 0xFFFF0000.toInt()
const val COLOR_GREEN = 0xFF00FF00.toInt()
const val COLOR_BLUE = 0xFF0000FF.toInt()
const val COLOR_YELLOW = 0xFFFFFF00.toInt()
const val COLOR_AQUA  = 0xFF00FFFF.toInt()
const val COLOR_FUCHSIA = 0xFFFF00FF.toInt()
const val COLOR_SHIFFMAN = 0xFF333333.toInt()

inline val Int.red: Int
    get() = (this shr 16) and 0xFF

inline val Int.green: Int
    get() = (this shr 8) and 0xFF

inline val Int.blue: Int
    get() = this and 0xFF

inline val Int.alpha: Int
    get() = (this shr 24) and 0xFF

inline val Int.HSB: FloatArray
    get() = Color.RGBtoHSB(this.red, this.green, this.blue, null)

inline fun Int.setRed(red: Int): Int =
    (this and 0xFF00FFFF.toInt()) or (red shl 16)

inline fun Int.setGreen(green: Int): Int =
    (this and 0xFFFF00FF.toInt()) or (green shl 8)

inline fun Int.setBlue(blue: Int): Int =
    (this and 0xFFFFFF00.toInt()) or blue

inline fun Int.setAlpha(alpha: Int, offset: Int = 0): Int =
//    Int.fromRGBA(this.red, this.green, this.blue, alpha + 1)
    (this and 0x00FFFFFF) or ((alpha + offset) shl 24)

inline fun Int.setRedNorm(normRed: Float) =
    this.setRed((normRed * 255).toInt())

inline fun Int.setGreenNorm(normGreen: Float) =
    this.setGreen((normGreen * 255).toInt())

inline fun Int.setBlueNorm(normBlue: Float) =
    this.setBlue((normBlue * 255).toInt())

inline fun Int.setAlphaNorm(normAlpha: Float, offset: Int = 0) =
    this.setAlpha((normAlpha * (255 - offset)).toInt(), offset)

/**
 * brightness liegt zwischen 0 und 255
 */
fun Int.getBrightness(): Int = (0.2126f * red + 0.7152 * green + 0.0722 * blue).toInt()

inline fun Int.Companion.fromRGBA(red: Float, green: Float, blue: Float, alpha: Float) =
    Int.fromRGBA(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())

inline fun Int.Companion.fromRGBA(red: Int, green: Int, blue: Int, alpha: Int): Int =
    (alpha shl 24) or (red shl 16) or (green shl 8) or blue

inline fun Int.Companion.fromRGB(red: Int, green: Int, blue: Int): Int =
    Int.fromRGBA(red, green, blue, 255)

inline fun Int.Companion.fromHSB(hue: Float, saturation: Float, brightness: Float) =
    Color.HSBtoRGB(hue, saturation, brightness)

//fun Int.Companion.randomColor(from: Int = COLOR_BLACK, until: Int = COLOR_WHITE): Int {
//    val red = Random.intInRange(from.red, until.red)
//    val green = Random.intInRange(from.green, until.green)
//    val blue = Random.intInRange(from.blue, until.blue)
//    val alpha = Random.intInRange(from.alpha, until.alpha)
//    return Int.fromRGBA(red, green, blue, alpha)
//}
//
//fun Int.mixColor(other: Int, amount: Float): Int {
//    val newRed = lerp(this.red, other.red, amount)
//    val newGreen = lerp(this.green, other.green, amount)
//    val newBlue = lerp(this.blue, other.blue, amount)
//    val newAlpha = lerp(this.alpha, other.alpha, amount)
//    return Int.fromRGBA(newRed, newGreen, newBlue, newAlpha)
//}

fun Int.getRGBDiff(other: Int): IntArray {
    val redDiff = (this.red - other.red).absoluteValue
    val greenDiff = (this.green - other.green).absoluteValue
    val blueDiff = (this.blue - other.blue).absoluteValue
    return intArrayOf(redDiff, greenDiff, blueDiff)
}

fun Int.getHSBDiff(other: Int): FloatArray {
    val hsb1 = this.HSB
    val hsb2 = other.HSB
    return FloatArray(3) {i -> (hsb1[i] - hsb2[i]).absoluteValue}
}

fun Int.printAsColor() {
    println("red=" + red + " green=" + green + " blue=" + blue + " alpha=" + alpha)
}

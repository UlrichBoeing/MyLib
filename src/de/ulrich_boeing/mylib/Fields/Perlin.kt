package de.ulrich_boeing.mylib.Fields

import de.ulrich_boeing.mylib.app
import de.ulrich_boeing.mylib.geometry.Clip
import de.ulrich_boeing.mylib.geometry.Rect
import processing.core.PImage
import kotlin.math.sqrt

//class FloatImage(val clip: Clip, val pixels: FloatArray) {
//    fun toGrayImage(): PImage {
//        val image = PImage(clip.width, clip.height)
//        image.loadPixels()
//        for (i in pixels.indices) {
//            val value = (pixels[i] * 255).toInt()
//            image.pixels[i] = app.color(value, value, value)
//        }
//        image.updatePixels()
//        return image
//    }
//}

/**
 * distance is increment for 1000px
 */
class NoiseField(val image: PImage) {
    val imageSize = image.width * image.height
    val imageFactor = sqrt(imageSize / 1_000_000f)

    fun rate(clip: Clip, xDistance: Float = 10f, yDistance: Float = xDistance): FloatArray {
        val xInc =  (xDistance / 1000f) / imageFactor
        val yInc = (yDistance / 1000f)  / imageFactor

        val array = FloatArray(clip.width * clip.height)
        for (y in 0 until clip.height) {
            for (x in 0 until clip.width) {
                val index = x + y * clip.width
                val xNoise = (clip.x + x) * xInc
                val yNoise = (clip.y + y) * yInc
                array[index] = app.noise(xNoise, yNoise)
            }
        }
        return array
    }

}

fun Clip.noise(rect: Rect): FloatArray {
    val array = FloatArray(width * height)
    val xInc = rect.width / width
    val yInc = rect.height / height

    for (y in 0 until height) {
        for (x in 0 until width) {
            val index = x + y * width
            val xNoise = rect.x + x * xInc
            val yNoise = rect.y + y * yInc
            array[index] = app.noise(xNoise, yNoise)
        }
    }
    return array
}




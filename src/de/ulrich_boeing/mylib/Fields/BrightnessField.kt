package de.ulrich_boeing.mylib.Fields

import de.ulrich_boeing.mylib.extensions.getBrightness
import de.ulrich_boeing.mylib.extensions.square
import de.ulrich_boeing.mylib.geometry.Clip
import de.ulrich_boeing.mylib.geometry.Vec
import processing.core.PImage
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class BrightnessField(private val image: PImage) {
    val pixels = FloatArray(image.width * image.height) { -1f }

    fun rate(clip: Clip): FloatArray {
        val array = FloatArray(clip.width * clip.height)
        for (y in 0 until clip.height) {
            for (x in 0 until clip.width) {
                val index = x + y * clip.width
                val sourceIndex = (x + clip.x) + (y + clip.y) * image.width
                if (pixels[sourceIndex] == -1f) {
                    pixels[sourceIndex] = image.pixels[sourceIndex].getBrightness() / 255f

                }
                array[index] = pixels[sourceIndex]
            }
        }
        println(array[0])
        return array
    }
}
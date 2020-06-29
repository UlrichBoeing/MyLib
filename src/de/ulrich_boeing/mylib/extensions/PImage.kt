package de.ulrich_boeing.mylib.extensions

import de.ulrich_boeing.mylib.app
import de.ulrich_boeing.mylib.geometry.Clip
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PImage
import kotlin.math.pow
import kotlin.random.Random

/**
 * blend-function of processing is used because
 * image-function of processing shows unexpected behavior for alpha = 0
 */
fun PImage.blend(x: Int, y: Int, mode: Int = PConstants.BLEND) {
    app.blend(this, 0, 0, width, height, x, y, width, height, mode)
}

fun createImage(clip: Clip, color: Int): PImage {
    val image = app.createImage(clip.width, clip.height, PApplet.RGB)
    with(image) {
        loadPixels()
        for (i in pixels.indices) {
            pixels[i] = color
        }
        updatePixels()
    }
    return image
}


fun PImage.setAlphaNorm(alphaArray: FloatArray): PImage {
    loadPixels()
    for (i in pixels.indices) {
        pixels[i] = pixels[i].setAlphaNorm(alphaArray[i], 0)
    }
    updatePixels()
    return this
}
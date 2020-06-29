package de.ulrich_boeing.mylib

import de.ulrich_boeing.mylib.Fields.BrightnessField
import de.ulrich_boeing.mylib.Fields.NoiseField

class SourceImage(val path: String) {
    val image by lazy { app.loadImage(path) }
    val brightness by lazy { BrightnessField(image) }
    val noise by lazy { NoiseField(image) }
}


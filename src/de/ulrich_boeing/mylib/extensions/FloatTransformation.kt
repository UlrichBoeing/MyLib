package de.ulrich_boeing.mylib.extensions

import kotlin.math.pow

fun FloatArray.invert() = mapInPlace { 1 - it }
fun FloatArray.repeat(n: Int) = mapInPlace { (it * n) % 1f }
fun FloatArray.split(split: Float) = mapInPlace { if (it > split) 1f else 0f }
fun FloatArray.exp(x: Float) = mapInPlace { it.pow(x) }
fun FloatArray.triangle(split: Float = 0.5f) = mapInPlace {
    if (it < split)
        it / split
    else
        (1 - it) / (1 - split)
}

private inline fun FloatArray.mapInPlace(transform: (Float) -> Float): FloatArray {
    for (i in indices) {
        this[i] = transform(this[i])
    }
    return this
}

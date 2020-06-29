package de.ulrich_boeing.mylib.Fields

import kotlin.math.pow

class ProductField(val size: Int) {
    val product = FloatArray(size) { 1f }
    private var weightSum = 0f

    /**
     * weight is a normalized value 0-1
     */
    fun add(arr: FloatArray, weight: Float = 1f): ProductField {
        for (i in product.indices) {
            val weightValue = arr[i] * weight + (1 - weight)
            product[i] *= weightValue
        }
        weightSum += weight
        return this
    }

    fun get(restore: Float = 1f) = FloatArray(product.size) {
        if (weightSum == 0f) 1f else product[it] * (1 - restore) + product[it].pow(1f / weightSum) * restore
    }
}



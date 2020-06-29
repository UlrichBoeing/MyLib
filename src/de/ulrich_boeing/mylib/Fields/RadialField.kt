package de.ulrich_boeing.mylib.Fields

import de.ulrich_boeing.mylib.extensions.square
import de.ulrich_boeing.mylib.geometry.Clip
import de.ulrich_boeing.mylib.geometry.Vec
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class RadialField(){
    companion object {
        private fun radiusAtAngle(clip: Clip, angle: Float): Float {
            val xRadius = clip.width / 2f
            val yRadius = clip.height / 2f
            val add1 = xRadius.square() * sin(angle).square()
            val add2 = yRadius.square() * cos(angle).square()
            return (xRadius * yRadius) / (sqrt(add1 + add2))
        }

        fun rate(clip: Clip): FloatArray {
            val array = FloatArray(clip.width * clip.height)
            val center = clip.center - Vec(clip.x, clip.y)

            for (y in 0 until clip.height) {
                for (x in 0 until clip.width) {
                    val centerToVec = center - Vec(x, y)
                    val len = centerToVec.length
                    val radius = radiusAtAngle(clip, centerToVec.angle)

                    var normRadius = (len / radius)
                    val index = x + y * clip.width
                    array[index] =  (1 - normRadius).coerceAtLeast(0f)
                }
            }
            return array
        }
    }
}
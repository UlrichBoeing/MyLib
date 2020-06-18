package de.ulrich_boeing.mylib.geometry

import processing.core.PGraphics
import java.lang.RuntimeException
import kotlin.math.*


/**
 * x, y are located on the rectangle (and thus left, top).
 * Right, bottom are *outside* the rectangle to ensure same operations like in rect.
 */
class Clip(var x: Int, var y: Int, width: Int, height: Int) {
    constructor () : this(Int.MIN_VALUE, Int.MIN_VALUE, 0, 0)
    constructor(width: Int, height: Int) : this(0, 0, width, height)
    constructor(x: Float, y: Float, width: Float, height: Float) : this(
        x.roundToInt(),
        y.roundToInt(),
        width.roundToInt(),
        height.roundToInt()
    )

    companion object {
        fun fromVertices(left: Int, top: Int, right: Int, bottom: Int) = Clip(left, top, right - left, bottom - top)

        fun aroundRect(rect: Rect, margin: Int = 0): Clip {
            val x = floor(rect.x).toInt() - margin
            val y = floor(rect.y).toInt() - margin
            val right = ceil(rect.right).toInt() + margin
            val bottom = ceil(rect.bottom).toInt() + margin
            return Clip(x, y, right - x, bottom - y)
        }

        fun fromRect(rect: Rect): Clip =
            Clip(rect.x.roundToInt(), rect.y.roundToInt(), rect.width.roundToInt(), rect.height.roundToInt())
    }

    var width = width
        set(value) {
            field = if (value > 0) value else 0
        }

    var height = height
        set(value) {
            field = if (value > 0) value else 0
        }

    var left: Int
        get() = x
        set(value) {
            val limitedValue = if (value < right) value else right
            width -= limitedValue - x
            x = limitedValue
        }

    var top: Int
        get() = y
        set(value) {
            val limitedValue = if (value < bottom) value else bottom
            height -= limitedValue - y
            y = limitedValue
        }

    var right: Int
        get() = x + width
        set(value) {
            val limitedValue = if (value > left) value else left
            width = limitedValue - x
        }

    var bottom: Int
        get() = y + height
        set(value) {
            val limitedValue = if (value > top) value else top
            height = limitedValue - y
        }

    val center: Vec
        get() = Vec(x + width / 2f, y + height / 2f)

    val size: Int
        get() = width * height

    val aspectRatio = width.toFloat() / height.toFloat()

    // ok
    val vertices: Array<IntVec>
        get() = arrayOf(IntVec(x, y), IntVec(right, y), IntVec(x, bottom), IntVec(right, bottom))

    // ok
    fun contains(p: IntVec): Boolean {
        return !(p.x < x || p.x > right || p.y < y || p.y > bottom)
    }

    fun contains(clip: Clip): Boolean {
        return (containsVertices(clip) == 4)
    }

    /**
     * number of vertices of clipping inside this
     */
    fun containsVertices(clip: Clip): Int {
        var numberVertices = 0
        for (p in clip.vertices)
            if (this.contains(p))
                numberVertices++

        return numberVertices
    }

    fun centerInto(other: Clip): Clip {
        val newX = other.x + (other.width - width) / 2
        val newY = other.y + (other.height - height) / 2
        return Clip(newX, newY, width, height)
    }

    // this clipping will be changed in size according to other
    fun shrinkInto(other: Clip): Clip {
        if (aspectRatio > other.aspectRatio) {
            val newHeight = (height.toFloat() * other.width/ width).roundToInt()
            return Clip(other.x, other.y, other.width, newHeight)
        } else {
            val newWidth = (width.toFloat() * other.height / height).roundToInt()
            return Clip(other.x, other.y, newWidth, other.height)
        }
    }

    fun exactUnion(other: Clip): Array<Clip> {
        if (!isOverlapping(other))
            return arrayOf(this, other)

        if (contains(other)) {
//            println("other inside")
            return arrayOf(this)
        }

        if (other.contains(this)) {
//            println("this inside")
            return arrayOf(other)
        }

        val sumOther = this.containsVertices(other)
        val sumThis = other.containsVertices(this)

        if (sumThis == 0 && sumOther == 0) {
//            println("splitMiddle(other)")
            return arrayOf(this) + other.splitMiddle(intersect(other))
        }

        if (sumThis == 2) {
//            println("this in zwei Teile aufteilen.")
            return arrayOf(other) + this.splitHalf(other.intersect(this))
        }

        if (sumOther == 2) {
//            println("other in zwei Teile aufteilen")
            return arrayOf(this) + other.splitHalf(intersect(other))
        }

        if (sumOther == 1 && sumThis == 1) {
            println("other in drei Teile aufteilen")
            return arrayOf(this) + other.splitCorner(intersect(other))
        }

        throw RuntimeException("Unexpected arrangement of rectangles.")
        return arrayOf(this, other)
    }

    // splits this in the middle defined by other
    fun splitMiddle(other: Clip): Array<Clip> {
        if (left < other.left) {
            return arrayOf(
                fromVertices(left, top, other.left, bottom),
                fromVertices(other.right, top, right, bottom)
            )
        } else
            return arrayOf(
                fromVertices(left, top, right, other.top),
                fromVertices(left, other.bottom, right, bottom)
            )
    }

    fun splitHalf(other: Clip): Array<Clip> {
        if (left == other.left && right != other.right)
            return arrayOf(Clip.fromVertices(other.right, top, right, bottom))
        else if (left != other.left && right == other.right)
            return arrayOf(Clip.fromVertices(left, top, other.left, bottom))
        else if (top == other.top && bottom != other.bottom)
            return arrayOf(Clip.fromVertices(left, other.bottom, right, bottom))
        else
            return arrayOf(Clip.fromVertices(left, top, right, other.top))

    }

    fun splitCorner(other: Clip): Array<Clip> {
        if (left == other.left && top == other.top)
            return arrayOf(
                fromVertices(left, other.bottom, other.right, bottom),
                fromVertices(other.right, top, right, bottom)
            )
        else if (left == other.left && bottom == other.bottom)
            return arrayOf(
                fromVertices(left, top, other.right, other.top),
                fromVertices(other.right, top, right, bottom)
            )
        else if (right == other.right && top == other.top)
            return arrayOf(
                fromVertices(left, top, other.left, bottom),
                fromVertices(other.left, other.bottom, right, bottom)
            )
        else
            return arrayOf(
                fromVertices(left, top, other.left, bottom),
                fromVertices(other.left, top, right, other.top)

            )

        return arrayOf(other)
    }

    fun isOverlapping(other: Clip): Boolean {
        return !(x >= other.right || right <= other.x || y >= other.bottom || bottom <= other.y)
    }

    fun isOverlapping(others: Array<Clip>): Boolean {
        for (other in others) {
            if (isOverlapping(other))
                return true
        }
        return false
    }

    fun intersect(other: Clip): Clip {
        val intersect = Clip()
        if (!isOverlapping(other)) return intersect

        intersect.x = max(x, other.x)
        intersect.y = max(y, other.y)
        intersect.right = min(right, other.right)
        intersect.bottom = min(bottom, other.bottom)
        return intersect
    }

    fun union(other: Clip): Clip {
        val union = Clip()
        union.x = min(x, other.x)
        union.y = min(y, other.y)
        union.right = max(right, other.right)
        union.bottom = max(bottom, other.bottom)
        return union
    }

    override fun equals(other: Any?): Boolean {
        if (this == other) return true
        if (other?.javaClass != javaClass) return false

        other as Clip
        return !(x != other.x || y != other.y || width != other.width || height != other.height)
    }

    override fun toString(): String {
        return "Clipping(x=$x, y=$y, width=$width, height=$height)"
    }

    fun draw(g: PGraphics) {
        g.rect(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
    }

}



class PointClipping(var x1: Int, var y1: Int, var x2: Int, var y2: Int) {
    val clip: Clip
        get() = Clip(x, y, width, height)
    val x: Int
        get() = min(x1, x2)
    val y: Int
        get() = min(y1, y2)
    val width: Int
        get() = (x2 - x1).absoluteValue
    val height: Int
        get() = (y2 - y1).absoluteValue
    val left: Int
        get() = min(x1, x2)
    val right: Int
        get() = max(x1, x2)
    val top: Int
        get() = min(y1, y2)
    val bottom: Int
        get() = max(y1, y2)
}


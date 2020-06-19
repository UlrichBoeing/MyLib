package de.ulrich_boeing.mylib.geometry

import processing.core.PGraphics
import java.lang.RuntimeException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

class Rect(var x: Float, var y: Float, width: Float, height: Float) : Figure {
    constructor(x: Int, y: Int, width: Int, height: Int) :
            this(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())

    constructor(width: Int, height: Int) :
            this(0f, 0f, width.toFloat(), height.toFloat())

    constructor(other: Rect) :
            this(other.x, other.y, other.width, other.height)

    companion object {
        fun fromVertices(x1: Float, y1: Float, x2: Float, y2: Float) = Rect(x1, y1, x2 - x1, y2 - y1)
//        fun fromCircle(circle: Circle) = fromCenter(circle.center, circle.radius * 2, circle.radius * 2)

        fun fromCenter(center: Vec, width: Float, height: Float) =
            Rect(center.x - width / 2, center.y - height / 2, width, height)
    }

    var throwException = false

    var width: Float = 0f
        set(value) {
            field = value
            if (value < 0f) {
                if (throwException) throw RuntimeException("Width in Rect negative: $value")
                field = 0f
            }
        }

    var height: Float = 0f
        set(value) {
            field = value
            if (value < 0f) {
                if (throwException) throw RuntimeException("Height in Rect negative: $value")
                field = 0f
            }
        }

    init {
        this.width = width
        this.height = height
    }

    var left: Float
        get() = x
        set(value) {
            width += x - value
            x = value
        }
    var top: Float
        get() = y
        set(value) {
            height += y - value
            y = value
        }
    var right: Float
        get() = x + width
        set(value) {
            width = value - x
        }
    var bottom: Float
        get() = y + height
        set(value) {
            height = value - y
        }

    val center = Vec(x + width / 2, y + height / 2)
    val aspectRatio = width / height

    override fun contains(vec: Vec): Boolean =
        !(vec.x < left || vec.x > right || vec.y < top || vec.y > bottom)

    override fun pureContains(vec: Vec): Boolean =
        !(vec.x <= left || vec.x >= right || vec.y <= top || vec.y >= bottom)

    override fun contains(other: Rect): Boolean =
        !(left > other.left || right < other.right || top > other.top || bottom < other.bottom)

    override fun pureContains(other: Rect): Boolean =
        !(left >= other.left || right <= other.right || top >= other.top || bottom <= other.bottom)

    override infix fun intersects(other: Rect): Boolean =
        !(left > other.right || right < other.left || top > other.bottom || bottom < other.top)

    override infix fun pureIntersects(other: Rect): Boolean =
        !(left >= other.right || right <= other.left || top >= other.bottom || bottom <= other.top)

    fun intersection(other: Rect) = fromVertices(
        max(left, other.left),
        max(top, other.top),
        min(right, other.right),
        min(bottom, other.bottom)
    )

    fun getEnclosingRectangle(other: Rect) = fromVertices(
        min(left, other.left),
        min(top, other.top),
        max(right, other.right),
        max(bottom, other.bottom)
    )

    /**
     * Returns a new Rectangle inside the old thus creating a margin
     */
    fun withMargin(margin: Float) = Rect(x + margin, y + margin, width - 2 * margin, height - 2 * margin)

    fun listOfRandomVec(num: Int) = List(num) { randomVec() }
    fun randomVec() = Vec(x + Random.nextFloat() * width, y + Random.nextFloat() * height)
}

fun Rect.draw(g: PGraphics) {
    g.rect(this.x, this.y, this.width, this.height)
}

fun List<Rect>.draw(g: PGraphics) = forEach { it.draw(g) }

/**
 * standard rectangle has a size of 1 Million Pixel.
 */
fun Rect.getStandardRect(): Rect {
    val numPixels = 1000000
    val newWidth = sqrt(numPixels * aspectRatio)
    val newHeight = height * (newWidth / width)
    return Rect(x, y, newWidth, newHeight)
}


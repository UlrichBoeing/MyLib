package de.ulrich_boeing.mylib.geometry

import processing.core.PGraphics
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
        fun fromCircle(center: Vec, radius: Float): Rect {
            val p = center - Vec(radius, radius)
            return Rect(p.x, p.y, radius * 2, radius * 2)
        }

        fun fromCenter(center: Vec, width: Float, height: Float): Rect =
            Rect(center.x - width / 2, center.y - height / 2, width, height)
    }

    var width: Float = 0f
        set(value) {
            if (value < 0f) {
                println("Width $value in Rect can not be negative.")
                field = 0f
            } else
                field = value
        }

    var height: Float = 0f
        set(value) {
            if (value < 0f) {
                println("Height $value in Rect can not be negative.")
                field = 0f
            } else
                field = value
        }
    val center: Vec
        get() = Vec(x + width / 2, y + height / 2)

    val aspectRatio: Float = width / height

    /**
     * standard rectangle has a size of 1 Million Pixel.
     *
     * @return the rectangle
     */
    val standardRect: Rect
        get() {
            val numPixels = 1000000
            val newWidth = sqrt(numPixels * aspectRatio)
            val newHeight = height * (newWidth / width)
            return Rect(x, y, newWidth, newHeight)
        }

    init {
        this.width = width
        this.height = height
    }

    var left: Float
        get() = x
        set(value) {
            println("value $value right $right")
            if (value < right) {
                width += value - x
                x = value
            } else
                println("hier liegt der Fehler")
        }
    var top: Float
        get() = y
        set(value) {
            height += value - y
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


    fun splitTo4(vec: Vec): List<Rect> {
        val rect1 = Rect(x, y, vec.x - x, vec.y - y)
        val rect2 = Rect(vec.x, y, right - vec.x, vec.y - y)
        val rect3 = Rect(vec.x, vec.y, right - vec.x, bottom - vec.y)
        val rect4 = Rect(x, vec.y, vec.x - x, bottom - vec.y)
        return listOf(rect1, rect2, rect3, rect4)
    }

    fun getEnclosingRectangle(other: Rect): Rect {
        val new = Rect(0, 0, 0, 0)
        new.left = if (left < other.left) left else other.left
        new.top = if (top < other.top) top else other.top
        new.right = if (right > other.right) right else other.right
        new.bottom = if (bottom > other.bottom) bottom else other.bottom
        return new
    }

    fun limit(other: Rect): Rect {
        val new = Rect(0, 0, 0, 0)
        if (!pureIntersects(other))
            return new

        new.left = if (other.left < left) left else other.left
        new.top = if (other.top < top) top else other.top
        new.right = if (other.right > right) right else other.right
        new.bottom = if (other.bottom > bottom) bottom else other.bottom

        return new
    }

    /**
     * Returns a new Rectangle inside the old thus creating a margin
     */
    fun withMargin(margin: Float)= Rect(x + margin, y + margin, width - 2 * margin, height - 2 * margin)

    fun listOfRandomVec(num: Int) = List(num) {randomVec() }
    fun randomVec() = Vec(x + Random.nextFloat() * width, y + Random.nextFloat() * height)
}

fun Rect.draw(g: PGraphics) {
    g.rect(this.x, this.y, this.width, this.height)
}

fun List<Rect>.draw(g: PGraphics) = forEach { it.draw(g) }
package de.ulrich_boeing.mylib.geometry

import processing.core.PGraphics
import kotlin.math.*
import kotlin.random.Random

data class IntVec(var x: Int, var y: Int)

// no cross product because it's only defined for 3 dimensional vectors

open class Vec(var x: Float, var y: Float) {
    constructor(x: Int, y: Int) : this(x.toFloat(), y.toFloat())
    constructor(vec: Vec) : this(vec.x, vec.y)

    companion object {
        const val PI = Math.PI.toFloat()
        const val TAU = 2 * PI
        fun fromAngle(angle: Float): Vec = Vec(cos(angle), sin(angle))
        fun fromRandomAngle(): Vec = fromAngle(Random.nextFloat() * TAU)
        fun fromPolar(angle: Float, length: Float) = fromAngle(angle) * length

        fun scalarProjection(vec: Vec, direction: Vec): Float {
            val normDirection = Vec(direction).normalize()
            return vec dot normDirection
        }
    }

    fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    operator fun component1(): Float = x
    operator fun component2(): Float = y

    operator fun plus(vec: Vec) = Vec(x + vec.x, y + vec.y)
    operator fun plusAssign(vec: Vec) = set(x + vec.x, y + vec.y)

    operator fun minus(vec: Vec) = Vec(x - vec.x, y - vec.y)
    operator fun minusAssign(vec: Vec) = set(x - vec.x, y - vec.y)

    operator fun times(n: Float) = Vec(n * x, n * y)
    operator fun times(n: Int) = Vec(n * x, n * y)
    operator fun timesAssign(n: Float) = set(n * x, n * y)
    operator fun timesAssign(n: Int) = set(n * x, n * y)

    operator fun div(n: Float) = Vec(x / n, y / n)
    operator fun divAssign(n: Float) = set(x / n, y / n)

    operator fun unaryMinus() = Vec(-x, -y)
    operator fun compareTo(vec: Vec): Int = squareLength.compareTo(vec.squareLength)
    override operator fun equals(other: Any?): Boolean =
        if (other is Vec)
            (x == other.x) && (y == other.y)
        else
            false


    var length: Float
        get() = sqrt(squareLength)
        set(value) {
            normalize()
            this *= value
        }
    val squareLength: Float
        get() = (x * x + y * y)

    val angle: Float
        get() = atan2(y, x)

    val tauAngle: Float
        get() = if (angle >= 0) angle else angle + TAU

    fun diffTauAngle(vec: Vec): Float = vec.tauAngle - tauAngle

    fun angleBetween(vec: Vec): Float {
        if (isZero() || vec.isZero())
            return 0f

        val fraction = (this dot vec) / (length * vec.length)
        return if (fraction <= -1) {
            PI
        } else if (fraction >= 1) {
            0f
        } else {
            acos(fraction)
        }
    }

    fun isZero(): Boolean = (x == 0f && y ==0f)


    fun normalize(): Vec {
        if (length != 0f) {
            this /= length
        }
        return this
    }

    fun limit(max: Float): Vec {
        if (squareLength > max * max)
            length = max
        return this
    }

    fun rotate(angle: Float): Vec {
        val newX = x * cos(angle) - y * sin(angle)
        y = x * sin(angle) + y * cos(angle)
        x = newX
        return this
    }

    fun getRotated(angle: Float) = Vec(
        x * cos(angle) - y * sin(angle),
        x * sin(angle) + y * cos(angle)
    )

    fun distance(vec: Vec): Float = sqrt(squareDistance(vec))

    fun squareDistance(vec: Vec): Float {
        val dx = x - vec.x
        val dy = y - vec.y
        return dx * dx + dy * dy
    }

    fun distanceToLine(linePoint1: Vec, linePoint2: Vec): Float = sqrt(squareDistanceToLine(linePoint1, linePoint2))

    fun squareDistanceToLine(linePoint1: Vec, linePoint2: Vec): Float {
        val normLine = (linePoint1 to linePoint2).normalize()
        val scalarProjection = (linePoint1 to this) dot normLine
        val projection = linePoint1 + normLine * scalarProjection
        return squareDistance(projection)
    }

    /**
     * Returns a new vector with a random position inside a circle around the given vector
     * @param radius The radius of the circle around the old vector
     */
    fun shiftInCircle(radius: Float): Vec {
        val angle = Vec.fromRandomAngle()
        val shift = angle * (radius * Random.nextFloat())
        return this + shift
    }

    infix fun to(vec: Vec): Vec = Vec(vec.x - x, vec.y - y)
    infix fun dot(vec: Vec): Float = x * vec.x + y * vec.y

    override fun toString(): String {
        val xStr = "%.2f".format(x)
        val yStr = "%.2f".format(y)
        return "Vec(x=$xStr, y=$yStr)"
    }

    private fun identityHashString(): String = System.identityHashCode(this).toString(16)

    fun lerp(vec: Vec, amount: Float): Vec = this + (vec - this) * amount

    fun lerpList(vec: Vec, count: Int) = List<Vec>(count) { i -> lerp(vec, (i.toFloat() / (count - 1))) }

    fun log(name: String = ""): String {
        val msg = StringBuilder()
        msg.append(if (name != "") "$name = " else "")
        msg.append("$this  id: ${identityHashString()}\n")
        msg.append("length=$length\n")
        msg.append("angle=$angle \t\t tauAngle=$tauAngle\n")
        return msg.toString()
    }

    fun logComparison(vec: Vec): String {
        val msg = StringBuilder()
        msg.appendln(" | $this | $vec | (compare=${compareTo(vec)})")
        msg.appendln("id  | " + identityHashString() + " | " + vec.identityHashString())
        msg.appendln("length | $length | ${vec.length}")
        msg.appendln("angle | $angle | ${vec.angle}")
        msg.appendln("tauAngle | $tauAngle | ${vec.tauAngle} | (diffTauAngle ${tauAngle - vec.tauAngle})")
        msg.appendln("angle degrees | ${tauAngle.toDegrees()} | ${vec.tauAngle.toDegrees()}")
        msg.appendln("angle between | ${angleBetween(vec).toDegrees().toInt()}")
        msg.appendln("diffTauAngle | ${diffTauAngle(vec).toDegrees().toInt()}")


        return msg.formatLikeTable().toString()
    }
}

fun StringBuilder.formatLikeTable(): StringBuilder {
    val separator = '|'
    val max = this.maxIndexOf(separator)

    if (max == -1)
        return this
    else {
        val nextStep = padIndexOf(separator, max)
        return nextStep.formatLikeTable()
    }
}

fun StringBuilder.padIndexOf(char: Char, length: Int, padChar: Char = ' ', excludeChar: Boolean = true): StringBuilder {
    val result = StringBuilder()
    for (str in this.split("\n")) {
        result.append(str.padIndexOf('|', length) + "\n")
    }
    return result
}

fun StringBuilder.maxIndexOf(char: Char): Int {
    var max = -1
    for (str in this.split("\n")) {
        max = maxOf(max, str.indexOf(char))
    }
    return max
}

fun String.padIndexOf(char: Char, length: Int, padChar: Char = ' ', excludeChar: Boolean = true): String {
    val index = this.indexOf(char)
    if (index == -1 || index > length)
        return this

    val padString = padChar.toString().repeat(length - index)
    val indexOfLastPart = if (excludeChar) index + 1 else index
    return this.substring(0, index) + padString + this.substring(indexOfLastPart)
}

fun Float.toDegrees(): Float = this * 360f / Vec.TAU
fun Float.toRadians(): Float = this * Vec.TAU / 360f

fun Vec.drawAsCircle(g: PGraphics, radius: Float = 8f) {
    g.ellipse(x, y, radius, radius)
}

//fun Vec.toPoint(canvas: CanvasLayer): Point = Point(this, canvas.getColor(this))


/**
 * A point divides a plane into four squares.
 * (upper1 -> 0, upper2 -> 1, lower1 -> 2, lower2 -> 3
 * quadIndex returns the index for vector this
 */
fun Vec.quadIndex(splitPoint: Vec): Int {
    val xIndex = if (x <= splitPoint.x) 0 else 1
    val yIndex = if (y <= splitPoint.y) 0 else 2
    return xIndex + yIndex
}

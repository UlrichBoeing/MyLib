package de.ulrich_boeing.mylib.geometry

import processing.core.PGraphics
import kotlin.math.cos
import kotlin.math.sin

class Circle(val center: Vec, val radius: Float): Figure {
    constructor() : this(Vec(0, 0), 1f)
    constructor(radius: Float) : this(Vec(0, 0), radius)
    constructor(x: Int, y: Int, radius: Float) : this(Vec(x, y), radius)

    /**
     * Punkte auf dem Rand des Kreises
     */
    fun edgePoints(count: Int): List<Vec> {
        val list = mutableListOf<Vec>()
        for (i in 0 until count) {
            val angle = (Vec.TAU * i) / count
            val x = center.x + radius * cos(angle)
            val y = center.y + radius * sin(angle)
            list.add(Vec(x, y))
        }
        return list
    }

    override fun contains(vec: Vec): Boolean = vec.squareDistance(center) <= radius.square()
    override fun pureContains(vec: Vec): Boolean = vec.squareDistance(center) < radius.square()

    override fun contains(rect: Rect): Boolean {
        // Finde den Eckpunkt des Rechtecks der am weitesten vom Mittelpunkt des Kreises entfernt ist
        val dx = max(center.x - rect.left, rect.right - center.x)
        val dy = max(center.y - rect.top, rect.bottom - center.y)
        // Ist dieser Punkt kleiner wie der Radius, liegen alle Punkte innerhalb des Kreises
        return dx.square() + dy.square() <= radius.square()

    }

    override fun pureContains(rect: Rect): Boolean {
        // Finde den Eckpunkt des Rechtecks der am weitesten vom Mittelpunkt des Kreises entfernt ist
        val dx = max(center.x - rect.left, rect.right - center.x)
        val dy = max(center.y - rect.top, rect.bottom - center.y)
        // Ist dieser Punkt kleiner wie der Radius, liegen alle Punkte innerhalb des Kreises
        return dx.square() + dy.square() < radius.square()
    }

    override infix fun intersects(rect: Rect): Boolean {
        val distanceToRect = Vec(abs(center.x - rect.center.x), abs(center.y - rect.center.y))

        return when {
            distanceToRect.x > (radius + rect.width / 2) -> false
            distanceToRect.y > (radius + rect.height / 2) -> false
            distanceToRect.x <= rect.width / 2 -> true
            distanceToRect.y <= rect.height / 2 -> true
            else -> {
                val cornerDistanceSquared = (distanceToRect.x - rect.width / 2).square() + (distanceToRect.y - rect.height / 2).square()
                cornerDistanceSquared <= radius.square()
            }
        }
    }

    override infix fun pureIntersects(rect: Rect): Boolean {
        val distanceToRect = Vec(abs(center.x - rect.center.x), abs(center.y - rect.center.y))

        return when {
            distanceToRect.x >= (radius + rect.width / 2) -> false
            distanceToRect.y >= (radius + rect.height / 2) -> false
            distanceToRect.x < rect.width / 2 -> true
            distanceToRect.y < rect.height / 2 -> true
            else -> {
                val cornerDistanceSquared = (distanceToRect.x - rect.width / 2).square() + (distanceToRect.y - rect.height / 2).square()
                cornerDistanceSquared < radius.square()
            }
        }
    }
}

fun Circle.draw(g: PGraphics) {
    g.ellipse(center.x, center.y, radius * 2, radius * 2)
}

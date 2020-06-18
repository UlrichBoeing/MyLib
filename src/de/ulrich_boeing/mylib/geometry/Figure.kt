package de.ulrich_boeing.mylib.geometry

interface Figure {
    fun contains(vec: Vec): Boolean
    fun pureContains(vec: Vec): Boolean

    fun contains(rect: Rect): Boolean
    fun pureContains(rect: Rect): Boolean

    infix fun intersects(other: Rect): Boolean
    infix fun pureIntersects(other: Rect): Boolean
}
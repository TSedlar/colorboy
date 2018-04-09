package com.colorboy.api.util.fx

import java.awt.Color
import java.awt.image.LookupTable

typealias RGBFilter = (r: Int, g: Int, b: Int) -> Boolean

/**
 * @author Tyler Sedlar
 * @since 4/7/2018
 */
class ColorMapper(private val filter: RGBFilter, to: Color) : LookupTable(0, 4) {

    private val to: IntArray = intArrayOf(to.red, to.green, to.blue)

    override fun lookupPixel(src: IntArray, dest: IntArray?): IntArray {
        var target = dest

        if (target == null) {
            target = IntArray(src.size)
        }

        val newColor = if (filter(src[0], src[1], src[2])) to else src
        System.arraycopy(newColor, 0, target, 0, newColor.size)

        return target
    }
}
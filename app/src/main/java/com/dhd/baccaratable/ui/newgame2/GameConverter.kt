package com.dhd.baccaratable.ui.newgame2

class GameConverter {
    //@TypeConverter
    fun load(coup: String): ArrayList<Int> {
        return coup.split(",").map { it.toInt() } as ArrayList<Int>
    }

    //@TypeConverter
    fun save(coupArray: ArrayList<Int>): String {
        return coupArray.joinToString(separator = ",")
    }
}
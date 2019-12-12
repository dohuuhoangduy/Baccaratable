package com.dhd.baccaratable
import android.content.Context
import java.io.Serializable
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


class Game (): Serializable{

    var coupArray: ArrayList<Int> = ArrayList()
    var row0: ArrayList<Int> = ArrayList()
    var row1: ArrayList<Int> = ArrayList()
    var row2: ArrayList<Int> = ArrayList()
    var row3: ArrayList<Int> = ArrayList()
    var row4: ArrayList<Int> = ArrayList()
    var rows = arrayListOf(row0, row1, row2, row3, row4)

    var maxSame: Int = 0

    var oneSameCount: Int = 0
    var maxContOneSame: Int = 0
    var oneEmptyCount: Int = 0
    var maxContOneEmpty: Int = 0

    var twoSameCount: Int = 0
    var maxContTwoSame: Int = 0
    var twoEmptyCount: Int = 0
    var maxContTwoEmpty: Int = 0

    var threeSameCount: Int = 0
    var maxContThreeSame: Int = 0
    var threeEmptyCount: Int = 0
    var maxContThreeEmpty: Int = 0

    var fourSameCount: Int = 0
    var maxContFourSame: Int = 0
    var fourEmptyCount: Int = 0
    var maxContFourEmpty: Int = 0

    var sameArray = ArrayList<Int>()
    var emptyArray = ArrayList<Int>()

    var groupThreeString = ""


    var resultArray = ArrayList<Int>()
    var totalResultArray = ArrayList<Int>()
    var lastNineResult = 0
    var totalResult = 0

    var playerResultArray = ArrayList<Int>()
    var bankerResultArray = ArrayList<Int>()

    init {

    }

    constructor(coupArray:ArrayList<Int>):this() {
        this.coupArray = ArrayList(coupArray)
    }

    constructor(game:Game):this() {
        this.coupArray = ArrayList(game.coupArray)
        this.row0 = ArrayList(game.row0)
        this.row1 = ArrayList(game.row1)
        this.row2 = ArrayList(game.row2)
        this.row3 = ArrayList(game.row3)
        this.row4 = ArrayList(game.row4)
        this.rows = arrayListOf(row0, row1, row2, row3, row4)

        this.maxSame = game.maxSame

        this.oneSameCount = game.oneSameCount
        this.maxContOneSame = game.maxContOneSame
        this.oneEmptyCount = game.oneEmptyCount
        this.maxContOneEmpty = game.maxContOneEmpty

        this.twoSameCount = game.twoSameCount
        this.maxContTwoSame = game.maxContTwoSame
        this.twoEmptyCount = game.twoEmptyCount
        this.maxContTwoEmpty = game.maxContTwoEmpty

        this.threeSameCount = game.threeSameCount
        this.maxContThreeSame = game.maxContThreeSame
        this.threeEmptyCount = game.threeEmptyCount
        this.maxContThreeEmpty = game.maxContThreeEmpty

        this.fourSameCount = game.fourSameCount
        this.maxContFourSame = game.maxContFourSame
        this.fourEmptyCount = game.fourEmptyCount
        this.maxContFourEmpty = game.maxContFourEmpty

        this.sameArray = ArrayList(game.sameArray)
        this.emptyArray = ArrayList(game.emptyArray)

        this.groupThreeString = game.groupThreeString

        this.resultArray = ArrayList(game.resultArray)
        this.totalResultArray = ArrayList(game.totalResultArray)
        this.lastNineResult = game.lastNineResult
        this.totalResult = game.totalResult

        this.bankerResultArray = ArrayList(game.bankerResultArray)
        this.playerResultArray = ArrayList(game.playerResultArray)

    }

    // Constant with a file name
    var fileName = "game.ser"

    // Serializes an object and saves it to a file
    fun saveToFile(context: Context, fileName: String) {

        try {
            val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(this)
            objectOutputStream.close()
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    // Creates an object by reading it from a file
    fun readFromFile(context: Context): Game? {
        var game: Game? = null
        try {
            val fileInputStream = context.openFileInput(fileName)
            val objectInputStream = ObjectInputStream(fileInputStream)
            game = objectInputStream.readObject() as Game
            objectInputStream.close()
            fileInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        return game
    }
}


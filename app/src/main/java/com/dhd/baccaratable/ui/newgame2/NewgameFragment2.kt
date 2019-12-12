package com.dhd.baccaratable.ui.newgame2

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.Fragment
import com.dhd.baccaratable.Game
import com.dhd.baccaratable.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import com.dhd.baccaratable.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.util.TypedValue
import android.view.*

import android.text.style.ForegroundColorSpan
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_newgame.resultTable
import kotlinx.android.synthetic.main.fragment_newgame2.*
import java.io.*
import androidx.core.content.FileProvider
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import com.dhd.baccaratable.BuildConfig


class NewgameFragment2 : Fragment() {
    private lateinit var savedGameViewModel: SavedGameViewModel
    private val savedGameActivityRequestCode = 1


    private val NO_PREDICT = 0
    private val NOT_FOUND = 2
    private val DEFAULT_FILTER_SIZE = 6

    private val ADD = 1
    private val REMOVE = -1
    private val REDRAW = 2
    private val CLEAR = 0

    var currentGame = Game(ArrayList<Int>())
    var menuExpanded = false

    var row1Pattern = ArrayList<ArrayList<Int>>()
    var row2Pattern = ArrayList<ArrayList<Int>>()
    var row3Pattern = ArrayList<ArrayList<Int>>()

    var patternMap = mutableMapOf(
        1 to arrayListOf(NOT_FOUND), 2 to arrayListOf(NOT_FOUND),
        3 to arrayListOf(NOT_FOUND), 4 to arrayListOf(NOT_FOUND), 5 to arrayListOf(NOT_FOUND),
        6 to arrayListOf(NOT_FOUND),
        7 to arrayListOf(NOT_FOUND), 8 to arrayListOf(NOT_FOUND)
    )
    var prevPattern = 0
    var prevPatternArray = ArrayList<Int>()
    var dupPatternResult = 0
    var prevPatternFollow = NOT_FOUND
    var dupPatternResultArray = ArrayList<Int>()
    var dupPatternTotal = 0

    var filteredList = ArrayList<Game>()
    var nextCoupList = ArrayList<Int>()

    var sameNextCoup = false
    var sameNextCoupArray = ArrayList<Boolean>()
    var threeDiff = false
    var threeDiffArray = ArrayList<Boolean>()
    var onlyOneMatch = false
    var onlyOneMatchArray = ArrayList<Boolean>()
    var nextCoupPredict = NOT_FOUND
    var nextCoupPredictArray = ArrayList<Int>()
    var filterSize = DEFAULT_FILTER_SIZE
    var filterSizeArray = ArrayList<Int>()

    var resultArray = ArrayList<Int>()
    var totalResult = 0

    var currentCoup = 0
    var coupIndex = 0

    var playerResultArray = ArrayList<Int>()
    var bankerResultArray = ArrayList<Int>()

    var colIndex = 0
    var rowIndex = 0

    var fileList = ArrayList<String>()
    var fileName = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_newgame2, container, false)

        val prefs = root.context.getSharedPreferences("preferences", 0)
        var savedGameIndex = prefs.getInt("savedGameIndex", 0)
        if (savedGameIndex == 0) {
            val editor = prefs.edit()
            editor.putInt("savedGameIndex", 1)
            editor.apply()
        }

        val scoreTable = root.findViewById<TableLayout>(R.id.scoreTable)
        val patternTable = root.findViewById<TableLayout>(R.id.patternTable)
        val dupPatternTable = root.findViewById<TableLayout>(R.id.dupPatternTable)
        val resultTable = root.findViewById<TableLayout>(R.id.resultTable)
        val predictTable = root.findViewById<TableLayout>(R.id.predictTable)
        val coupIndexText = root.findViewById<TextView>(R.id.coupIndexText)

        val gd = GradientDrawable()
        gd.shape = GradientDrawable.OVAL
        gd.setSize(32, 32)
        gd.setVisible(false, true)

        savedGameViewModel = ViewModelProvider(this).get(SavedGameViewModel::class.java)

        val gameArray = (activity as MainActivity).gameArray

        fileList = getArrayPrefs("fileList", root.context)

        val filepath =
            "Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)"
        var myExternalFile: File =
            File(root.context.getExternalFilesDir(filepath), "savedGames.csv")



        drawTable(scoreTable, GradientDrawable.OVAL, 20, false, 96)
        drawTextTable(patternTable)
        drawTextTable(dupPatternTable)
        drawTextTable(resultTable)
        drawTable(predictTable, GradientDrawable.OVAL, 10, false, 96)


        val bankerButton: FloatingActionButton = root.findViewById(R.id.bankerButton)
        bankerButton.setOnClickListener { _ ->
            if (currentGame.coupArray.size < 60) {
                currentCoup = 1
                currentGame.coupArray.add(1)

                updateScoreTable(scoreTable, currentGame, ADD)
                updatePatternTable(patternTable, currentGame.coupArray, ADD)
                updateDupPatternTable(dupPatternTable, currentGame, ADD)

                updateResult(currentGame, currentGame.coupArray.size - 1)
                updateResultTable(resultTable, currentGame, ADD)

                filter(gameArray, currentGame.coupArray)

                updatePredict(predictTable, nextCoupList)

                coupIndex++

                updateLabels()

                if (rowIndex < 2) rowIndex++
                else {
                    rowIndex = 0
                    colIndex++
                }
            } else {
                val toast = Toast.makeText(root.context, "Cannot add more", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        val playerButton: FloatingActionButton = root.findViewById(R.id.playerButton)
        playerButton.setOnClickListener { _ ->
            if (currentGame.coupArray.size < 60) {
                currentCoup = 0
                currentGame.coupArray.add(0)

                updateScoreTable(scoreTable, currentGame, ADD)
                updatePatternTable(patternTable, currentGame.coupArray, ADD)
                updateDupPatternTable(dupPatternTable, currentGame, ADD)

                updateResult(currentGame, currentGame.coupArray.size - 1)
                updateResultTable(resultTable, currentGame, ADD)

                filter(gameArray, currentGame.coupArray)

                updatePredict(predictTable, nextCoupList)

                coupIndex++

                updateLabels()

                if (rowIndex < 2) rowIndex++
                else {
                    rowIndex = 0
                    colIndex++
                }


            } else {
                val toast = Toast.makeText(root.context, "Cannot add more", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        val backButton: FloatingActionButton = root.findViewById(R.id.backButton)
        backButton.setOnClickListener { _ ->
            if (currentGame.coupArray.size > 0) {
                var currentRow = (currentGame.coupArray.size - 1) % 3
                when (currentRow) {
                    0 -> {
                        if (row1Pattern.size > 0) {
                            if (row1Pattern.last().size > 0) row1Pattern.last().removeAt(row1Pattern.last().size - 1)
                            else row1Pattern.removeAt(row1Pattern.size - 1)
                        }
                    }
                    1 -> {
                        if (row2Pattern.size > 0) {
                            if (row2Pattern.last().size > 0) row2Pattern.last().removeAt(row2Pattern.last().size - 1)
                            else row2Pattern.removeAt(row2Pattern.size - 1)
                        }
                    }
                    2 -> {
                        if (row3Pattern.size > 0) {
                            if (row3Pattern.last().size > 0) row3Pattern.last().removeAt(row3Pattern.last().size - 1)
                            else row3Pattern.removeAt(row3Pattern.size - 1)
                        }
                    }
                }
                if (rowIndex > 0) rowIndex--
                else {
                    rowIndex = 2
                    colIndex--
                }
                currentGame.coupArray.removeAt(currentGame.coupArray.size - 1)

                //playerResultArray.removeAt(playerResultArray.size - 1)
                //bankerResultArray.removeAt(bankerResultArray.size - 1)

                if (prevPatternArray.size > 0) {
                    prevPattern = prevPatternArray.removeAt(prevPatternArray.size - 1)
                    if (prevPattern > 0 && patternMap.getValue(prevPattern).size > 0) {
                        patternMap.getValue(prevPattern)
                            .removeAt(patternMap.getValue(prevPattern).size - 1)
                        prevPatternFollow = patternMap.getValue(prevPattern).last()
                    } else prevPatternFollow = NOT_FOUND
                } else {
                    prevPattern = 0
                    prevPatternFollow = NOT_FOUND
                }
                dupPatternTotal -= dupPatternResultArray.removeAt(dupPatternResultArray.size - 1)

                filterSize = filterSizeArray.removeAt(filterSizeArray.size - 1)
                filter(gameArray, currentGame.coupArray)

                updateScoreTable(scoreTable, currentGame, REMOVE)
                updatePatternTable(patternTable, currentGame.coupArray, REMOVE)
                updateDupPatternTable(dupPatternTable, currentGame, REMOVE)

                updateResultTable(resultTable, currentGame, REMOVE)
                updatePredict(predictTable, nextCoupList)

                if (resultArray.size > 0) {
                    totalResult -= resultArray[resultArray.size - 1]
                    resultArray.removeAt(resultArray.size - 1)
                } else totalResult = 0

                coupIndex--

                updateLabels()
            }
        }

        val newButton: FloatingActionButton = root.findViewById(R.id.newButton)
        newButton.setOnClickListener { _ ->
            toggleMenuButton()
            val mAlertDialog = AlertDialog.Builder(root.context)

            mAlertDialog.setTitle("New game") //set alertdialog title
            mAlertDialog.setMessage("Are you sure to start a new game?") //set alertdialog message
            mAlertDialog.setPositiveButton("Yes") { _, _ ->
                //perform some tasks here
                backButton.show()
                currentGame = Game(ArrayList<Int>())
                //currentGame.coupArray.clear()
                clearVariables()
                clearTables()
                clearLabels()
            }
            mAlertDialog.setNegativeButton("No") { _, _ ->

            }
            mAlertDialog.show()
        }

        val openButton: FloatingActionButton = root.findViewById(R.id.openButton)
        openButton.setOnClickListener { _ ->
            toggleMenuButton()

            var openGameIndex = 0
            val builder = AlertDialog.Builder(root.context)
            //val inflater = layoutInflater
            builder.setTitle("Open game")
            val dialogLayout = inflater.inflate(R.layout.open_game_dialog, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.editText)
            val dialogOpenButton = dialogLayout.findViewById<Button>(R.id.dialogOpenButton)
            builder.setView(dialogLayout)

            var dialog = builder.show()
            editText.requestFocus()
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)


            dialogOpenButton.setOnClickListener { _ ->
                if (editText.text.isEmpty())
                    Toast.makeText(
                        root.context,
                        "Enter number between 1 and ${gameArray.size}",
                        Toast.LENGTH_SHORT
                    ).show()
                else {
                    openGameIndex = editText.text.toString().toInt() - 1

                    if (openGameIndex < 0 || openGameIndex >= gameArray.size)
                        Toast.makeText(
                            root.context,
                            "Enter number between 1 and ${gameArray.size}",
                            Toast.LENGTH_SHORT
                        ).show()
                    else {
                        backButton.hide()
                        clearVariables()
                        clearTables()
                        clearLabels()

                        coupIndex = 59
                        var gameArrayWithoutOpened = ArrayList(gameArray)
                        var openedGame = gameArrayWithoutOpened.removeAt(openGameIndex)
                        currentGame = Game(loadGame(gameArrayWithoutOpened, openedGame))
                        OpenGame(scoreTable, resultTable, currentGame)
                        coupIndexText.text = "60"

                        updateResultLabel(resultLabel)

                        dialog.dismiss()

                    }
                }

            }
        }


        val saveButton: FloatingActionButton = root.findViewById(R.id.saveButton)
        saveButton.setOnClickListener { _ ->
            toggleMenuButton()
            if (currentGame.coupArray.size < 60) Toast.makeText(
                root.context,
                "Please complete game before saving",
                Toast.LENGTH_SHORT
            ).show()
            else {
                val mAlertDialog = AlertDialog.Builder(root.context)

                mAlertDialog.setTitle("Save game") //set alertdialog title
                mAlertDialog.setMessage("Are you sure to save current game?") //set alertdialog message
                mAlertDialog.setPositiveButton("Save") { _, _ ->

                    var coupArrayString = currentGame.coupArray.joinToString(separator = ",")
                    val coupString = coupArrayString.toString()
                    coupArrayString += "\n"
                    var date = Date()
                    val formatter = SimpleDateFormat("MMM/dd/yyyy HH:mm")
                    savedGameIndex = prefs.getInt("savedGameIndex", 0)
                    val title = "Game $savedGameIndex"
                    val dateString: String = formatter.format(date)
                    savedGameViewModel.insert(SavedGame(title, coupString, dateString))
                    savedGameIndex++
                    val editor = prefs.edit()
                    editor.putInt("savedGameIndex", savedGameIndex)
                    editor.apply()

                    try {
                        myExternalFile.appendText(coupArrayString)
                        Toast.makeText(root.context, "Game has been saved", Toast.LENGTH_SHORT)
                            .show()

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
                mAlertDialog.setNegativeButton("Cancel") { _, _ ->

                }
                mAlertDialog.show()
            }
        }


        val menuButton: FloatingActionButton = root.findViewById(R.id.menuButton)
        menuButton.setOnClickListener { _ ->
            toggleMenuButton()
        }

        val openFileButton: FloatingActionButton = root.findViewById(R.id.openFileButton)
        openFileButton.setOnClickListener { _ ->
            val fileUri = FileProvider.getUriForFile(
                root.context,
                BuildConfig.APPLICATION_ID + ".provider", myExternalFile
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                type = "application/excel"
                //type = "*/*"
                data = fileUri
                putExtra(Intent.EXTRA_MIME_TYPES, "text/plain")
                setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            if (intent.resolveActivity(root.context.packageManager) != null) {
                startActivity(intent)
            }
        }

        val openSavedButton: FloatingActionButton = root.findViewById(R.id.openSavedButton)
        openSavedButton.setOnClickListener { _ ->
            val intent = Intent(root.context, SavedGameActivity::class.java)
            startActivityForResult(intent, savedGameActivityRequestCode)
        }

        return root
    }


    private fun toggleMenuButton() {
        menuExpanded = !menuExpanded
        if (menuExpanded) menuButton.setImageResource(android.R.drawable.arrow_down_float)
        else menuButton.setImageResource(android.R.drawable.arrow_up_float)
        newButton.isVisible = !newButton.isVisible
        openButton.isVisible = !openButton.isVisible
        saveButton.isVisible = !saveButton.isVisible
        openFileButton.isVisible = !openFileButton.isVisible
    }

    private fun drawTable(
        table: TableLayout,
        shape: Int,
        numOfColumn: Int,
        stretch: Boolean,
        size: Int
    ) {
        var i = 0
        while (i < table.childCount) {
            var row = table.getChildAt(i) as TableRow
            var j = 0
            while (j < numOfColumn) {
                val gd = GradientDrawable()
                gd.shape = shape
                gd.setSize(24, 24)
                gd.setVisible(false, true)

                var image = ImageView(this.context)
                image.setImageDrawable(gd)
                image.setPadding(8, 8, 8, 8)

                var border = GradientDrawable()
                border.setColor(Color.parseColor("#FFFFFF"))
                border.setStroke(1, Color.parseColor("#000000"))
                if (size > 0) border.setSize(size, size)
                image.background = border

                row.addView(image)
                j++
            }
            i++
        }
        table.isStretchAllColumns = stretch
    }

    private fun drawTextTable(table: TableLayout) {

        var i = 0
        while (i < table.childCount) {
            var row = table.getChildAt(i) as TableRow
            var j = 0
            while (j < 20) {
                val textView = TextView(this.context)
                textView.gravity = Gravity.CENTER
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
                textView.setTypeface(null, Typeface.BOLD)

                var border = GradientDrawable()
                border.setColor(Color.parseColor("#FFFFFF"))
                border.setStroke(1, Color.parseColor("#000000"))
                border.setSize(96, 96)

                textView.background = border

                row.addView(textView)
                j++
            }
            i++
        }
        //table.setStretchAllColumns(true)
    }

    private fun clearTables() {
        clearTable(scoreTable)
        clearTextTable(patternTable)
        clearTextTable(resultTable)
        clearTextTable(resultTable)
        clearTextTable(dupPatternTable)
        clearTable(predictTable)
    }


    private fun updateScoreTable(table: TableLayout, game: Game, updateType: Int) {
        when (updateType) {
            ADD -> {
                var addedScore = game.coupArray[game.coupArray.size - 1]
                var row = table.getChildAt(rowIndex) as TableRow
                var image = row.getChildAt(colIndex) as ImageView
                var gd = image.drawable as GradientDrawable
                when (addedScore) {
                    0 -> {
                        gd.setColor(resources.getColor(R.color.PLAYER, null))
                        gd.setVisible(true, true)
                    }
                    1 -> {
                        gd.setColor(resources.getColor(R.color.BANKER, null))
                        gd.setVisible(true, true)
                    }
                }
            }
            REMOVE -> {
                removeLast(table)
            }
            CLEAR -> {
                clearTable(table)
            }
            REDRAW -> {
                rowIndex = 0
                colIndex = 0

                for (score in game.coupArray) {
                    var row = table.getChildAt(rowIndex) as TableRow
                    var image = row.getChildAt(colIndex) as ImageView
                    var gd = image.drawable as GradientDrawable
                    when (score) {
                        0 -> {
                            gd.setColor(resources.getColor(R.color.PLAYER, null))
                            gd.setVisible(true, true)
                        }
                        1 -> {
                            gd.setColor(resources.getColor(R.color.BANKER, null))
                            gd.setVisible(true, true)
                        }
                    }
                    if (rowIndex < 2) rowIndex++
                    else {
                        rowIndex = 0
                        colIndex++
                    }
                }
            }
        }
    }

    private fun updatePatternTable(table: TableLayout, coupArray: ArrayList<Int>, updateType: Int) {
        when (updateType) {
            ADD -> {

                if (coupArray.size >= 3) {
                    var patternValue = 0
                    var patternList =
                        coupArray.subList(coupArray.size - 3, coupArray.size)
                    when (patternList.toString()) {
                        "[0, 0, 0]" -> {
                            patternValue = 1

                        }
                        "[0, 0, 1]" -> patternValue = 2
                        "[0, 1, 0]" -> patternValue = 3
                        "[0, 1, 1]" -> patternValue = 4
                        "[1, 0, 0]" -> patternValue = 5
                        "[1, 0, 1]" -> patternValue = 6
                        "[1, 1, 0]" -> patternValue = 7
                        "[1, 1, 1]" -> patternValue = 8
                    }

                    // Update pattern map for duplicate pattern table

                    if (prevPattern > 0) {
                        var prevPatternValueArray = patternMap.getValue(prevPattern)

                        if (prevPatternValueArray.last() != NOT_FOUND) {
                            if (prevPatternValueArray.last() == coupArray.last()) dupPatternResult =
                                1
                            else dupPatternResult = -1
                        } else dupPatternResult = 0
                        patternMap.getValue(prevPattern).add(coupArray.last())

                    } else dupPatternResult = 0

                    prevPatternFollow = patternMap.getValue(patternValue).last()
                    prevPatternArray.add(prevPattern)


                    prevPattern = patternValue

                    var row = table.getChildAt(rowIndex) as TableRow
                    var textView = row.getChildAt(colIndex) as TextView
                    textView.text = patternValue.toString()
                    if (patternValue > 4) textView.setTextColor(Color.RED)
                    else textView.setTextColor(Color.BLACK)

                    var background = textView.background as GradientDrawable
                    var currentRow = (coupArray.size - 1) % 3
                    when (currentRow) {
                        0 -> {
                            if (row1Pattern.size == 0) row1Pattern.add(ArrayList<Int>())
                            if (row1Pattern.last().contains(patternValue)) {
                                background.setColor(
                                    resources.getColor(
                                        R.color.duplicatePattern,
                                        null
                                    )
                                )
                                row1Pattern.add(ArrayList<Int>())
                            } else row1Pattern.last().add(patternValue)
                        }
                        1 -> {
                            if (row2Pattern.size == 0) row2Pattern.add(ArrayList<Int>())
                            if (row2Pattern.last().contains(patternValue)) {
                                background.setColor(
                                    resources.getColor(
                                        R.color.duplicatePattern,
                                        null
                                    )
                                )
                                row2Pattern.add(ArrayList<Int>())
                            } else row2Pattern.last().add(patternValue)
                        }
                        2 -> {
                            if (row3Pattern.size == 0) row3Pattern.add(ArrayList<Int>())
                            if (row3Pattern.last().contains(patternValue)) {
                                background.setColor(
                                    resources.getColor(
                                        R.color.duplicatePattern,
                                        null
                                    )
                                )
                                row3Pattern.add(ArrayList<Int>())
                            } else row3Pattern.last().add(patternValue)
                        }
                    }

                } else {
                    dupPatternResult = 0
                }
                dupPatternTotal += dupPatternResult
                dupPatternResultArray.add(dupPatternResult)


            }
            REMOVE -> {
                removeLastText(table)
            }
            CLEAR -> {
                clearTextTable(table)
            }
        }
    }

    /*private fun updateMatchTable(table: TableLayout, game: Game, updateType: Int) {
        when (updateType) {
            ADD -> {
                var result = predictMatchResultArray.last()
                val row = table.getChildAt(rowIndex) as TableRow
                var textView = row.getChildAt(colIndex) as TextView

                textView.text = result.toString()
                if (result == 0) textView.setTextColor(Color.TRANSPARENT)
                else if (result > 0) textView.setTextColor(Color.RED)
                else textView.setTextColor(Color.BLACK)

                val background = textView.background as GradientDrawable
                if (matchPredictCoup != NOT_FOUND && !onlyOneMatch) {
                    when(result) {
                        1 -> background.setColor(resources.getColor(R.color.lightYellow, null))
                        (-1) -> background.setColor(resources.getColor(R.color.lightBlue, null))
                    }
                }
            }
            REMOVE -> {
                removeLastText(table)
            }
            CLEAR -> {
                clearTable(table)
            }
            REDRAW -> {

            }
        }
    }*/

    private fun updateDupPatternTable(table: TableLayout, game: Game, updateType: Int) {
        when (updateType) {
            ADD -> {
                var row = table.getChildAt(rowIndex) as TableRow
                var textView = row.getChildAt(colIndex) as TextView
                textView.text = dupPatternResult.toString()
                if (dupPatternResult == 0) textView.setTextColor(Color.TRANSPARENT)
                else if (dupPatternResult > 0) textView.setTextColor(Color.RED)
                else textView.setTextColor(Color.BLACK)


            }
            REMOVE -> {
                removeLastText(table)
            }
        }
    }

    private fun updateResultTable(table: TableLayout, game: Game, updateType: Int) {
        if (resultArray.size > 0) {

            when (updateType) {
                ADD -> {
                  /*  var currentResultArray = ArrayList<Int>()
                    var playerCount = 0
                    var bankerCount = 0*/

                  /*  currentResultArray = resultArray
                    playerCount = playerResultArray.last()
                    bankerCount = bankerResultArray.last()*/


                    val result = resultArray.last()
                    val row = table.getChildAt(rowIndex) as TableRow
                    var textView = row.getChildAt(colIndex) as TextView
                    var background = textView.background as GradientDrawable
                    textView.text = result.toString()
                    if (result == 0) textView.setTextColor(Color.TRANSPARENT)
                    else if (result > 0) textView.setTextColor(Color.RED)
                    else textView.setTextColor(Color.BLACK)

                    if (onlyOneMatch) background.setColor(getResources().getColor(R.color.lightGreen, null))
                    else if (sameNextCoup) background.setColor(getResources().getColor(R.color.lightYellow, null))
                    else if (threeDiff) background.setColor(getResources().getColor(R.color.lightBlue, null))



                }
                REMOVE -> {
                    removeLastText(table)
                }
                CLEAR -> {
                    clearTable(table)
                }
                REDRAW -> {

                }
            }
        }
    }

    private fun updatePredict(table: TableLayout, predictArray: ArrayList<Int>) {
        var numOfColumn = (predictArray.size / 3) + 1

        if (numOfColumn < 10) numOfColumn = 10

        var row = 0
        while (row < predictTable.childCount) {
            var predictRow = predictTable.getChildAt(row) as TableRow
            predictRow.removeAllViews()
            row++
        }

        drawTable(predictTable, GradientDrawable.OVAL, numOfColumn, false, 96)
        var i = 0
        var predictRowIndex = 0
        var predictColIndex = 0
        while (i < predictArray.size) {
            var row = table.getChildAt(predictRowIndex) as TableRow
            try {
                var image = row.getChildAt(predictColIndex) as ImageView
                var gd = image.drawable as GradientDrawable
                when (predictArray[i]) {
                    0 -> {
                        gd.setColor(resources.getColor(R.color.PLAYER, null))
                        gd.setVisible(true, true)
                    }
                    1 -> {
                        gd.setColor(resources.getColor(R.color.BANKER, null))
                        gd.setVisible(true, true)
                    }
                }
            }
            catch (e : Exception){
                Log.e("null", currentGame.coupArray.toString())
            }


            predictRowIndex++
            if (predictRowIndex > 2) {
                predictRowIndex = 0
                predictColIndex++
            }
            i++
        }
    }

    private fun removeLast(table: TableLayout) {
        val row = table.getChildAt(rowIndex) as TableRow
        val image = row.getChildAt(colIndex) as ImageView
        val gd = image.drawable as GradientDrawable
        gd.color = null
        gd.setVisible(false, true)
        val imageBackground = image.background as GradientDrawable
        imageBackground.setColor(Color.parseColor("#FFFFFF"))
    }

    private fun removeLastText(table: TableLayout) {
        val row = table.getChildAt(rowIndex) as TableRow
        val textView = row.getChildAt(colIndex) as TextView
        textView.setTextColor(Color.TRANSPARENT)
        val background = textView.background as GradientDrawable
        background.setColor(null)
    }

    private fun clearTable(table: TableLayout) {
        var i = 0
        while (i < table.childCount) {
            val row = table.getChildAt(i) as TableRow
            var j = 0
            while (j < row.childCount) {
                val image = row.getChildAt(j) as ImageView
                val gd = image.drawable as GradientDrawable
                gd.color = null
                gd.setVisible(false, true)
                val imageBackground = image.background as GradientDrawable
                imageBackground.setColor(Color.parseColor("#FFFFFF"))
                //image.setBackgroundColor(Color.parseColor("#FFFFFF"))
                j++
            }
            i++
        }
    }

    private fun clearTextTable(table: TableLayout) {
        var i = 0
        while (i < table.childCount) {
            val row = table.getChildAt(i) as TableRow
            var j = 0
            while (j < row.childCount) {
                val textView = row.getChildAt(j) as TextView
                textView.setTextColor(Color.TRANSPARENT)
                val background = textView.background as GradientDrawable
                background.setColor(Color.TRANSPARENT)
                j++
            }
            i++
        }
    }

    private fun updatePredictLabel(totalLabel: TextView) {

        var playerCountTotal = nextCoupList.filter { it.equals(0) }.size
        var bankerCountTotal = nextCoupList.filter { it.equals(1) }.size

        val totalBuilder = SpannableStringBuilder()

        val totalPlayerText = SpannableString(playerCountTotal.toString())
        totalPlayerText.setSpan(ForegroundColorSpan(Color.BLUE), 0, totalPlayerText.length, 0)
        totalBuilder.append(totalPlayerText)

        totalBuilder.append(" - ")

        val totalBankerText = SpannableString(bankerCountTotal.toString())
        totalBankerText.setSpan(ForegroundColorSpan(Color.RED), 0, totalBankerText.length, 0)
        totalBuilder.append(totalBankerText)

        totalLabel.setText(totalBuilder, TextView.BufferType.SPANNABLE)
    }

    private fun clearPredictLabel() {

        predictLabel.text = ""
    }

    private fun filter(gameArray: ArrayList<Game>, patternList: ArrayList<Int>) {



        if (patternList.size >= filterSize) {

            var firstIndex = patternList.size - filterSize
            var filterPattern = patternList.subList(firstIndex, patternList.size)
            filteredList = gameArray.filter {
                it.coupArray.subList(firstIndex, patternList.size).equals(filterPattern)
            } as ArrayList

            nextCoupList.clear()
            for (matchingGame in filteredList) {
                var nextCoupIndex = patternList.size
                if (nextCoupIndex < matchingGame.coupArray.size)
                    nextCoupList.add(matchingGame.coupArray.get(nextCoupIndex))
            }

            var filteredplayerCount = nextCoupList.filter { it.equals(0) }.size
            var filteredbankerCount = nextCoupList.filter { it.equals(1) }.size


            while ((filteredplayerCount == filteredbankerCount) && filterSize > 1) {
                filterSize--
                firstIndex = patternList.size - filterSize
                filterPattern =
                    patternList.subList(firstIndex, patternList.size)
                filteredList = gameArray.filter {
                    it.coupArray.subList(firstIndex, patternList.size)
                        .equals(filterPattern)
                } as ArrayList

                nextCoupList.clear()
                for (matchingGame in filteredList) {
                    var nextCoupIndex = patternList.size
                    if (nextCoupIndex < matchingGame.coupArray.size)
                        nextCoupList.add(matchingGame.coupArray.get(nextCoupIndex))
                }

                filteredplayerCount = nextCoupList.filter { it.equals(0) }.size
                filteredbankerCount = nextCoupList.filter { it.equals(1) }.size
            }

        } else {
            nextCoupList.clear()
        }

    }

    private fun clearVariables() {
        rowIndex = 0
        colIndex = 0
        coupIndex = 0

        sameNextCoup = false
        sameNextCoupArray.clear()
        threeDiff = false
        threeDiffArray.clear()
        onlyOneMatch = false
        onlyOneMatchArray.clear()
        nextCoupPredict = NOT_FOUND
        nextCoupPredictArray.clear()
        filterSize = DEFAULT_FILTER_SIZE
        filterSizeArray.clear()

        filteredList.clear()
        nextCoupList.clear()
        resultArray.clear()
        totalResult = 0

        playerResultArray.clear()
        bankerResultArray.clear()

        row1Pattern.clear()
        row2Pattern.clear()
        row3Pattern.clear()

        patternMap = mutableMapOf(
            1 to arrayListOf(NOT_FOUND),
            2 to arrayListOf(NOT_FOUND),
            3 to arrayListOf(NOT_FOUND),
            4 to arrayListOf(NOT_FOUND),
            5 to arrayListOf(NOT_FOUND),
            6 to arrayListOf(NOT_FOUND),
            7 to arrayListOf(NOT_FOUND),
            8 to arrayListOf(NOT_FOUND)
        )
        prevPattern = 0
        dupPatternResult = 0
        prevPatternFollow = NOT_FOUND
        dupPatternResultArray.clear()
        dupPatternTotal = 0
    }


    private fun OpenGame(scoreTable: TableLayout, totalResultTable: TableLayout, game: Game) {
        updateScoreTable(scoreTable, game, REDRAW)
        drawResultTable(totalResultTable)
        drawDupPatternTable(dupPatternTable)
    }

    /* private fun drawMatchTable(table: TableLayout) {
        var i = 0
        rowIndex = 0
        colIndex = 0
        while (i < predictMatchResultArray.size) {

            val row = table.getChildAt(rowIndex) as TableRow
            val textView = row.getChildAt(colIndex) as TextView

            var result = predictMatchResultArray[i]
            textView.text = result.toString()

            if (result == 0) textView.setTextColor(Color.TRANSPARENT)
            else if (result > 0) textView.setTextColor(Color.RED)
            else textView.setTextColor(Color.BLACK)

            val background = textView.background as GradientDrawable
            if (matchPredictCoupArray[i] != NOT_FOUND && !onlyOneMatchArray[i]) {
                when(result) {
                    1 -> background.setColor(resources.getColor(R.color.lightYellow, null))
                    (-1) -> background.setColor(resources.getColor(R.color.lightBlue, null))
                }
            }

            if (rowIndex < 2) rowIndex++
            else {
                rowIndex = 0
                colIndex++
            }
            i++
        }
    }*/

    private fun drawDupPatternTable(table: TableLayout) {
        var i = 0
        rowIndex = 0
        colIndex = 0
        while (i < dupPatternResultArray.size) {

            val row = table.getChildAt(rowIndex) as TableRow
            val textView = row.getChildAt(colIndex) as TextView

            var result = dupPatternResultArray[i]
            textView.text = result.toString()
            if (result == 0) textView.setTextColor(Color.TRANSPARENT)
            else textView.setTextColor(Color.BLACK)

            if (rowIndex < 2) rowIndex++
            else {
                rowIndex = 0
                colIndex++
            }
            i++
        }
    }

    // Draw a game result array to the table UI
    private fun drawResultTable(table: TableLayout) {

        var currentResultArray = ArrayList<Int>()
        var playerCount = 0
        var bankerCount = 0
        currentResultArray = resultArray

        rowIndex = 0
        colIndex = 0

        var i = 0
        while (i < currentResultArray.size) {

            val row = table.getChildAt(rowIndex) as TableRow
            val textView = row.getChildAt(colIndex) as TextView
            val background = textView.background as GradientDrawable

            var result = currentResultArray[i]
            textView.text = result.toString()
            if (result == 0) textView.setTextColor(Color.TRANSPARENT)
            else if (result > 0) textView.setTextColor(Color.RED)
            else textView.setTextColor(Color.BLACK)

            var playerCount = 0
            var bankerCount = 0
            if (playerResultArray.size > 0) {
                playerCount = playerResultArray[i]
                bankerCount = bankerResultArray[i]

                if ((playerCount == 0 || bankerCount == 0) && kotlin.math.abs(playerCount - bankerCount) > 0)
                    background.setColor(resources.getColor(R.color.hasZero, null))
                else {
                    if (table.id == resultTable.id) {
                        if (kotlin.math.abs(playerCount - bankerCount) > 4) {
                            //background.setColor(resources.getColor(R.color.MAJORITY,null))
                            background.setColor(resources.getColor(R.color.lightYellow, null))
                        }
                    }
                }
            }

            if (rowIndex < 2) rowIndex++
            else {
                rowIndex = 0
                colIndex++
            }
            i++
        }
    }

    // Populate result for a game (for old games that do not have result array yet)
    private fun loadGame(gameArray: ArrayList<Game>, game: Game): Game {

        var i = 0
        rowIndex = 0
        colIndex = 0
        while (i < game.coupArray.size) {
            var patternList = ArrayList(game.coupArray.subList(0, i))

            filter(gameArray, patternList)
            updateResult(game, i)



            patternList = ArrayList(game.coupArray.subList(0, i + 1))
            updatePatternTable(patternTable, patternList, ADD)

            i++
            if (rowIndex < 2) rowIndex++
            else {
                rowIndex = 0
                colIndex++
            }
        }

        return game
    }

    /*private fun updateMatchResult(game: Game, currentIndex: Int) {
        when (matchPredictCoup) {
            NOT_FOUND -> predictMatchResult = NO_PREDICT
            game.coupArray[currentIndex] -> predictMatchResult = -1
            else -> predictMatchResult = 1
        }
        predictMatchResultArray.add(predictMatchResult)
        predictMatchTotal += predictMatchResult
    }
*/

    private fun updateResult(game: Game, currentIndex: Int) {

        onlyOneMatchArray.add(onlyOneMatch)
        threeDiffArray.add(threeDiff)
        sameNextCoupArray.add(sameNextCoup)

        onlyOneMatch = false
        threeDiff = false
        sameNextCoup = false

        filterSizeArray.add(filterSize)

        if (nextCoupList.size == 0 || currentIndex < 6) {
            resultArray.add(0)
            //playerResultArray.add(0)
            //bankerResultArray.add(0)

        } else {
            var playerCount = nextCoupList.filter { it.equals(0) }.size
            var bankerCount = nextCoupList.filter { it.equals(1) }.size

            if (nextCoupList.size == 1) {
                onlyOneMatch = true
                filterSize = DEFAULT_FILTER_SIZE
            }
            else if (nextCoupList.size == 3 && playerCount > 0 && bankerCount > 0) {
                threeDiff = true
                filterSize++
            }
            else if (playerCount == 0 || bankerCount == 0) {
                sameNextCoup = true
                filterSize = DEFAULT_FILTER_SIZE
            }
            else filterSize++


            var filterPredict = 0

            if (playerCount > bankerCount) filterPredict = 0
            else filterPredict = 1

            // actual added coup match prediction
            if (filterPredict == game.coupArray[currentIndex]) {
                resultArray.add(1)

            }
            // actual added coup doesn't match prediction
            else {
                resultArray.add(-1)
            }

            totalResult += resultArray.last()
            playerResultArray.add(playerCount)
            bankerResultArray.add(bankerCount)
        }
    }

    private fun setArrayPrefs(arrayName: String, array: ArrayList<String>, mContext: Context) {
        val prefs = mContext.getSharedPreferences("preferencename", 0)
        val editor = prefs.edit()
        editor.putInt(arrayName + "_size", array.size)
        var i = 0
        while (i < array.size) {
            editor.putString(arrayName + "_" + i, array[i])
            i++
        }

        editor.apply()
    }

    private fun getArrayPrefs(arrayName: String, mContext: Context): ArrayList<String> {
        val prefs = mContext.getSharedPreferences("preferencename", 0)
        val size = prefs.getInt(arrayName + "_size", 0)
        val array = ArrayList<String>(size)


        for (i in 0 until size) {
            array.add(prefs.getString(arrayName + "_" + i, null) as String)
        }

        return array
    }

    private fun removeArrayPref(arrayName: String, mContext: Context, position: Int) {
        val prefs = mContext.getSharedPreferences("preferencename", 0)
        prefs.edit().remove(arrayName + "_" + position)
        prefs.edit().apply()
    }

    private fun updateFileName(name: String) {
        fileName = name
    }

    private fun deleteSavedGame() {

    }

    private fun updateLabels() {
        coupIndexText.text = "$coupIndex"
        updateDupPatternLabel(dupPatternLabel)
        updateResultLabel(resultLabel)
        updatePredictLabel(predictLabel)
    }

    private fun clearLabels() {
        coupIndexText.text = ""
        dupPatternLabel.text = ""
        var gd = dupPatternPredictImg.drawable as GradientDrawable
        gd.setColor(Color.TRANSPARENT)
        resultLabel.text = ""
        clearPredictLabel()
    }

    private fun updateDupPatternLabel(textView: TextView) {
        textView.text = dupPatternTotal.toString()

        val gd = GradientDrawable()
        gd.shape = GradientDrawable.OVAL
        gd.setSize(60, 60)
        if (prevPatternFollow == 0) gd.setColor(Color.BLUE)
        else if (prevPatternFollow == 1) gd.setColor(Color.RED)
        else gd.setColor(Color.TRANSPARENT)

        dupPatternPredictImg.setImageDrawable(gd)
        //dupPatternPredictImg.setPadding(8, 8, 24, 8)
    }

    private fun updateResultLabel(textView: TextView) {
        textView.text = "${totalResult}"
        if (totalResult > 0) textView.setTextColor(Color.RED)
        else textView.setTextColor(Color.BLACK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == savedGameActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.let {

                val coupString = it.getStringExtra(SavedGameActivity.EXTRA_REPLY)
                openSavedGame(coupString)
            }
        } else {
            Toast.makeText(
                this.context,
                "No game selected",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun openSavedGame(coupString: String) {
        var coupArray = coupString.split(',').map{it.toInt()} as ArrayList<Int>

        val gameArray = (activity as MainActivity).gameArray
        val game = Game(coupArray)

        backButton.hide()
        clearVariables()
        clearTables()
        clearLabels()

        coupIndex = 59

        currentGame = Game(loadGame(gameArray, game))
        OpenGame(scoreTable,resultTable, currentGame)
        coupIndexText.text = "60"
        updateResultLabel(resultLabel)
    }
}
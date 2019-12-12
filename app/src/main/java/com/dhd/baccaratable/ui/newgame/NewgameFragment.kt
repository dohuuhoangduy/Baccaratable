package com.dhd.baccaratable.ui.newgame

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_newgame.*


class NewgameFragment : Fragment() {

    private val PREDICT_RIGHT = 1
    private val PREDICT_WRONG = -1
    private val NO_PREDICT = 0

    private val ADD = 1
    private val REMOVE = -1
    private val REDRAW = 2
    private val CLEAR = 0


    var filteredList = ArrayList<Game>()
    var filteredListLastNine = ArrayList<Game>()
    var nextCoupList = ArrayList<Int>()
    var nextCoupListLastNine = ArrayList<Int>()

    var currentCoup = 0
    var coupIndex = 0

    //var lastNineResult = 0
    //var resultArray = ArrayList<Int>()

    var colIndex = 0
    var rowIndex = 0

    var fileList = ArrayList<String>()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: RecyclerAdapter

    var fileName = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_newgame, container, false)

        val scoreTable = root.findViewById<TableLayout>(R.id.scoreTable)
        val resultTable = root.findViewById<TableLayout>(R.id.resultTable)
        val totalResultTable = root.findViewById<TableLayout>(R.id.resultTable)
        val predictTable = root.findViewById<TableLayout>(R.id.predictLastNineTable)
        val coupIndexText = root.findViewById<TextView>(R.id.coupIndexText)
        val resultLabel = root.findViewById<TextView>(R.id.resultLabel)
        val playerLastNine = root.findViewById<TextView>(R.id.playerLastNine)
        val playerTotalText = root.findViewById<TextView>(R.id.playerTotal)
        val bankerLastNine = root.findViewById<TextView>(R.id.bankerLastNine)
        val bankerTotalText = root.findViewById<TextView>(com.dhd.baccaratable.R.id.bankerTotal)

        val gameArray = (activity as MainActivity).gameArray

        fileList = getArrayPrefs("fileList", root.context)



        var currentGame = Game(ArrayList<Int>())

        drawTable(scoreTable, GradientDrawable.OVAL)
        drawTable(resultTable, GradientDrawable.RECTANGLE)
        drawTable(totalResultTable, GradientDrawable.RECTANGLE)
        drawTable(predictTable, GradientDrawable.OVAL)

        //drawBorder(coupIndexText)
        //drawBorder(resultLabel)

        val bankerButton: FloatingActionButton = root.findViewById(R.id.bankerButton)
        bankerButton.setOnClickListener { view ->
            if (currentGame.coupArray.size < 60) {
                currentCoup = 1
                currentGame.coupArray.add(1)
                updateResult(currentGame, currentGame.coupArray.size - 1)
                updateTotalResult(currentGame, currentGame.coupArray.size - 1)
                updateResultTable(resultTable, currentGame, ADD)
                updateResultTable(totalResultTable, currentGame, ADD)
                filterGame(gameArray, currentGame.coupArray)
                updateScoreTable(scoreTable, currentGame, ADD)
                updatePredict(predictTable)
                if (rowIndex < 2) rowIndex++
                else {
                    rowIndex = 0
                    colIndex++
                }
                showTotal(playerLastNine)
                showTotal(playerTotalText)
                showTotal(bankerLastNine)
                showTotal(bankerTotalText)
                coupIndex++
                coupIndexText.text = "$coupIndex"
                updateResultLabel(resultLabel, currentGame)
                updateTotalResultLabel(resultLabel, currentGame)
            }
            else {
                val toast = Toast.makeText(root.context, "Cannot add more", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        val playerButton: FloatingActionButton = root.findViewById(R.id.playerButton)
        playerButton.setOnClickListener { view ->
            if (currentGame.coupArray.size < 60) {
                currentCoup = 0
                currentGame.coupArray.add(0)
                updateResult(currentGame, currentGame.coupArray.size - 1)
                updateTotalResult(currentGame, currentGame.coupArray.size - 1)
                updateResultTable(resultTable, currentGame, ADD)
                updateResultTable(totalResultTable, currentGame, ADD)
                filterGame(gameArray, currentGame.coupArray)
                updateScoreTable(scoreTable, currentGame, ADD)
                updatePredict(predictTable)
                if (rowIndex < 2) rowIndex++
                else {
                    rowIndex = 0
                    colIndex++
                }
                showTotal(playerLastNine)
                showTotal(playerTotalText)
                showTotal(bankerLastNine)
                showTotal(bankerTotalText)
                coupIndex++
                coupIndexText.text = "$coupIndex"
                updateResultLabel(resultLabel, currentGame)
                updateTotalResultLabel(resultLabel, currentGame)
            }
            else {
                val toast = Toast.makeText(root.context, "Cannot add more", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        val backButton: FloatingActionButton = root.findViewById(R.id.backButton)
        backButton.setOnClickListener { view ->
            if (currentGame.coupArray.size > 0) {
                if (rowIndex > 0) rowIndex--
                else {
                    rowIndex = 2
                    colIndex--
                }
                currentGame.coupArray.removeAt(currentGame.coupArray.size - 1)
                filterGame(gameArray, currentGame.coupArray)
                showTotal(playerLastNine)
                showTotal(playerTotalText)
                showTotal(bankerLastNine)
                showTotal(bankerTotalText)
                updateScoreTable(scoreTable, currentGame, REMOVE)
                updateResultTable(resultTable, currentGame, REMOVE)
                updateResultTable(totalResultTable, currentGame, REMOVE)
                updatePredict(predictTable)
                if (currentGame.resultArray.size > 0) {
                    currentGame.lastNineResult -= currentGame.resultArray[currentGame.resultArray.size - 1]
                    currentGame.resultArray.removeAt(currentGame.resultArray.size - 1)
                }
                else currentGame.lastNineResult = 0

                if (currentGame.totalResultArray.size > 0) {
                    currentGame.totalResult -= currentGame.totalResultArray[currentGame.totalResultArray.size - 1]
                    currentGame.totalResultArray.removeAt(currentGame.totalResultArray.size - 1)
                }
                else currentGame.totalResult = 0

                coupIndex--
                coupIndexText.text = "$coupIndex"
                updateResultLabel(resultLabel, currentGame)
                updateTotalResultLabel(resultLabel, currentGame)
            }
        }

        val clearButton: FloatingActionButton = root.findViewById(R.id.newButton)
        clearButton.setOnClickListener { view ->
            currentGame = Game(ArrayList<Int>())
            //currentGame.coupArray.clear()
            filteredList.clear()
            filteredListLastNine.clear()
            nextCoupList.clear()
            nextCoupListLastNine.clear()
            clearTable(scoreTable)
            clearTable(resultTable)
            clearTable(totalResultTable)
            clearTable(predictTable)
            playerTotalText.text = ""
            playerLastNine.text = ""
            bankerTotalText.text = ""
            bankerLastNine.text = ""
            rowIndex = 0
            colIndex = 0
            //currentGame.lastNineResult = 0
            //currentGame.resultArray.clear()
            coupIndex = 0

            coupIndexText.text = ""
            resultLabel.text = ""
            resultLabel.text = ""
        }

        val openButton: FloatingActionButton = root.findViewById(R.id.openButton)
        openButton.setOnClickListener { view ->
            /*val builder = AlertDialog.Builder(root.context)
            val inflater = layoutInflater
            builder.setTitle("Open game")
            val dialogLayout = inflater.inflate(R.layout.open_game_dialog2, null)
            val fileListSpinner = dialogLayout.findViewById<Spinner>(R.id.fileListSpinner)
            val dialogOpenButton  = dialogLayout.findViewById<Button>(R.id.dialogOpenButton2)

            val recyclerView = dialogLayout.findViewById<RecyclerView>(R.id.recyclerView)
            linearLayoutManager = LinearLayoutManager(root.context)
            recyclerView.layoutManager = linearLayoutManager
            var game = Game()
            adapter = RecyclerAdapter(fileList, root.context, {name:String -> updateFileName(name)})

            recyclerView.adapter = adapter
            builder.setView(dialogLayout)




            var adapter = ArrayAdapter(root.context,android.R.layout.simple_spinner_item, fileList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            fileListSpinner.setAdapter(adapter)

            var dialog = builder.show()


            fileListSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {

                    try {
                        val fileInputStream = context!!.openFileInput(fileListSpinner.selectedItem.toString())
                        val objectInputStream = ObjectInputStream(fileInputStream)
                        game = objectInputStream.readObject() as Game
                        objectInputStream.close()
                        fileInputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: ClassNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }

            dialogOpenButton.setOnClickListener {view ->
                try {
                    val fileInputStream = context!!.openFileInput(fileName)
                    val objectInputStream = ObjectInputStream(fileInputStream)
                    game = objectInputStream.readObject() as Game
                    objectInputStream.close()
                    fileInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
                filteredList.clear()
                filteredListLastNine.clear()
                nextCoupList.clear()
                nextCoupListLastNine.clear()
                clearTable(scoreTable)
                clearTable(resultTable)
                clearTable(predictTable)
                playerTotalText.text = ""
                playerLastNine.text = ""
                bankerTotalText.text = ""
                bankerLastNine.text = ""
                rowIndex = 0
                colIndex = 0
                coupIndex = 59

                currentGame = Game(populateResults(gameArray, game))
                OpenGame(scoreTable, resultTable, totalResultTable, gameArray, currentGame)
                coupIndexText.text = "60"
                updateResultLabel(resultLabel, currentGame)
                updateTotalResultLabel(totalResultLabel, currentGame)
                dialog.dismiss()
            }*/


            var openGameIndex = 0
            val builder = AlertDialog.Builder(root.context)
            val inflater = layoutInflater
            builder.setTitle("Open game")
            val dialogLayout = inflater.inflate(R.layout.open_game_dialog, null)
            val editText = dialogLayout.findViewById<EditText>(R.id.editText)
            val dialogOpenButton = dialogLayout.findViewById<Button>(R.id.dialogOpenButton)
            builder.setView(dialogLayout)

            var dialog = builder.show()
            editText.requestFocus()
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

            dialogOpenButton.setOnClickListener { view ->
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
                        filteredList.clear()
                        filteredListLastNine.clear()
                        nextCoupList.clear()
                        nextCoupListLastNine.clear()
                        clearTable(scoreTable)
                        clearTable(resultTable)
                        clearTable(predictTable)
                        playerTotalText.text = ""
                        playerLastNine.text = ""
                        bankerTotalText.text = ""
                        bankerLastNine.text = ""
                        rowIndex = 0
                        colIndex = 0
                        coupIndex = 59
                        var gameArrayWithoutOpened = ArrayList(gameArray)
                        var openedGame = gameArrayWithoutOpened.removeAt(openGameIndex)
                        currentGame = Game(populateResults(gameArrayWithoutOpened, openedGame))
                        OpenGame(scoreTable, resultTable, totalResultTable, currentGame)
                        coupIndexText.text = "60"
                        updateResultLabel(resultLabel, currentGame)
                        updateTotalResultLabel(resultLabel, currentGame)
                        dialog.dismiss()
                    }
                }

            }
        }


            /*filteredList.clear()
            filteredListLastNine.clear()
            nextCoupList.clear()
            nextCoupListLastNine.clear()
            clearTable(scoreTable)
            clearTable(resultTable)
            clearTable(predictTable)
            playerTotalText.text = ""
            bankerTotalText.text = ""
            rowIndex = 0
            colIndex = 0
            coupIndex = 59
            var newGame = OpenGame(scoreTable, resultTable, gameArray, gameArray[openGameIndex])
            currentGame = Game(newGame)
            coupIndexText.text = "Hand thứ 60"
            resultLabel.text = "Kết quả: ${currentGame.lastNineResult}"*/
        //}

        val saveButton: FloatingActionButton = root.findViewById(R.id.saveButton)
        saveButton.setOnClickListener { view ->
            var date = Date()
            val formatter = SimpleDateFormat("MMM_dd_yyyy HH_mm_ss")
            val dateString: String = formatter.format(date)
            currentGame.saveToFile(root.context, dateString)
            if (!fileList.contains(dateString)) fileList.add(dateString)
            setArrayPrefs("fileList",fileList,root.context)
        }

        return root
    }

    private fun drawBorder(view: View) {
        var border = GradientDrawable()
        border.setColor(Color.parseColor("#FFFFFF"))
        border.setStroke(2,Color.parseColor("#000000"))
        view.background = border
    }

    private fun drawTable(table: TableLayout, shape: Int) {

        var i = 0
        while (i < table.childCount) {
            var row = table.getChildAt(i) as TableRow
            var j = 0
            while (j < 20) {
                val gd = GradientDrawable()
                gd.shape = shape
                gd.setSize(24,24)
                gd.setVisible(false,true)

                var image = ImageView(this.context)
                image.setImageDrawable(gd)
                image.setPadding(8,8,8,8)

                var border = GradientDrawable()
                border.setColor(Color.parseColor("#FFFFFF"))
                border.setStroke(1,Color.parseColor("#000000"))
                image.background = border

                row.addView(image)
                j++
            }
            i++
        }
        table.isStretchAllColumns = true
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
                        gd.setColor(resources.getColor(R.color.PLAYER,null))
                        gd.setVisible(true,true)
                    }
                    1 -> {
                        gd.setColor(resources.getColor(R.color.BANKER,null))
                        gd.setVisible(true,true)
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
                            gd.setColor(resources.getColor(R.color.PLAYER,null))
                            gd.setVisible(true,true)
                        }
                        1 -> {
                            gd.setColor(resources.getColor(R.color.BANKER,null))
                            gd.setVisible(true,true)
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

    private fun updateResultTable(table: TableLayout, game: Game, updateType: Int) {
        var resultArray = ArrayList<Int>()
        when (table.id) {
            resultTable.id -> resultArray = game.resultArray
            resultTable.id -> resultArray = game.totalResultArray
        }
        when (updateType) {
            ADD -> {
                var row = table.getChildAt(rowIndex) as TableRow
                var image = row.getChildAt(colIndex) as ImageView
                var gd = image.drawable as GradientDrawable
                when (resultArray[resultArray.size - 1]) {
                    PREDICT_RIGHT -> {
                        gd.setColor(resources.getColor(R.color.RIGHT,null))
                        gd.setVisible(true, true)
                        if (table.id == resultTable.id) {
                            if (kotlin.math.abs(game.playerResultArray[game.playerResultArray.size - 1] - game.bankerResultArray[game.bankerResultArray.size - 1]) > 4) {
                                var imageBackground = image.background as GradientDrawable
                                imageBackground.setColor(
                                    resources.getColor(
                                        R.color.MAJORITY,
                                        null
                                    )
                                )
                            }
                        }
                    }
                    PREDICT_WRONG -> {
                        gd.setColor(resources.getColor(R.color.WRONG, null))
                        gd.setVisible(true, true)
                        if (table.id == resultTable.id) {
                            if (kotlin.math.abs(game.playerResultArray[game.playerResultArray.size - 1] - game.bankerResultArray[game.bankerResultArray.size - 1]) > 3) {
                                var imageBackground = image.background as GradientDrawable
                                imageBackground.setColor(
                                    resources.getColor(
                                        R.color.MAJORITY,
                                        null
                                    )
                                )
                            }
                        }
                    }
                    NO_PREDICT -> {
                        gd.color = null
                        gd.setVisible(false, true)
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

            }
        }

    }

    private fun updatePredict(table: TableLayout) {
        clearTable(table)
        var i = 0
        var predictRowIndex = 0
        var predictColIndex = 0
        while (i < nextCoupList.size) {
            var row = table.getChildAt(predictRowIndex) as TableRow
            var image = row.getChildAt(predictColIndex) as ImageView
            var gd = image.drawable as GradientDrawable
            when (nextCoupList[i]) {
                0 -> {
                    gd.setColor(resources.getColor(R.color.PLAYER,null))
                    gd.setVisible(true,true)
                }
                1 -> {
                    gd.setColor(resources.getColor(R.color.BANKER,null))
                    gd.setVisible(true,true)
                }
            }
            predictRowIndex ++
            if (predictRowIndex > 2) {
                predictRowIndex = 0
                predictColIndex ++
            }
            i++
        }
    }

    private fun removeLast(table: TableLayout) {
        var row = table.getChildAt(rowIndex) as TableRow
        var image = row.getChildAt(colIndex) as ImageView
        var gd = image.drawable as GradientDrawable
        gd.color = null
        gd.setVisible(false,true)
        var imageBackground = image.background as GradientDrawable
        imageBackground.setColor(Color.parseColor("#FFFFFF"))
    }

    private fun clearTable(table: TableLayout) {
        var i = 0
        while (i < table.childCount) {
            var row = table.getChildAt(i) as TableRow
            var j = 0
            while (j < row.childCount) {
                var image = row.getChildAt(j) as ImageView
                var gd = image.drawable as GradientDrawable
                gd.color = null
                gd.setVisible(false, true)
                var imageBackground = image.background as GradientDrawable
                imageBackground.setColor(Color.parseColor("#FFFFFF"))
                //image.setBackgroundColor(Color.parseColor("#FFFFFF"))
                j++
            }
            i++
        }
    }

    private fun showTotal(textView: TextView) {
        when (textView.id) {
            R.id.playerLastNine -> {
                var playerCountLastNine = nextCoupListLastNine.filter{it.equals(0)}.size
                textView.text = playerCountLastNine.toString()
            }
            R.id.playerTotal -> {
                var playerCountTotal = nextCoupList.filter{it.equals(0)}.size
                textView.text = playerCountTotal.toString()
            }
            R.id.bankerLastNine -> {
                var bankerCountLastNine = nextCoupListLastNine.filter{it.equals(1)}.size
                textView.text = bankerCountLastNine.toString()
            }
            R.id.bankerTotal -> {
                var bankerCountTotal = nextCoupList.filter{it.equals(1)}.size
                textView.text = bankerCountTotal.toString()
            }
        }
    }

    private fun filterGame(gameArray: ArrayList<Game>, patternList: ArrayList<Int>) {

        if (patternList.size > 4) {
            var numOfCoupToFilter = 5
            var firstIndex = patternList.size - numOfCoupToFilter
            var filterPattern = patternList.subList(firstIndex, firstIndex + 5)
            filteredList = gameArray.filter{
                it.coupArray.subList(firstIndex,firstIndex + numOfCoupToFilter).equals(filterPattern)
            } as ArrayList
            while (filteredList == null || filteredList.size < 9) {
                numOfCoupToFilter--
                firstIndex = patternList.size - numOfCoupToFilter
                filteredList = gameArray.filter{
                    it.coupArray.subList(firstIndex,firstIndex + numOfCoupToFilter).equals(filterPattern)
                } as ArrayList
            }
            filteredListLastNine = ArrayList(filteredList.subList(filteredList.size - 9, filteredList.size))

            nextCoupList.clear()
            for (matchingGame in filteredList) {
                var nextCoupIndex = firstIndex + numOfCoupToFilter
                if (nextCoupIndex < matchingGame.coupArray.size)
                    nextCoupList.add(matchingGame.coupArray.get(firstIndex + numOfCoupToFilter))
            }
            if (nextCoupList.size >= 9)
                nextCoupListLastNine = ArrayList(nextCoupList.subList(nextCoupList.size - 9, nextCoupList.size))
            else nextCoupListLastNine.clear()
        }
    }

    private fun OpenGame(scoreTable: TableLayout, resultTable: TableLayout, totalResultTable: TableLayout, game: Game){
        updateScoreTable(scoreTable, game, REDRAW)
        drawResultTable(resultTable, game)
        drawResultTable(totalResultTable, game)
    }

    // Draw a game result array to the table UI
    private fun drawResultTable(table: TableLayout, game: Game){

        var resultArray = ArrayList<Int>()
        when (table.id) {
            resultTable.id -> resultArray = game.resultArray
            resultTable.id -> resultArray = game.totalResultArray
        }
        rowIndex = 0
        colIndex = 0

        var i = 0
        while (i < resultArray.size) {

            var row = table.getChildAt(rowIndex) as TableRow
            var image = row.getChildAt(colIndex) as ImageView
            var gd = image.drawable as GradientDrawable
            when (resultArray[i]) {
                PREDICT_RIGHT -> {
                    gd.setColor(resources.getColor(R.color.RIGHT,null))
                    gd.setVisible(true, true)
                    if (table.id == resultTable.id) {
                        if (kotlin.math.abs(game.playerResultArray[i] - game.bankerResultArray[i]) > 3) {
                            var imageBackground = image.background as GradientDrawable
                            imageBackground.setColor(
                                resources.getColor(
                                    R.color.MAJORITY,
                                    null
                                )
                            )
                        }
                    }
                }
                PREDICT_WRONG -> {
                    gd.setColor(resources.getColor(R.color.WRONG,null))
                    gd.setVisible(true, true)
                    if (table.id == resultTable.id) {
                        if (kotlin.math.abs(game.playerResultArray[i] - game.bankerResultArray[i]) > 3) {
                            var imageBackground = image.background as GradientDrawable
                            imageBackground.setColor(
                                resources.getColor(
                                    R.color.MAJORITY,
                                    null
                                )
                            )
                        }
                    }
                }
                NO_PREDICT -> {
                    gd.color = null
                    gd.setVisible(false, true)
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
    private fun populateResults(gameArray: ArrayList<Game>, game: Game):Game{
        if (game.resultArray.size == 0) {
            var i = 0
            rowIndex = 0
            colIndex = 0
            while (i < game.coupArray.size) {
                var patternList = ArrayList(game.coupArray.subList(0, i))
                filterGame(gameArray, patternList)
                updateResult(game, i)
                updateTotalResult(game, i)

                i++
                if (rowIndex < 2) rowIndex++
                else {
                    rowIndex = 0
                    colIndex++
                }
            }
        }
        return game
    }

    // Update the result array and total result of a game after a new coup has been added
    private fun updateResult(game: Game, currentIndex: Int) {
        if (nextCoupListLastNine.size == 0) {
            game.resultArray.add(NO_PREDICT)
            game.playerResultArray.add(0)
            game.bankerResultArray.add(0)
        }
        else {
            var playerCount = nextCoupListLastNine.filter{it.equals(0)}.size
            var bankerCount = nextCoupListLastNine.filter{it.equals(1)}.size
            var filterPredict = 0
            if (playerCount > bankerCount) filterPredict = 0
            else filterPredict = 1

            // actual added coup match prediction
            if (filterPredict == game.coupArray[currentIndex]) {
                game.resultArray.add(PREDICT_RIGHT)
                game.lastNineResult += game.resultArray[game.resultArray.size - 1]
            }
            // actual added coup doesn't match prediction
            else {
                game.resultArray.add(PREDICT_WRONG)
                game.lastNineResult += game.resultArray[game.resultArray.size - 1]
            }
            game.playerResultArray.add(playerCount)
            game.bankerResultArray.add(bankerCount)
        }
    }

    private fun updateTotalResult(game: Game, currentIndex: Int) {
        if (nextCoupList.size == 0) {
            game.totalResultArray.add(NO_PREDICT)
        }
        else {
            var playerCount = nextCoupList.filter{it.equals(0)}.size
            var bankerCount = nextCoupList.filter{it.equals(1)}.size

            if (playerCount == bankerCount) game.totalResultArray.add(NO_PREDICT)
            else {
                var filterPredict = 0
                if (playerCount > bankerCount) filterPredict = 0
                else filterPredict = 1

                // actual added coup match prediction
                if (filterPredict == game.coupArray[currentIndex]) {
                    game.totalResultArray.add(PREDICT_RIGHT)
                    game.totalResult += game.totalResultArray[game.totalResultArray.size - 1]
                }
                // actual added coup doesn't match prediction
                else {
                    game.totalResultArray.add(PREDICT_WRONG)
                    game.totalResult += game.totalResultArray[game.totalResultArray.size - 1]
                }
            }
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

    private fun removeArrayPref(arrayName: String, mContext: Context, position: Int){
        val prefs = mContext.getSharedPreferences("preferencename", 0)
        prefs.edit().remove(arrayName + "_" + position)
        prefs.edit().apply()
    }

    private fun updateFileName(name: String) {
        fileName = name
    }

    private fun deleteSavedGame() {

    }

    private fun updateResultLabel(textView: TextView, game: Game) {
        textView.text = "${game.lastNineResult}"
        if (game.lastNineResult > 0) textView.setTextColor(Color.RED)
        else textView.setTextColor(Color.BLACK)
    }

    private fun updateTotalResultLabel(textView: TextView, game: Game) {
        textView.text = "${game.totalResult}"
        if (game.totalResult > 0) textView.setTextColor(Color.RED)
        else textView.setTextColor(Color.BLACK)
    }

}
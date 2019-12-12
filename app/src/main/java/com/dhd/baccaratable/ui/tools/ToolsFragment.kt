package com.dhd.baccaratable.ui.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dhd.baccaratable.R
import android.graphics.drawable.GradientDrawable

import android.graphics.Color
import android.widget.*
import com.dhd.baccaratable.Game
import com.dhd.baccaratable.MainActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.collections.ArrayList


class ToolsFragment : Fragment() {

    private lateinit var toolsViewModel: ToolsViewModel
    var filterList = ArrayList<Game>()
    var tableRowIndex = 0
    var tableColumnIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_tools, container, false)

        val gameArray = (activity as MainActivity).gameArray
        var table = root.findViewById<TableLayout>(R.id.table)
        var textView2 = root.findViewById<TextView>(R.id.textView2)
        var textView3 = root.findViewById<TextView>(R.id.textView3)
        var chart = root.findViewById<PieChart>(R.id.chart)
        StyleChart(chart)

        var tableRow1 = TableRow(root.context)
        var tableRow2 = TableRow(root.context)
        var tableRow3 = TableRow(root.context)
        table.addView(tableRow1)
        table.addView(tableRow2)
        table.addView(tableRow3)

        var currentGame = Game(ArrayList<Int>())

        val bankerButton: FloatingActionButton = root.findViewById(R.id.bankerButton)
        bankerButton.setOnClickListener { view ->
            currentGame.coupArray.add(1)
            UpdateTable(currentGame, table)
            UpdateGroupString(currentGame)
            textView2.text = "Pattern: ${currentGame.groupThreeString}"
            textView3.text = "Total match: ${MatchCount(currentGame, gameArray)}"
            UpdateChart(currentGame,chart)
        }

        val playerButton: FloatingActionButton = root.findViewById(R.id.playerButton)
        playerButton.setOnClickListener { view ->
            currentGame.coupArray.add(0)
            UpdateTable(currentGame, table)
            UpdateGroupString(currentGame)
            textView2.text = "Pattern: ${currentGame.groupThreeString}"
            textView3.text = "Total match: ${MatchCount(currentGame, gameArray)}"
            UpdateChart(currentGame,chart)
        }

        val clearButton: FloatingActionButton = root.findViewById(R.id.newButton)
        clearButton.setOnClickListener { view ->
            currentGame.coupArray.clear()
            currentGame.groupThreeString = ""
            tableRow1.removeAllViews()
            tableRow2.removeAllViews()
            tableRow3.removeAllViews()
            UpdateGroupString(currentGame)
            textView2.text = "Pattern: ${currentGame.groupThreeString}"
            textView3.text = "Total match: ${MatchCount(currentGame, gameArray)}"
            chart.clear()
        }

        val backButton: FloatingActionButton = root.findViewById(R.id.backButton)
        backButton.setOnClickListener { view ->
            if (tableRowIndex >= 0 && tableColumnIndex >= 0) {
                currentGame.coupArray.removeAt(currentGame.coupArray.size - 1)
                if (currentGame.groupThreeString.length > 2) {
                    currentGame.groupThreeString.removeRange(
                        currentGame.groupThreeString.length - 3,
                        currentGame.groupThreeString.length - 2
                    )
                }

                var row = table.getChildAt(tableRowIndex) as TableRow
                row.removeViewAt(tableColumnIndex)

                UpdateGroupString(currentGame)
                textView2.text = "Pattern: ${currentGame.groupThreeString}"
                textView3.text = "Total match: ${MatchCount(currentGame, gameArray)}"
                UpdateChart(currentGame, chart)

                if (tableRowIndex > 0) {
                    tableRowIndex -= 1
                }
                else {
                    tableRowIndex = 2
                    tableColumnIndex -= 1
                }
            }
        }





        //draw scoreboard table of a coup
        /*val coupIndex = 0
        for (row in gameArray[coupIndex].rows) {
            var tableRow = TableRow(root.context)

            var j = 0
            while (j<row.size) {
                val gd = GradientDrawable()
                gd.shape = GradientDrawable.OVAL
                when (row[j]) {
                    0 -> gd.setColor(Color.parseColor("#48b3ff"))
                    1 -> gd.setColor(Color.parseColor("#FF0000"))
                    9 -> gd.setVisible(false,true)
                }

                gd.setSize(24,24)

                var image = ImageView(root.context)

                image.setImageDrawable(gd)
                image.setPadding(8,8,8,8)
                var border = GradientDrawable()
                border.setColor(Color.parseColor("#FFFFFF"))
                border.setStroke(1,Color.parseColor("#000000"))
                image.background = border
                tableRow.addView(image)
                j++
            }
            table.addView(tableRow)

        }
        table.invalidate()*/


        //var textView2 = root.findViewById<TextView>(R.id.textView2)
        //textView2.text = gameArray[coupIndex].groupThreeString

        return root
    }

    fun UpdateTable(game:Game, table: TableLayout) : Unit {
        var i = 0
        var rowIndex = (game.coupArray.size - 1) % 3



        val gd = GradientDrawable()
        gd.shape = GradientDrawable.OVAL
        when (game.coupArray[game.coupArray.size-1]) {
            0 -> gd.setColor(Color.parseColor("#0000FF"))
            1 -> gd.setColor(Color.parseColor("#FF0000"))
        }

        gd.setSize(24,24)

        var image = ImageView(this.context)

        image.setImageDrawable(gd)
        image.setPadding(8,8,8,8)
        /*var border = GradientDrawable()
        border.setColor(Color.parseColor("#FFFFFF"))
        border.setStroke(1,Color.parseColor("#000000"))
        image.background = border*/
        var row = table.getChildAt(rowIndex) as TableRow
        row.addView(image)
        tableRowIndex = rowIndex
        tableColumnIndex = row.childCount - 1
        table.invalidate()
    }

    fun UpdateGroupString(game:Game) : Unit {
        var i = 0
        var groupString = StringBuilder()
        while (i + 2 < game.coupArray.size) {
            when (game.coupArray.subList(i, i + 3).toString()) {
                "[0, 0, 0]" -> groupString.append("1,")
                "[0, 0, 1]" -> groupString.append("2,")
                "[0, 1, 0]" -> groupString.append("3,")
                "[0, 1, 1]" -> groupString.append("4,")
                "[1, 0, 0]" -> groupString.append("5,")
                "[1, 0, 1]" -> groupString.append("6,")
                "[1, 1, 0]" -> groupString.append("7,")
                "[1, 1, 1]" -> groupString.append("8,")
            }
            i += 1
        }
        game.groupThreeString = groupString.trim(',').toString()
    }

    fun MatchCount(game:Game, gameArray:ArrayList<Game>) :String {
        filterList = gameArray.filter{it.groupThreeString.startsWith(game.groupThreeString)} as ArrayList
        return filterList.size.toString()

    }

    fun UpdateChart(game:Game, chart: PieChart):Unit {

        var entries = ArrayList<PieEntry>()
        var set: PieDataSet
        var data: PieData

        var valueMap = filterList.groupingBy{it.groupThreeString.substring(game.groupThreeString.length).get(1)}.eachCount()
        valueMap = valueMap.toSortedMap()
        for (element in valueMap) {
            var elementFirstCoup = ""
            when (element.key) {
                '1' -> elementFirstCoup = "P"
                '2' -> elementFirstCoup = "B"
                '3' -> elementFirstCoup = "P"
                '4' -> elementFirstCoup = "B"
                '5' -> elementFirstCoup = "P"
                '6' -> elementFirstCoup = "B"
                '7' -> elementFirstCoup = "P"
                '8' -> elementFirstCoup = "B"
            }
            entries.add(PieEntry(element.value.toFloat(),"Next: $elementFirstCoup"))

        }
        set = PieDataSet(entries,"Next Pattern")
        data = PieData(set)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(17f)
        chart.data = data
        val colors = ArrayList<Int>()
        /*for (c in ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS)
            colors.add(c)*/
        if (game.groupThreeString.length > 0) {
            if (entries.size == 1) {
                colors.clear()
                when (entries[0].label.get(entries[0].label.length - 1)) {
                    'P' -> colors.add(resources.getColor(R.color.PLAYER,null))
                    'B' -> colors.add(resources.getColor(R.color.BANKER,null))
                }
            }
            else {
                colors.add(resources.getColor(R.color.PLAYER,null))
                colors.add(resources.getColor(R.color.BANKER,null))
            }
        }
        else colors.add(resources.getColor(R.color.WHITE,null))
        /*colors.add(getResources().getColor(R.color.PLAYER,null))
        colors.add(getResources().getColor(R.color.BANKER,null))*/
        set.colors = colors
        //chart.animateXY(100, 100)
        chart.centerText = "Next Pattern"
        chart.invalidate()
    }

    fun StyleChart(chart:PieChart):PieChart {
        chart.setUsePercentValues(false)
        chart.setExtraOffsets(5f, 10f, 5f, 5f)

        chart.description.isEnabled = false


        chart.setHoleColor(Color.WHITE)

        chart.setEntryLabelColor(Color.WHITE)
        chart.setEntryLabelTextSize(16f)

        chart.setCenterTextColor(Color.BLACK)
        chart.setCenterTextSize(20f)

        val l = chart.legend
        l.isEnabled = false
        /*l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f*/
        return chart
    }


}
package com.dhd.baccaratable.ui.boardanalysis

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dhd.baccaratable.MainActivity
import com.dhd.baccaratable.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.*
import kotlin.collections.ArrayList

class BoardAnalysisFragment : Fragment() {

    //private lateinit var galleryViewModel: GalleryViewModel



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //galleryViewModel =
        //    ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_boardanalysis, container, false)
        /*val textView: TextView = root.findViewById(R.id.text_gallery)
        galleryViewModel.text.observe(this, Observer {
            textView.text = it
        })*/
        val gameArray = (activity as MainActivity).gameArray

        var maxSameArray = ArrayList<Int>()

        var oneSameCountArray = ArrayList<Int>()
        var maxContOneSameArray = ArrayList<Int>()
        var oneEmptyCountArray = ArrayList<Int>()
        var maxContOneEmptyArray = ArrayList<Int>()

        var twoSameCountArray = ArrayList<Int>()
        var maxContTwoSameArray = ArrayList<Int>()
        var twoEmptyCountArray = ArrayList<Int>()
        var maxContTwoEmptyArray = ArrayList<Int>()

        var threeSameCountArray = ArrayList<Int>()
        var maxContThreeSameArray = ArrayList<Int>()
        var threeEmptyCountArray = ArrayList<Int>()
        var maxContThreeEmptyArray = ArrayList<Int>()

        var fourSameCountArray = ArrayList<Int>()
        var maxContFourSameArray = ArrayList<Int>()
        var fourEmptyCountArray = ArrayList<Int>()
        var maxContFourEmptyArray = ArrayList<Int>()

        for (game in gameArray) {
            maxSameArray.add(game.maxSame)

            oneSameCountArray.add(game.oneSameCount)
            maxContOneSameArray.add(game.maxContOneSame)
            oneEmptyCountArray.add(game.oneEmptyCount)
            maxContOneEmptyArray.add(game.maxContOneEmpty)

            twoSameCountArray.add(game.twoSameCount)
            maxContTwoSameArray.add(game.maxContTwoSame)
            twoEmptyCountArray.add(game.twoEmptyCount)
            maxContTwoEmptyArray.add(game.maxContTwoEmpty)

            threeSameCountArray.add(game.threeSameCount)
            maxContThreeSameArray.add(game.maxContThreeSame)
            threeEmptyCountArray.add(game.threeEmptyCount)
            maxContThreeEmptyArray.add(game.maxContThreeEmpty)

            fourSameCountArray.add(game.fourSameCount)
            maxContFourSameArray.add(game.maxContFourSame)
            fourEmptyCountArray.add(game.fourEmptyCount)
            maxContFourEmptyArray.add(game.maxContFourEmpty)
        }

        var maxSameMap = maxSameArray.groupingBy{it}.eachCount().toSortedMap()

        var oneSameCountMap = oneSameCountArray.groupingBy{it}.eachCount().toSortedMap()
        var maxContOneSameMap = maxContOneSameArray.groupingBy{it}.eachCount().toSortedMap()
        var oneEmptyCountMap = oneEmptyCountArray.groupingBy{it}.eachCount().toSortedMap()
        var maxContOneEmptyMap = maxContOneEmptyArray.groupingBy{it}.eachCount().toSortedMap()

        var twoSameCountMap = twoSameCountArray.groupingBy{it}.eachCount().toSortedMap()
        var maxContTwoSameMap = maxContTwoSameArray.groupingBy{it}.eachCount().toSortedMap()
        var twoEmptyCountMap = twoEmptyCountArray.groupingBy{it}.eachCount().toSortedMap()
        var maxContTwoEmptyMap = maxContTwoEmptyArray.groupingBy{it}.eachCount().toSortedMap()

        var threeSameCountMap = threeSameCountArray.groupingBy{it}.eachCount().toSortedMap()
        var maxContThreeSameMap = maxContThreeSameArray.groupingBy{it}.eachCount().toSortedMap()
        var threeEmptyCountMap = threeEmptyCountArray.groupingBy{it}.eachCount().toSortedMap()
        var maxContThreeEmptyMap = maxContThreeEmptyArray.groupingBy{it}.eachCount().toSortedMap()

        var fourSameCountMap = fourSameCountArray.groupingBy{it}.eachCount().toSortedMap()
        var maxContFourSameMap = maxContFourSameArray.groupingBy{it}.eachCount().toSortedMap()
        var fourEmptyCountMap = fourEmptyCountArray.groupingBy{it}.eachCount().toSortedMap()
        var maxContFourEmptyMap = maxContFourEmptyArray.groupingBy{it}.eachCount().toSortedMap()

        val chart = StyleChart(root.findViewById<PieChart>(R.id.chart))
        val spinner: Spinner = root.findViewById(R.id.spinner)
        val chartList = arrayListOf("Dính lớn nhất",
            "Tổng số dính 1", "Dính 1 liên tiếp lớn nhất", "Tổng số trống 1", "Trống 1 liên tiếp lớn nhất",
            "Tổng số dính 2", "Dính 2 liên tiếp lớn nhất", "Tổng số trống 2", "Trống 2 liên tiếp lớn nhất",
            "Tổng số dính 3", "Dính 3 liên tiếp lớn nhất", "Tổng số trống 3", "Trống 3 liên tiếp lớn nhất",
            "Tổng số dính 4", "Dính 4 liên tiếp lớn nhất", "Tổng số trống 4", "Trống 4 liên tiếp lớn nhất")

        var adapter = ArrayAdapter(root.context,android.R.layout.simple_spinner_item,chartList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when(spinner.selectedItem) {
                    "Dính lớn nhất" -> {
                        PopulateChart(maxSameMap,chart,spinner)
                    }
                    "Tổng số dính 1" -> {
                        PopulateChart(oneSameCountMap,chart,spinner)
                    }
                    "Dính 1 liên tiếp lớn nhất" -> {
                        PopulateChart(maxContOneSameMap,chart,spinner)
                    }
                    "Tổng số trống 1" -> {
                        PopulateChart(oneEmptyCountMap,chart,spinner)
                    }
                    "Trống 1 liên tiếp lớn nhất" -> {
                        PopulateChart(maxContOneEmptyMap,chart,spinner)
                    }
                    "Tổng số dính 2" -> {
                        PopulateChart(twoSameCountMap,chart,spinner)
                    }
                    "Dính 2 liên tiếp lớn nhất" -> {
                        PopulateChart(maxContTwoSameMap,chart,spinner)
                    }
                    "Tổng số trống 2" -> {
                        PopulateChart(twoEmptyCountMap,chart,spinner)
                    }
                    "Trống 2 liên tiếp lớn nhất" -> {
                        PopulateChart(maxContTwoEmptyMap,chart,spinner)
                    }
                    "Tổng số dính 3" -> {
                        PopulateChart(threeSameCountMap,chart,spinner)
                    }
                    "Dính 3 liên tiếp lớn nhất" -> {
                        PopulateChart(maxContThreeSameMap,chart,spinner)
                    }
                    "Tổng số trống 3" -> {
                        PopulateChart(threeEmptyCountMap,chart,spinner)
                    }
                    "Trống 3 liên tiếp lớn nhất" -> {
                        PopulateChart(maxContThreeEmptyMap,chart,spinner)
                    }
                    "Tổng số dính 4" -> {
                        PopulateChart(fourSameCountMap,chart,spinner)
                    }
                    "Dính 4 liên tiếp lớn nhất" -> {
                        PopulateChart(maxContFourSameMap,chart,spinner)
                    }
                    "Tổng số trống 4" -> {
                        PopulateChart(fourEmptyCountMap,chart,spinner)
                    }
                    "Trống 4 liên tiếp lớn nhất" -> {
                        PopulateChart(maxContFourEmptyMap,chart,spinner)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }
        return root
    }

    fun PopulateChart(valueMap: SortedMap<Int, Int>, chart: PieChart, spinner:Spinner):Unit {
        var entries = ArrayList<PieEntry>()
        var set: PieDataSet
        var data: PieData
        for (element in valueMap) {
            entries.add(PieEntry(element.value.toFloat(),"${element.key}"))
        }
        set = PieDataSet(entries,spinner.selectedItem.toString())
        data = PieData(set)
        data.setValueTextSize(16f)
        chart.data = data
        val colors = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS)
            colors.add(c)
        set.colors = colors
        chart.animateXY(1000, 1000)
        chart.centerText = spinner.selectedItem.toString()
        chart.invalidate()
    }

    fun StyleChart(chart:PieChart):PieChart {
        chart.setUsePercentValues(false)
        chart.setExtraOffsets(5f, 10f, 5f, 5f)

        chart.description.isEnabled = false


        chart.setHoleColor(Color.WHITE)

        chart.setEntryLabelColor(Color.BLACK)
        chart.setEntryLabelTextSize(15f)

        chart.setCenterTextColor(Color.BLACK)
        chart.setCenterTextSize(20f)

        val l = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f
        return chart
    }
}
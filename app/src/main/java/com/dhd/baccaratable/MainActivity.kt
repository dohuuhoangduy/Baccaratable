package com.dhd.baccaratable

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    var gameArray = ArrayList<Game>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        InitGame()

        /*val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_boardanalysis, R.id.nav_groupanalysis, R.id.nav_newgame2,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun InitGame():Unit {
        try {
            var fileReader = BufferedReader(InputStreamReader(assets.open("DataPlay1000.csv")))
            var line = fileReader.readLine()
            while (line!= null) {
                val tokens = line.split(",")
                var game = NewGame(tokens.map{it.toInt()} as ArrayList)
                CalculateSameInGame(game)
                CalculateEmptyInGame(game)
                gameArray.add(game)
                line = fileReader.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun NewGame (coupArray : ArrayList<Int>):Game {
        var game = Game(coupArray)

        // populate rows
        var i = 0
        var rowId = 0
        var colId = 0
        var groupThreeIndex = 0
        var groupString = StringBuilder()
        var isBottom = false
        while (i < game.coupArray.size) {
            //check if current row is at bottom
            var currentRow = game.rows[rowId]
            if (rowId == 4) isBottom = true
            else {
                var nextRow = game.rows[rowId+1]
                if (nextRow.size > colId) isBottom = true
                else {
                    if (nextRow.size > 0 && nextRow.size == colId && nextRow[nextRow.size - 1] == currentRow[colId]) isBottom = true
                    else {
                        if (rowId + 2 < game.rows.size) {
                            var secondNextRow = game.rows[rowId + 2]
                            isBottom = secondNextRow.size > colId &&
                                    secondNextRow[colId] == currentRow[colId]
                        } else isBottom = false
                    }
                }
            }

            // First coup
            if (i==0) {
                rowId = 0
                game.rows[rowId].add(game.coupArray[i])
            }
            // different value
            else if(game.coupArray[i] != game.coupArray[i-1]) {
                // add empty coup til reach bottom row
                var j = rowId + 1
                while (j < game.rows.size) {
                    while (game.rows[j].size <= colId) {
                        game.rows[j].add(9)
                    }
                    j++
                }
                rowId = 0
                game.rows[rowId].add(game.coupArray[i])
                colId = game.rows[rowId].size - 1
            }
            // same value
            else {
                // add empty coup til reach bottom row
                if (isBottom) {
                    var k = rowId + 1
                    while (k < game.rows.size) {
                        while (game.rows[k].size <= colId) {
                            game.rows[k].add(9)
                        }
                        k++
                    }
                    colId++
                }
                else {
                    rowId++
                }
                game.rows[rowId].add(game.coupArray[i])
            }

            //popuate group 3 string
            if (groupThreeIndex + 2 < game.coupArray.size) {
                when (game.coupArray.subList(groupThreeIndex, groupThreeIndex + 3).toString()) {
                    "[0, 0, 0]" -> groupString.append("1,")
                    "[0, 0, 1]" -> groupString.append("2,")
                    "[0, 1, 0]" -> groupString.append("3,")
                    "[0, 1, 1]" -> groupString.append("4,")
                    "[1, 0, 0]" -> groupString.append("5,")
                    "[1, 0, 1]" -> groupString.append("6,")
                    "[1, 1, 0]" -> groupString.append("7,")
                    "[1, 1, 1]" -> groupString.append("8,")
                }
            }

            i++
            groupThreeIndex += 1
        }
        game.groupThreeString = groupString.trim(',').toString()

        // get max row size
        var maxRowSize = 0
        for (row in game.rows) {
            if (row.size > maxRowSize) maxRowSize = row.size
        }
        // fill up empty space
        for (row in game.rows) {
            while (row.size < maxRowSize) row.add(9)
        }

        return game
    }

    fun CalculateEmptyInGame (game:Game):Unit {
        // Calculate empty cells in second row
        var i = 0
        var numberOfEmpty = 0

        while (i < game.row1.size) {
            if (game.row1[i] == 9) {
                numberOfEmpty++
            }
            else {
                if (numberOfEmpty > 0) {
                    if (numberOfEmpty > 4) numberOfEmpty = 4
                    game.emptyArray.add(numberOfEmpty)
                }
                numberOfEmpty = 0
            }
            if (i == game.row1.size-1) {
                if (numberOfEmpty > 0) {
                    if (numberOfEmpty > 4) numberOfEmpty = 4
                    game.emptyArray.add(numberOfEmpty)
                }
            }
            i++
        }

        var contOneEmpty = 0
        var contTwoEmpty = 0
        var contThreeEmpty = 0
        var contFourEmpty = 0

        var j=0
        if (game.emptyArray != null) {
            while (j < game.emptyArray.size) {
                when (game.emptyArray[j]) {
                    1 -> {
                        game.oneEmptyCount++
                        if (j == 0) contOneEmpty++
                        else if (game.emptyArray[j] == game.emptyArray[j - 1]) contOneEmpty++
                        else {
                            contOneEmpty = 1
                        }
                        if (game.maxContOneEmpty < contOneEmpty) {
                            game.maxContOneEmpty = contOneEmpty
                        }
                    }
                    2 -> {
                        game.twoEmptyCount++
                        if (j == 0) contTwoEmpty++
                        else if (game.emptyArray[j] == game.emptyArray[j - 1]) contTwoEmpty++
                        else {
                            contTwoEmpty = 1
                        }
                        if (game.maxContTwoEmpty < contTwoEmpty) {
                            game.maxContTwoEmpty = contTwoEmpty
                        }
                    }
                    3 -> {
                        game.threeEmptyCount++
                        if (j == 0) contThreeEmpty++
                        else if (game.emptyArray[j] == game.emptyArray[j - 1]) contThreeEmpty++
                        else {
                            contThreeEmpty = 1
                        }
                        if (game.maxContThreeEmpty < contThreeEmpty) {
                            game.maxContThreeEmpty = contThreeEmpty
                        }
                    }
                    4 -> {
                        game.fourEmptyCount++
                        if (j == 0) contFourEmpty++
                        else if (game.emptyArray[j] == game.emptyArray[j - 1]) contFourEmpty++
                        else {
                            contFourEmpty = 1
                        }
                        if (game.maxContFourEmpty < contFourEmpty) {
                            game.maxContFourEmpty = contFourEmpty
                        }
                    }
                }
                j++
            }
        }
    }

    fun CalculateSameInGame (game:Game):Unit {
        var i = 0
        var numOfSame = 0

        while (i < Math.min(game.row0.size, game.row1.size) -1) {
            if (game.row0[i] != 9 && game.row0[i+1] != 9 &&
                game.row0[i] == game.row1[i] && game.row0[i+1] == game.row1[i+1]) {
                numOfSame++
            }
            else {
                if (numOfSame > 0) {
                    if (numOfSame > 4) numOfSame = 4
                    game.sameArray.add(numOfSame)
                }
                numOfSame = 0
            }
            if (i == Math.min(game.row0.size, game.row1.size) -2 && numOfSame > 0) {
                if (numOfSame > 4) numOfSame = 4
                game.sameArray.add(numOfSame)
            }
            i++
        }

        if (game.sameArray.max() != null) game.maxSame = game.sameArray.max()!!

        var contOneSame = 0
        var contTwoSame = 0
        var contThreeSame = 0
        var contFourSame = 0

        var j = 0
        if (game.sameArray != null) {
            while (j < game.sameArray.size) {
                if (game.sameArray[j] > 4) game.sameArray[j] = 4
                when (game.sameArray[j]) {
                    1 -> {
                        game.oneSameCount++
                        if (j == 0) {
                            contOneSame++
                        } else if (game.sameArray[j] == game.sameArray[j - 1]) contOneSame++
                        else {
                            contOneSame = 1
                        }
                        if (game.maxContOneSame < contOneSame) {
                            game.maxContOneSame = contOneSame
                        }
                    }

                    2 -> {
                        game.twoSameCount++
                        if (j == 0) {
                            contTwoSame++
                        } else if (game.sameArray[j] == game.sameArray[j - 1]) contTwoSame++
                        else {
                            contTwoSame = 1
                        }
                        if (game.maxContTwoSame < contTwoSame) {
                            game.maxContTwoSame = contTwoSame
                        }
                    }

                    3 -> {
                        game.threeSameCount++
                        if (j == 0) {
                            contThreeSame++
                        } else if (game.sameArray[j] == game.sameArray[j - 1]) contThreeSame++
                        else {
                            contThreeSame = 1
                        }
                        if (game.maxContThreeSame < contThreeSame) {
                            game.maxContThreeSame = contThreeSame
                        }
                    }

                    4 -> {
                        game.fourSameCount++
                        if (j == 0) {
                            contFourSame++
                        } else if (game.sameArray[j] == game.sameArray[j - 1]) contFourSame++
                        else {
                            contFourSame = 1
                        }
                        if (game.maxContFourSame < contFourSame) {
                            game.maxContFourSame = contFourSame
                        }
                    }
                }
                j++
            }
        }
    }
}

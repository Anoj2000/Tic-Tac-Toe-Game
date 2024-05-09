package com.example.tictacgame

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.tictacgame.R.id
import android.content.SharedPreferences

class MainActivity : AppCompatActivity(), View.OnClickListener {
    // Array value  store the buttons
    private val buttons: Array<Array<Button?>> = Array<Array<Button?>>(3) { arrayOfNulls<Button>(3) }

    private var player1Turn = true
    // number of rounds played
    private var roundCount = 0
    // Scores for each player
    private var player1Points = 0
    private var player2Points = 0
    //  display the scores
    private lateinit var texviewP1: TextView
    private lateinit var texviewP2: TextView
    // Names of the players
    private var playerFirst = " "
    private var playerSecond = " "

    private lateinit var sharedPreferences: SharedPreferences
    private var highestScore: Int = 0

    // Overrides the onCreate method to set up the game
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        texviewP1 = findViewById(R.id.text_viewP1)
        texviewP2 = findViewById(R.id.text_viewP2)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Retrieve highest score from SharedPreferences
        highestScore = sharedPreferences.getInt("highestScore", 0)

        // Update TextView with the highest score
        updateHighScoreTextView()

        getPlayerName()
        for (i in 0..2) {
            for (j in 0..2) {
                val buttonID = "button_$i$j"
                val resID = resources.getIdentifier(buttonID, "id", packageName)
                buttons[i][j] = findViewById(resID)
                buttons[i][j]!!.setOnClickListener(this)
            }
        }
        val buttonReset: Button = findViewById(id.resetBtn)
        buttonReset.setOnClickListener { resetGame() }
    }

    // This class represents the landing page of the app
    class landing_page : AppCompatActivity() {
        // Overrides the onCreate method to set up the landing page
        @SuppressLint("MissingInflatedId")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.start_page)

            // Sets up the click listener for the "Start" button
            val button: Button = findViewById(id.btnStart)
            button.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // Method to update highest score
    private fun updateHighestScore(score: Int) {
        if (score > highestScore) {
            highestScore = score
            // Update SharedPreferences with the new highest score
            sharedPreferences.edit().putInt("highestScore", highestScore).apply()

            updateHighScoreTextView()
        }
    }

    // Method to update the TextView with the highest score
    private fun updateHighScoreTextView() {
        val highScoreTextView = findViewById<TextView>(R.id.text_viewP4)
        highScoreTextView.text = "Player High score : $highestScore"
    }


    // This method prompts the user to enter the names of the players
    private fun getPlayerName() {
        val infalter = LayoutInflater.from(this@MainActivity)
        val plView = infalter.inflate(R.layout.player_info, null)
        val player1Name = plView.findViewById<EditText>(R.id.firstPl1)
        val player2Name = plView.findViewById<EditText>(R.id.firstPl2)
        val plDialog = AlertDialog.Builder(this@MainActivity)
        plDialog.setView(plView)
        plDialog.setPositiveButton("Add") { dialog,_ ->
            if (player1Name.text.toString().isEmpty() && player2Name.text.toString().isEmpty()) {
                playerFirst = "Player1"
                playerSecond = "Player2"
                texviewP1.text = "$playerFirst : 0"
                texviewP2.text = "$playerSecond : 0"
            } else {
                playerFirst = player1Name.text.toString()
                playerSecond = player2Name.text.toString()
                texviewP1.text = "$playerFirst : 0"
                texviewP2.text = "$playerSecond : 0"
            }
        }
        plDialog.setNeutralButton("Cancel") { dialog, _ ->
            playerFirst = "Player1"
            playerSecond = "Player2"
            texviewP1.text = "$playerFirst : 0"
            texviewP2.text = "$playerSecond : 0"
        }
        plDialog.create()
        plDialog.show()
    }

    // Overrides the onClick method to handle button clicks
    override fun onClick(v: View) {
        if (!(v as Button).getText().toString().equals("")) {
            return
        }
        if (player1Turn) {
            (v as Button).setText("X")
            v.setTextColor(resources.getColor(R.color.red))
        } else {
            (v as Button).setText("O")
            v.setTextColor(resources.getColor(R.color.green))
        }
        roundCount++
        if (checkForWin()) {
            if (player1Turn) {
                player1Wins()
            } else {
                player2Wins()
            }
        } else if (roundCount == 9) {
            draw()
        } else {
            player1Turn = !player1Turn
        }
    }

    // This method checks if a player has won the game
    private fun checkForWin(): Boolean {
        val field = Array(3) { arrayOfNulls<String>(3) }
        for (i in 0..2) {
            for (j in 0..2) {
                field[i][j] = buttons[i][j]!!.text.toString()
            }
        }
        for (i in 0..2) {
            if (field[i][0] == field[i][1] && field[i][0] == field[i][2] && field[i][0] != "") {
                return true
            }
        }
        for (i in 0..2) {
            if (field[0][i] == field[1][i] && field[0][i] == field[2][i] && field[0][i] != "") {
                return true
            }
        }
        if (field[0][0] == field[1][1] && field[0][0] == field[2][2] && field[0][0] != "") {
            return true
        }
        return if (field[0][2] == field[1][1] && field[0][2] == field[2][0] && field[0][2] != "") {
            true
        } else false
    }

    // This method is called when Player 1 wins the game
    private fun player1Wins() {
        player1Points++
        AlertDialog.Builder(this@MainActivity)
            .setTitle("Congratulation !!")
            .setIcon(R.drawable._con)
            .setMessage("Congratulation $playerFirst, you are Win ... !!")
            .create()
            .show()
        updatePointsText()

        updateHighestScore(player1Points)

        resetBoard()
    }

    // This method is called when Player 2 wins the game
    private fun player2Wins() {
        player2Points++
        AlertDialog.Builder(this@MainActivity)
            .setTitle("Congratulation !!")
            .setIcon(R.drawable._con)
            .setMessage("Congratulation $playerSecond, you are Win ... !!")
            .create()
            .show()
        updatePointsText()

        updateHighestScore(player2Points)

        resetBoard()
    }

    // This method is called when the game ends in a draw
    private fun draw() {
        AlertDialog.Builder(this)
            .setTitle("Draw !!")
            .setMessage("The game is drawn, Please Play again...!!")
            .setPositiveButton("ok") { dialog, _ ->
                resetBoard()
            }
            .setNeutralButton("Cancel") { dialog, _ -> resetBoard() }
            .create()
            .show()
    }

    // This method updates the score text views
    private fun updatePointsText() {
        texviewP1.text = "Player 1: $player1Points"
        texviewP2.text = "Player 2: $player2Points"
    }

    // This method resets the game board
    private fun resetBoard() {
        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j]!!.setText("")
            }
        }
        roundCount = 0
        player1Turn = true
    }

    // This method resets the entire game
    private fun resetGame() {
        player1Points = 0
        player2Points = 0
        updatePointsText()
        resetBoard()
    }

    // This method saves the game state when the app is paused
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("roundCount", roundCount)
        outState.putInt("player1Points", player1Points)
        outState.putInt("player2Points", player2Points)
        outState.putBoolean("player1Turn", player1Turn)
    }

    // This method restores the game state when the app is resumed
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        player1Points = savedInstanceState.getInt("player1Points")
        player2Points = savedInstanceState.getInt("player2Points")
        player1Turn = savedInstanceState.getBoolean("player1Turn")
    }
}
package com.example.wordle

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.view.View
import android.widget.FrameLayout
import androidx.core.text.color
import com.github.jinatonic.confetti.CommonConfetti

class MainActivity : AppCompatActivity() {

    var targetWord = ""
    var remainingGuess = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playGame()

        val guessInput = findViewById<EditText>(R.id.guessInput)
        val resultText = findViewById<TextView>(R.id.resultText)
        val resetButton = findViewById<Button>(R.id.resetButton)
        val submitButton = findViewById<Button>(R.id.submitButton)

        submitButton.setOnClickListener {
            if (remainingGuess > 0) {
                val userGuess = guessInput.text.toString().uppercase()
                if (userGuess.length == 4 && userGuess.matches(Regex("[A-Z]+"))) {
                    val correctnessText = findViewById<TextView>(R.id.correctNess)
                    val correctness = checkGuess(userGuess)

                    // Create a new SpannableString
                    val spannableString = SpannableString("\n\n$userGuess\n\n$userGuess")

                    // Apply color spans to the correctness part
                    for (i in 0 until 4) {
                        when {
                            correctness[i] == 'O' -> spannableString.setSpan(ForegroundColorSpan(Color.GREEN), i + 8, i + 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            correctness[i] == 'X' -> spannableString.setSpan(ForegroundColorSpan(Color.RED), i + 8, i + 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }

                    // Append the new SpannableString to the existing text
                    correctnessText.append(spannableString)

                    val guessText = findViewById<TextView>(R.id.guessText)
                    val guessNumber = 4 - remainingGuess
                    val formattedGuess = "Guess #$guessNumber"
                    val formattedGuessCheck = "Guess #$guessNumber Check"

                    guessText.append("\n\n$formattedGuess\n\n$formattedGuessCheck")

                    remainingGuess--

                    if (correctness.toString() == "OOOO") {
                        val resultText = findViewById<TextView>(R.id.resultText)
                        addConfettiAnimation()
                        resultText.append("\n$targetWord")
                        disableInputButton()
                        swapButtons(submitButton, resetButton, "Reset")
                    }
                    else if(remainingGuess == 0) {
                        val resultText = findViewById<TextView>(R.id.resultText)
                        resultText.append("\n$targetWord")
                        disableInputButton()
                        swapButtons(submitButton, resetButton, "Reset")
                    }
                }
            }
        }


        resetButton.setOnClickListener {
            playGame()
            clearScreen()
            swapButtons(resetButton, submitButton, "Submit")
        }
    }

    private fun clearScreen() {
        val correctText = findViewById<TextView>(R.id.correctNess)
        val guessText = findViewById<TextView>(R.id.guessText)
        correctText.text = ""
        guessText.text = ""

        val resultText = findViewById<TextView>(R.id.resultText)
        resultText.text = ""
    }

    fun playGame() {
        targetWord = FourLetterWordList.getRandomFourLetterWord()
        remainingGuess = 3

        val resultText = findViewById<TextView>(R.id.resultText)
        resultText.text = ""

        val guessInput = findViewById<EditText>(R.id.guessInput)
        guessInput.text.clear()

        enableInputButton()
    }

    private fun checkGuess(guess: String): SpannableStringBuilder {
        var result = SpannableStringBuilder()
        for (i in 0..3) {
            if (guess[i] == targetWord[i]) {
                result.color(Color.GREEN, { append("O") })
            } else if (guess[i] in targetWord) {
                result.color(Color.RED, { append("X") })
            } else {
                result.append("+")
            }
        }
        return result
    }

    private fun disableInputButton() {
        val submitButton: Button = findViewById(R.id.submitButton)
        val resetButton: Button = findViewById(R.id.resetButton)
        val guessInput: EditText = findViewById(R.id.guessInput)

        submitButton.isEnabled = false
        guessInput.isEnabled = false
        resetButton.isEnabled = true
    }

    private fun enableInputButton() {
        val submitButton: Button = findViewById(R.id.submitButton)
        val resetButton: Button = findViewById(R.id.resetButton)
        val guessInput: EditText = findViewById(R.id.guessInput)

        submitButton.isEnabled = true
        guessInput.isEnabled = true
        resetButton.isEnabled = false
    }

    private fun swapButtons(buttonToHide: Button, buttonToShow: Button, buttonText: String) {
        buttonToHide.visibility = View.GONE
        buttonToShow.visibility = View.VISIBLE
        buttonToShow.text = buttonText
    }

    private fun addConfettiAnimation() {
        val container = findViewById<FrameLayout>(R.id.confettiContainer)

        CommonConfetti.rainingConfetti(container, intArrayOf(Color.YELLOW, Color.GREEN))
            .infinite();

        val confettiDuration = 3000L

        Handler().postDelayed({
            container.removeAllViews()
        }, confettiDuration)
    }

}
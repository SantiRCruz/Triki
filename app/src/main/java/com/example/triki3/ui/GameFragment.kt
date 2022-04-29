package com.example.triki3.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.navArgs
import com.example.triki3.R
import com.example.triki3.databinding.FragmentGameBinding

class GameFragment : Fragment(R.layout.fragment_game) {
    private val args by navArgs<GameFragmentArgs>()

    enum class Turn {
        NOUGHT,
        CROSS
    }

    private var firstTurn = Turn.CROSS
    private var currentTurn = Turn.CROSS

    private var noughtsScore = 0
    private var crossesScore = 0

    private var boardList = mutableListOf<Button>()

    private lateinit var binding: FragmentGameBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGameBinding.bind(view)
        setTurnLabel()
        scoreBoard()
        initBoard()
        clicks()

    }

    private fun scoreBoard() {
        binding.txtPlayerOne.text = "${args.playerOne} : $crossesScore"
        binding.txtPlayerTwo.text = "${args.playerTwo} : $noughtsScore"
    }

    private fun clicks() {
        binding.a1.setOnClickListener { boardTapped(it) }
        binding.a2.setOnClickListener { boardTapped(it) }
        binding.a3.setOnClickListener { boardTapped(it) }
        binding.b1.setOnClickListener { boardTapped(it) }
        binding.b2.setOnClickListener { boardTapped(it) }
        binding.b3.setOnClickListener { boardTapped(it) }
        binding.c1.setOnClickListener { boardTapped(it) }
        binding.c2.setOnClickListener { boardTapped(it) }
        binding.c3.setOnClickListener { boardTapped(it) }
    }

    private fun initBoard() {
        boardList.add(binding.a1)
        boardList.add(binding.a2)
        boardList.add(binding.a3)
        boardList.add(binding.b1)
        boardList.add(binding.b2)
        boardList.add(binding.b3)
        boardList.add(binding.c1)
        boardList.add(binding.c2)
        boardList.add(binding.c3)
    }

    private fun boardTapped(view: View) {
        if (view !is Button)
            return
        addToBoard(view)

        if (checkForResult(NOUGHT)) {
            noughtsScore++
            result("${args.playerTwo} Win!")
        }
        if (checkForResult(CROSS)) {
            crossesScore++
            result("${args.playerOne} Win!")
        }

        if (fullBoard()) {
            result("Draw")
        }
    }

    private fun checkForResult(s: String): Boolean {
        //horizontal
        if (match(binding.a1, s) && match(binding.a2, s) && match(binding.a3, s))
            return true
        if (match(binding.b1, s) && match(binding.b2, s) && match(binding.b3, s))
            return true
        if (match(binding.c1, s) && match(binding.c2, s) && match(binding.c3, s))
            return true
        //vertical
        if (match(binding.a1, s) && match(binding.b1, s) && match(binding.c1, s))
            return true
        if (match(binding.a2, s) && match(binding.b2, s) && match(binding.c2, s))
            return true
        if (match(binding.a3, s) && match(binding.b3, s) && match(binding.c3, s))
            return true

        //diagonal
        if (match(binding.a1, s) && match(binding.b2, s) && match(binding.c3, s))
            return true
        if (match(binding.a3, s) && match(binding.b2, s) && match(binding.c1, s))
            return true


        return false
    }

    private fun match(button: Button, symbol: String): Boolean = button.text == symbol

    private fun result(s: String) {
        val message = "\n${args.playerOne} $crossesScore \n\n${args.playerTwo} $noughtsScore"
        AlertDialog.Builder(requireContext())
            .setTitle(s)
            .setMessage(message)
            .setPositiveButton("Reset") { _, _ ->
                resetBoard()
            }
            .setCancelable(false)
            .show()

        scoreBoard()

    }

    private fun resetBoard() {
        for (button in boardList) {
            button.text = ""
        }
        if (firstTurn == Turn.NOUGHT)
            firstTurn = Turn.CROSS
        else if (firstTurn == Turn.CROSS)
            firstTurn = Turn.NOUGHT

        currentTurn = firstTurn
        setTurnLabel()
    }

    private fun fullBoard(): Boolean {
        for (button in boardList) {
            if (button.text == "")
                return false
        }
        return true
    }

    private fun addToBoard(button: Button) {
        if (button.text != "")
            return

        if (currentTurn == Turn.NOUGHT) {
            button.text = NOUGHT
            currentTurn = Turn.CROSS
        } else if (currentTurn == Turn.CROSS) {
            button.text = CROSS
            currentTurn = Turn.NOUGHT
        }

        setTurnLabel()
    }

    private fun setTurnLabel() {
        var turnText = ""
        if (currentTurn == Turn.NOUGHT)
            turnText = "Turn ${args.playerTwo} $NOUGHT"
        else if (currentTurn == Turn.CROSS)
            turnText = "Turn ${args.playerOne} $CROSS"

        binding.turnTV.text = turnText
    }

    companion object {
        const val NOUGHT = "O"
        const val CROSS = "X"
    }
}
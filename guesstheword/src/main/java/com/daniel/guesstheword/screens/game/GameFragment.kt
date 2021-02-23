package com.daniel.guesstheword.screens.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.daniel.guesstheword.R
import com.daniel.guesstheword.databinding.GameFragmentBinding

class GameFragment : Fragment() {

//
//    //the current word
//    private var word = ""
//
//    // curent score
//    private var score = 0
//
//    // list of words - the front of the list is the next word to guess
//    private lateinit var wordList: MutableList<String>

    private lateinit var binding: GameFragmentBinding

    // viewmodel that will be associated with this fragment
    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)

        Log.i("GameFragment", "Called ViewModelProvider.get")
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        viewModel.score.observe(viewLifecycleOwner, Observer { newScore ->
            
            binding.scoreText.text = newScore.toString()
        })

        viewModel.word.observe( viewLifecycleOwner, Observer{ newWord->
            binding.wordText.text = newWord
        })


        binding.correctButton.setOnClickListener { onCorrect() }
        binding.skipButton.setOnClickListener { onSkip() }
        binding.endGameButton.setOnClickListener { onEndGame() }

//        not needed after updating the live data observer
//        updateScoreText()
//        updateWordText()

        return binding.root
    }

    /** not needed afer adding the view model
     * Resets the list of words and randomizes the order


    private fun resetList() {

        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }
     */
    /**
     * Moves to the next word in the list
    private fun nextWord() {
        if (!wordList.isEmpty()) {
            //Select and remove a word from the list
            word = wordList.removeAt(0)
        }
        updateWordText()
        updateScoreText()
    }
     */

    /** Methods for buttons presses **/

    private fun onSkip() {
        viewModel.onSkip()

//        updateScoreText()
//        updateWordText()
    }

    private fun onCorrect() {
        viewModel.onCorrect()
//        updateWordText()
//        updateScoreText()
    }




    /** Methods for updating the UI **/
//        not needed after updating the live data observer
    private fun updateWordText() {
        binding.wordText.text = viewModel.word.value.toString()
    }
//        not needed after updating the live data observer
    private fun updateScoreText() {
        binding.scoreText.text = viewModel.score.toString()
    }

    private fun onEndGame(){
        gameFinished()
    }

    private fun gameFinished(){
        Toast.makeText(activity, "game finishde",Toast.LENGTH_LONG).show()
        val action = GameFragmentDirections.actionGameFragmentToScoreFragment()
        action.score = viewModel.score.value?:0
        NavHostFragment.findNavController(this).navigate(action)
    }
}


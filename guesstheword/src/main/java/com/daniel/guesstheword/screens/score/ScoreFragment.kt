package com.daniel.guesstheword.screens.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.daniel.guesstheword.R
import com.daniel.guesstheword.databinding.ScoreFragmentBinding

class ScoreFragment : Fragment() {

    private lateinit var viewModel: ScoreViewModel
    private lateinit var viewModelFactory: ScoreViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: ScoreFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.score_fragment, container, false)

        viewModelFactory =
            ScoreViewModelFactory(ScoreFragmentArgs.fromBundle(requireArguments()).score)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ScoreViewModel::class.java)
//        binding.scoreText.text = viewModel.score.toString()

        viewModel.score.observe(viewLifecycleOwner, Observer { newScore ->
            binding.scoreText.text = newScore.toString()
        })

        // Navigates back to game when button is pressed
        viewModel.eventPlayAgain.observe(viewLifecycleOwner, Observer { playAgain ->
            if (playAgain) {
                findNavController().navigate(ScoreFragmentDirections.actionScoreFragmentToGameFragment())
                viewModel.onPlayAgainComplete()
            }
        })

        binding.playAgainButton.setOnClickListener { viewModel.onPlayAgain() }

        return binding.root
    }
}
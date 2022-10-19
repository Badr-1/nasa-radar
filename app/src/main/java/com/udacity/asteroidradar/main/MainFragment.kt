package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(requireActivity().application)).get(
            MainViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.asteroidRecycler.adapter = AsteroidAdapter(AsteroidAdapter.OnClickListener {
            viewModel.navigateToSelectedAsteroid(it)
        })
        viewModel.navigateToSelectedAsteroid.observe(viewLifecycleOwner) {
            if (null != it) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayAsteroidDetailsComplete()
            }
        }
        viewModel.filter.observe(viewLifecycleOwner) {
            (binding.asteroidRecycler.adapter as AsteroidAdapter).submitList(viewModel.filteredAsteroids.value)
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_week_menu -> {
                viewModel.updateFilter(AsteroidFilter.SHOW_WEEK)
                true
            }
            R.id.show_today_menu -> {
                viewModel.updateFilter(AsteroidFilter.SHOW_TODAY)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

package com.zaze.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaze.apps.base.AbsFragment
import com.zaze.apps.databinding.FragmentOverviewBinding

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-21 - 16:14
 */
class OverviewFragment : AbsFragment() {
    private lateinit var binding: FragmentOverviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.overviewRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}
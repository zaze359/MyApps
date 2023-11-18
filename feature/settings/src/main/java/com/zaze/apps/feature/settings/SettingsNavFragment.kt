package com.zaze.apps.feature.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.zaze.apps.core.base.AbsFragment
import com.zaze.apps.core.ext.initToolbar
import com.zaze.apps.feature.settings.databinding.SettingsFragmentNavBinding
import com.zaze.apps.core.ext.findNavHostFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * 设置导航页
 */
@AndroidEntryPoint
class SettingsNavFragment : AbsFragment(), NavController.OnDestinationChangedListener {

    private var _binding: SettingsFragmentNavBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentNavBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavHostFragment(R.id.fragment_container).navController
        initToolbar(binding.appbarLayout.toolbar) {
            title = "设置"
            it.isTitleCentered = false
            it.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

//        viewLifecycleOwner.lifecycleScope.launch {
//            val navGraph = navController.navInflater.inflate(R.navigation.settings_graph)
//            navController.graph = navGraph
//            val destinationId = appPreferencesDataStore.getScope().lastNavDestination
//            ZLog.i(ZTag.TAG, "get lastNavDestination: $destinationId")
//            if (navGraph.contains(destinationId)) {
//                navGraph.setStartDestination(destinationId)
//            }
//            navController.addOnDestinationChangedListener(this@SettingsNavFragment)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        findNavHostFragment(R.id.fragment_container).navController.removeOnDestinationChangedListener(
            this
        )
        _binding = null
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.id == controller.graph.startDestinationId) { // 去除第一个页面的进入动画。
            findNavHostFragment(R.id.fragment_container).childFragmentManager.fragments.firstOrNull()?.enterTransition = null
        }
        binding.appbarLayout.title = destination.label?.toString() ?: ""
    }
}
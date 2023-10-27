package com.zaze.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaze.apps.MenuHandler.handleMenuItemSelected
import com.zaze.apps.base.AbsFragment
import com.zaze.apps.databinding.FragmentOverviewBinding
import com.zaze.apps.adapters.CardsAdapter
import com.zaze.apps.ext.setupActionBar
import com.zaze.apps.utils.AppUsageHelper
import com.zaze.apps.viewmodels.OverviewAction
import com.zaze.apps.viewmodels.OverviewUiState
import com.zaze.apps.viewmodels.OverviewViewModel
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.launch

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-21 - 16:14
 */
class OverviewFragment : AbsFragment(), MenuProvider {
    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OverviewViewModel by activityViewModels()

    private lateinit var cardsAdapter: CardsAdapter

    private val appUsagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            ZLog.i(ZTag.TAG, "result: ${it.resultCode}, ${it.data}")
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.loadOverview()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar(binding.toolbar)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.STARTED)
        binding.overviewRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cardsAdapter = CardsAdapter()
        binding.overviewRecyclerView.adapter = cardsAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when(state) {
                        OverviewUiState.NULL -> {

                        }
                        is OverviewUiState.HasData -> {
                            cardsAdapter.submitList(state.cards)
                            if (state.moveToTop) {
                                binding.overviewRecyclerView.smoothScrollToPosition(0)
                            }
                            when(val action = state.action) {
                                OverviewAction.JumpToAppListFragment -> {
                                    val navController = requireActivity().findNavController(R.id.fragment_container)
                                    val builder = NavOptions.Builder().setLaunchSingleTop(true).setRestoreState(true)
                                    builder.setPopUpTo(
                                        navController.graph.findStartDestination().id,
                                        inclusive = false,
                                        saveState = true
                                    )
                                    navController.navigate(R.id.app_list_fragment, null, builder.build())
                                }
                                OverviewAction.RequestAppUsagePermission -> {
                                    AppUsageHelper.requestAppUsagePermission(appUsagePermissionLauncher)
                                }
                                is OverviewAction.StartActivity -> {
                                    startActivity(action.intent)
                                }
                                OverviewAction.None ->{

                                }
                            }
                            ZLog.i(ZTag.TAG, "state.action:${state.action}")
                            viewModel.actionHandled(state.action)
                        }
                    }

                }
            }
        }
        viewModel.loadOverview()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.action_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return handleMenuItemSelected(menuItem)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
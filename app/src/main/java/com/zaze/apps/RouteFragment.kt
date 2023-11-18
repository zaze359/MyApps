package com.zaze.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zaze.apps.core.base.AbsFragment
import com.zaze.apps.databinding.FragmentRouteBinding

/**
 * Description : 用于跳转到实际的Fragment 中。
 * @author : zaze
 * @version : 2023-11-13 23:26
 */
abstract class RouteFragment : AbsFragment() {

    private var _binding: FragmentRouteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteBinding.inflate(inflater, container, false)
        getFragment()?.let {
            childFragmentManager.beginTransaction().replace(R.id.route_layout, it).commit()
        }
        return binding.root
    }

    abstract fun getFragment(): Fragment?
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
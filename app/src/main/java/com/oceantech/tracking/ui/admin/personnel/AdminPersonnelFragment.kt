package com.oceantech.tracking.ui.admin.personnel

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.airbnb.mvrx.activityViewModel
import com.google.android.material.tabs.TabLayout
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Constants.Companion.TAG
import com.oceantech.tracking.databinding.FragmentAdminPersonnelBinding
import com.oceantech.tracking.ui.admin.AdminViewModel

@SuppressLint("SetTextI18n")
class AdminPersonnelFragment : TrackingBaseFragment<FragmentAdminPersonnelBinding>() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAdminPersonnelBinding {
        return FragmentAdminPersonnelBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = views.tabLayout
        viewPager = views.viewPager

        tabLayout.setupWithViewPager(viewPager)
        val vpAdapter = VPAdapter(childFragmentManager)
        vpAdapter.addFragment(AdminTeamFragment(), "Team")
        vpAdapter.addFragment(AdminMemberFragment(), "Member")
        viewPager.offscreenPageLimit = vpAdapter.count
        viewPager.adapter = vpAdapter
    }

    inner class VPAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        private val fragmentList = arrayListOf<Fragment>()
        private val fragmentTitleList = arrayListOf<String>()

        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        fun addFragment(fragment: Fragment, title: String){
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitleList[position]
        }
    }
}

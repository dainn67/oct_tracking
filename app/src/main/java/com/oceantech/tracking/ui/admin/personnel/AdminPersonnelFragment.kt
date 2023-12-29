package com.oceantech.tracking.ui.admin.personnel

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.AdapterView.VISIBLE
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.tabs.TabLayout
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.Constants
import com.oceantech.tracking.data.model.Constants.Companion.TAG
import com.oceantech.tracking.data.model.response.Member
import com.oceantech.tracking.data.model.response.Team
import com.oceantech.tracking.databinding.FragmentAdminPersonnelBinding
import com.oceantech.tracking.databinding.ItemMemberBinding
import com.oceantech.tracking.databinding.ItemTeamBinding
import com.oceantech.tracking.ui.admin.AdminViewModel
import com.oceantech.tracking.ui.admin.OnCallBackListenerAdmin

@SuppressLint("SetTextI18n")
class AdminPersonnelFragment : TrackingBaseFragment<FragmentAdminPersonnelBinding>() {

    private val viewModel: AdminViewModel by activityViewModel()
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
        val vpAdapter = VPAdapter(requireActivity().supportFragmentManager)
        vpAdapter.addFragment(AdminTeamFragment(), "Team")
        vpAdapter.addFragment(AdminMemberFragment(), "Member")
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

package com.oceantech.tracking.ui.admin

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.MenuRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.utils.LocalHelper
import com.google.android.material.navigation.NavigationView
import java.util.*
import javax.inject.Inject
import com.oceantech.tracking.R
import com.oceantech.tracking.databinding.ActivityMainAdminBinding
import com.oceantech.tracking.ui.security.LoginActivity
import com.oceantech.tracking.ui.security.UserPreferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ActivityAdmin : TrackingBaseActivity<ActivityMainAdminBinding>(), AdminViewModel.Factory {

    private val adminViewModel: AdminViewModel by viewModel()

    @Inject
    lateinit var userPref: UserPreferences

    @Inject
    lateinit var localHelper: LocalHelper

    @Inject
    lateinit var adminViewModelFactory: AdminViewModel.Factory

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView

    private var lang = "en"

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(views.root)
        setupToolbar()
        setupDrawer()

        adminViewModel.subscribe(this) {
            views.waitingView.visibility = if (it.isLoading() || it.isFailed())
                View.VISIBLE else View.GONE
        }
    }


    override fun create(initialState: AdminViewState): AdminViewModel {
        return adminViewModelFactory.create(initialState)
    }

    override fun getBinding(): ActivityMainAdminBinding {
        return ActivityMainAdminBinding.inflate(layoutInflater)
    }

    private fun setupToolbar() {
        toolbar = views.toolbar
        toolbar.title = ""
        views.title.text = getString(R.string.app_name)
        setSupportActionBar(toolbar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }


    @SuppressLint("ResourceType")
    private fun setupDrawer() {
        drawerLayout = views.includeDrawerLayout.drawerLayoutAdmin
        navView = views.includeDrawerLayout.navView
        navController = findNavController(R.id.nav_host_fragment_content_admin)

        appBarConfiguration = AppBarConfiguration(
            // nếu ko thêm vào đây thì icon là back, nhấn quay lại tracking, thêm thì thành mở menu
            setOf(
                R.id.adminTrackingFragment,
                R.id.adminProjectFragment,
                R.id.adminPersonnelFragment,
                R.id.adminUsersFragment
            ),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // settings
        navView.setNavigationItemSelectedListener { menuItem ->

            val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)

            when (menuItem.itemId) {
                R.id.nav_home_admin -> {
                    navController.navigate(R.id.adminTrackingFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    views.title.text = getString(R.string.app_name)
                }
                R.id.nav_project -> {
                    navController.navigate(R.id.adminProjectFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    views.title.text = getString(R.string.project)
                }
                R.id.nav_personnel -> {
                    navController.navigate(R.id.adminPersonnelFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    views.title.text = getString(R.string.personnel)
                }
                R.id.nav_users -> {
                    navController.navigate(R.id.adminUsersFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    views.title.text = getString(R.string.users)
                }
                R.id.exit -> {
                    val homeIntent = Intent(Intent.ACTION_MAIN)
                    homeIntent.addCategory(Intent.CATEGORY_HOME)
                    homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(homeIntent)
                }

                R.id.nav_change_language -> {
                    showMenu(findViewById(R.id.nav_change_language), R.menu.menu_main)
                }

                R.id.logout -> {
                    GlobalScope.launch {
                        userPref.clear()
                    }
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }

                else -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    handled
                }
            }
            handled
        }
        val menu: Menu = navView.menu
        val menuItem = menu.findItem(R.id.nav_change_language)
        val actionView: View = MenuItemCompat.getActionView(menuItem)

        val res: Resources = resources
        val conf: Configuration = res.configuration
        val local = conf.locale
        val lang = local.displayLanguage
        if (lang == "English") {
            adminViewModel.language = 0
            menuItem.title = getString(R.string.en)
        } else {
            adminViewModel.language = 1
            menuItem.title = getString(R.string.vi)
        }
        val buttonShowMenu = actionView as AppCompatImageView
        buttonShowMenu.setImageDrawable(getDrawable(R.drawable.ic_drop))
        buttonShowMenu.setOnClickListener {
            showMenu(findViewById(R.id.nav_change_language), R.menu.menu_main)
        }

    }

    private fun changeLanguage(lang: String) {
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        val myLocale = Locale(lang)
        conf.setLocale(myLocale)
        res.updateConfiguration(conf, dm)
        views.title.text = getString(R.string.app_name)
        updateLanguage(lang)
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.popup_window, null)
        val popup = PopupWindow(
            view,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popup.elevation = 20F
        popup.setBackgroundDrawable(getDrawable(R.drawable.backgound_box))
        popup.showAsDropDown(v, 280, -140, Gravity.CENTER_HORIZONTAL)
        view.findViewById<LinearLayout>(R.id.to_lang_en).setOnClickListener {
            changeLanguage("en")
            adminViewModel.language = 0
            popup.dismiss()
            adminViewModel.handle(AdminViewAction.ResetLang)
        }
        view.findViewById<LinearLayout>(R.id.to_lang_vi).setOnClickListener {
            changeLanguage("vi")
            adminViewModel.language = 1
            popup.dismiss()
            adminViewModel.handle(AdminViewAction.ResetLang)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_admin)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (drawerLayout.isOpen)
                    drawerLayout.closeDrawer(GravityCompat.START)
                else if (
//                    navController.currentDestination?.id == R.id.nav_home_admin ||
                        navController.currentDestination?.id == R.id.adminTrackingFragment
                        || navController.currentDestination?.id == R.id.adminProjectFragment
                        || navController.currentDestination?.id == R.id.adminPersonnelFragment
                        || navController.currentDestination?.id == R.id.adminUsersFragment
                    )
                    drawerLayout.openDrawer(GravityCompat.START)
                else{
                    navController.navigateUp()
                    views.title.text = getString(R.string.app_name)
                }
                return true
            }

//            R.id.menu_list_refresh -> {
//                return true
//            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        views.title.text = getString(R.string.app_name)
    }

    private fun updateLanguage(lang: String) {
        val menu: Menu = navView.menu
        menu.findItem(R.id.nav_home_admin).title = getString(R.string.menu_home)
        menu.findItem(R.id.nav_timekeeping).title = getString(R.string.timekeeping)
        menu.findItem(R.id.nav_project).title = getString(R.string.project)
        menu.findItem(R.id.nav_personnel).title = getString(R.string.personnel)
        menu.findItem(R.id.nav_users).title = getString(R.string.users)

        views.title.text = when (navController.currentDestination?.id){
            R.id.adminProjectFragment -> getString(R.string.project)
            R.id.adminPersonnelFragment -> getString(R.string.personnel)
            R.id.adminUsersFragment -> getString(R.string.users)
            else -> getString(R.string.app_name)
        }

        menu.findItem(R.id.nav_change_language).title = if (lang == "en") getString(R.string.en) else getString(R.string.vi)
        menu.findItem(R.id.exit).title = getString(R.string.exit)
        menu.findItem(R.id.logout).title = getString(R.string.log_out)
    }
}


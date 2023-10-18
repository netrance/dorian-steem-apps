package lee.dorian.steem_ui

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import lee.dorian.steem_ui.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {

    override val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_tags, R.id.navigation_profile, R.id.navigation_wallet
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener(navDestinationChangedListener)

        setUpListeners()
    }

    private val navDestinationChangedListener = NavController.OnDestinationChangedListener { _, dest, _ ->
        supportActionBar?.setTitle(viewModel.readTitle(dest.id))
        setViewVisibilities(dest.id)
    }

    // Added to allow back button on tool bar of PostFragment to be clicked
    // Reference - https://stackoverflow.com/questions/59834398/android-navigation-component-back-button-not-working
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun setViewVisibilities(destID: Int) {
        val layoutTagLookup = binding.includeTagLookup.layoutTagLookup
        val layoutAccountLookup = binding.includeAccountLookup.layoutAccountLookup

        binding.navView.visibility = View.VISIBLE
        when (destID) {
            R.id.navigation_tags -> {
                layoutTagLookup.visibility = View.VISIBLE
                layoutAccountLookup.visibility = View.GONE
            }
            R.id.navigation_profile, R.id.navigation_wallet -> {
                layoutTagLookup.visibility = View.GONE
                layoutAccountLookup.visibility = View.VISIBLE
            }
            // Added R.id.navigation_post. Others will be added later. (15th Jun 2023)
            else ->  {
                layoutTagLookup.visibility = View.GONE
                layoutAccountLookup.visibility = View.GONE
                binding.navView.visibility = View.GONE
            }
        }
    }

    private fun setUpListeners() {
        binding.run {
            includeTagLookup.buttonTagSearch.setOnClickListener(buttonTagSearchClickListener)
            includeAccountLookup.buttonAccountSearch.setOnClickListener(buttonAccountSearchClickListener)
        }
    }

    private val buttonTagSearchClickListener = View.OnClickListener {
        val tag = binding.includeTagLookup.editSteemitTag.text.toString()
        if (tag.isNotEmpty()) {
            viewModel.currentTag.value = binding.includeTagLookup.editSteemitTag.text.toString()
        }
    }

    private val buttonAccountSearchClickListener = View.OnClickListener {
        val account = binding.includeAccountLookup.editSteemitAccount.text.toString()
        if (account.length > 2) {
            viewModel.currentAccount.value = account
        }
    }

}
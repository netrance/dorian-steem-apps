package lee.dorian.steem_ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.View.OnKeyListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import lee.dorian.dorian_android_ktx.android.context.hideKeyboard
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

    private val navDestinationChangedListener = NavController.OnDestinationChangedListener { _, dest, args ->
        supportActionBar?.setTitle(viewModel.readTitle(dest.id))
        setViewVisibilities(dest.id, args ?: Bundle())
    }

    // Added to allow back button on tool bar of PostFragment to be clicked
    // Reference - https://stackoverflow.com/questions/59834398/android-navigation-component-back-button-not-working
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun setViewVisibilities(destID: Int, args: Bundle) {
        val layoutTagLookup = binding.includeTagLookup.layoutTagLookup

        binding.navView.visibility = View.VISIBLE
        when (destID) {
            R.id.navigation_tags -> {
                layoutTagLookup.visibility = View.VISIBLE
            }
            R.id.navigation_profile, R.id.navigation_wallet -> {
                val account = args.getString("account", "")
                layoutTagLookup.visibility = View.GONE
                binding.navView.visibility = when {
                    account.isEmpty() -> View.VISIBLE
                    else -> View.GONE
                }
            }
            // Added R.id.navigation_post. Others will be added later. (15th Jun 2023)
            else ->  {
                layoutTagLookup.visibility = View.GONE
                binding.navView.visibility = View.GONE
            }
        }
    }

    private fun setUpListeners() {
        binding.run {
            includeTagLookup.editSteemitTag.setOnKeyListener(editSteemitTagKeyListener)
            includeTagLookup.buttonTagSearch.setOnClickListener(buttonTagSearchClickListener)
        }
    }

    private val editSteemitTagKeyListener = OnKeyListener { view, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            binding.includeTagLookup.buttonTagSearch.performClick()
            true
        }
        false
    }

    private val buttonTagSearchClickListener = View.OnClickListener {
        val tag = binding.includeTagLookup.editSteemitTag.text.toString()
        if (tag.isNotEmpty()) {
            viewModel.currentTag.value = binding.includeTagLookup.editSteemitTag.text.toString()
        }
        hideKeyboard(it)
    }

}
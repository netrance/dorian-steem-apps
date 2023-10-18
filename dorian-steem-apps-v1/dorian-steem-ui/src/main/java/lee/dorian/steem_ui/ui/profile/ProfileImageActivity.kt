package lee.dorian.steem_ui.ui.profile

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import lee.dorian.steem_ui.BaseActivity
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.ActivityProfileImageBinding
import lee.dorian.steem_ui.databinding.ActivityVoteListBinding
import lee.dorian.steem_ui.ext.load

class ProfileImageActivity : BaseActivity<ActivityProfileImageBinding, ProfileImageViewModel>(R.layout.activity_profile_image) {

    companion object {
        const val INTENT_BUNDLE_STEEMIT_ACCOUNT = "profile_image"
    }

    override val viewModel: ProfileImageViewModel by lazy {
        ViewModelProvider(this).get(ProfileImageViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.lifecycleOwner = this@ProfileImageActivity
        viewModel.steemitAccount.apply {
            observe(this@ProfileImageActivity, steemitAccountObserver)
            value = intent.getStringExtra(INTENT_BUNDLE_STEEMIT_ACCOUNT) ?: ""
        }

        supportActionBar?.title = "@${viewModel.steemitAccount.value}"
    }

    private val steemitAccountObserver = Observer<String> {
        when (it.isEmpty()) {
            true -> binding.imageProfile.setImageResource(lee.dorian.dorian_android_ktx.R.drawable.no_image_available)
            else -> binding.imageProfile.load("https://steemitimages.com/u/${it}/avatar/large")
        }
    }

}
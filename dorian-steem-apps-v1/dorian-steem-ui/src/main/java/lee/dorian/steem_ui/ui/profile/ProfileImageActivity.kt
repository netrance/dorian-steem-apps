package lee.dorian.steem_ui.ui.profile

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import lee.dorian.steem_ui.BaseActivity
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.databinding.ActivityProfileImageBinding
import lee.dorian.steem_ui.databinding.ActivityVoteListBinding

class ProfileImageActivity : BaseActivity<ActivityProfileImageBinding, ProfileImageViewModel>(R.layout.activity_profile_image) {

    companion object {
        const val INTENT_BUNDLE_STEEMIT_ACCOUNT = "profile_image"
    }

    override val viewModel: ProfileImageViewModel by lazy {
        ViewModelProvider(this).get(ProfileImageViewModel::class.java).apply {
            steemitAccount.value = intent.getStringExtra(INTENT_BUNDLE_STEEMIT_ACCOUNT) ?: ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        supportActionBar?.title = "@${viewModel.steemitAccount.value}"
    }

}
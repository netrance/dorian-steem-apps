package lee.dorian.steem_ui.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import lee.dorian.steem_domain.model.SteemitProfile
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.R
import lee.dorian.steem_ui.ext.setActivityActionBarTitle
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.compose.AccountInputForm
import lee.dorian.steem_ui.ui.compose.CustomTextField
import lee.dorian.steem_ui.ui.compose.ErrorOrFailure
import lee.dorian.steem_ui.ui.compose.GetCurrentFragment
import lee.dorian.steem_ui.ui.compose.Loading

class ProfileFragment : Fragment() {

    private val args: ProfileFragmentArgs by navArgs()

    val viewModel by lazy {
        ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    val activityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ProfileScreen(viewModel)
            }

            if (args.account.isNotEmpty() and (viewModel.profileState.value is State.Empty)) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.readSteemitProfile(args.account)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        val state = viewModel.profileState.value
        setActivityActionBarTitle(when (state) {
            is State.Success -> "Profile of @${state.data.account}"
            else -> "Profile"
        })
    }

}

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val state by viewModel.profileState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .verticalScroll(rememberScrollState())
    ) {
        AccountInputForm("Input a Steemit account.") { account ->
            if (account.length > 2)
            viewModel.readSteemitProfile(account)
        }

        val commonModifier = Modifier.fillMaxWidth().weight(1f).background(Color.White)
        when (state) {
            is State.Empty -> ProfileEmpty(modifier = commonModifier)
            is State.Loading -> Loading(modifier = commonModifier)
            !is State.Success -> ErrorOrFailure()
            else -> {
                val profile = (state as State.Success<SteemitProfile>).data
                ProfileContent(profile)
                ProfileMenu(profile)
            }
        }
    }
}

@Composable
@Preview
fun ProfileScreenPreview() {
    val viewModel = ProfileViewModel(State.Success(sampleSteemitProfile))
    ProfileScreen(viewModel)
}

private val profileContentTextStyle = TextStyle(
    color = Color.White,
    fontSize = 20.sp
)

@Composable
fun ProfileEmpty(modifier: Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            text = "Input a Steemit account.",
            style = TextStyle(
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
@Preview
fun ProfileEmptyPreview() {
    ProfileEmpty(modifier = Modifier.fillMaxSize().background(Color.White))
}

@Composable
fun ProfileContent(profile: SteemitProfile) {
    Box(modifier = Modifier.fillMaxWidth()) {
        ProfileContentBackground(profile.coverImageURL, Modifier.matchParentSize())
        ProfileContentText(profile)
    }
}

@Composable
fun ProfileContentBackground(imageURL: String, modifier: Modifier) {
    AsyncImage(
        model = imageURL,
        contentDescription = "",
        contentScale = ContentScale.FillHeight,
        modifier = modifier
    )
}

@Composable
fun ProfileContentText(profile: SteemitProfile) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 30.dp)
    ) {
        // Profile image, account and name
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = "https://steemitimages.com/u/${profile.account}/avatar/small",
                contentDescription = "",
                modifier = Modifier.width(80.dp).height(80.dp).clip(CircleShape)
            )
            Column(
                modifier = Modifier.height(80.dp).padding(start = 20.dp).wrapContentHeight()
            ) {
                Text(
                    text = "@${profile.account}",
                    style = TextStyle(
                        color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold
                    )
                )
                if (profile.name.isNotEmpty()) {
                    Text(
                        text = "${profile.name}",
                        style = TextStyle(color = Color.White, fontSize = 18.sp),
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }
            }
        }

        // Following and Followers
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        ) {
            Text(
                text = "${profile.followerCount} Following",
                style = profileContentTextStyle
            )
            Text(
                text = "${profile.followerCount} Followers",
                style = profileContentTextStyle,
                modifier = Modifier.padding(start = 30.dp)
            )
        }

        // About
        Text(
            text = "${profile.about}",
            textAlign = TextAlign.Center,
            style = profileContentTextStyle,
            modifier = Modifier.padding(top = 16.dp)
        )

        // Location
        Text(
            text = getLocationText(profile.location),
            textAlign = TextAlign.Center,
            style = profileContentTextStyle,
            modifier = Modifier.padding(top = 16.dp)
        )

        // Web site
        BasicText(
            text = getWebsiteText(profile.website),
            //textAlign = TextAlign.Center,
            style = profileContentTextStyle,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

private fun getLocationText(about: String) = when {
    about.isEmpty() -> ""
    else -> "\uD83D\uDCCD ${about}"
}

private fun getWebsiteText(website: String): AnnotatedString = buildAnnotatedString {
    when {
        website.isEmpty() -> append("")
        else -> {
            append("\uD83D\uDD17 ")
            withLink(
                LinkAnnotation.Url(
                    website,
                    TextLinkStyles(style = SpanStyle(color = Color.Blue))
                )
            ) {
                append(website)
            }
        }
    }
}

@Composable
@Preview
fun ProfileContentPreview() {
    ProfileContent(sampleSteemitProfile)
}

@Composable
fun ProfileMenu(profile: SteemitProfile) {
    val fragment = GetCurrentFragment(R.id.nav_host_fragment_activity_main)
    Column(modifier = Modifier.fillMaxWidth()) {
        ProfileMenuRow(
            ProfileMenuItem("Details", Color.Black, 18, Color.White) {
                val action = ProfileFragmentDirections.actionNavigationProfileToNavigationAccountDetails(profile.account)
                fragment?.findNavController()?.navigate(action)
                fragment?.setActivityActionBarTitle("Details of @${profile.account}")
            },
            ProfileMenuItem("Blog", Color.White, 18, Color.Black) {
                val action = ProfileFragmentDirections.actionNavigationProfileToNavigationPostList(profile.account, "blog")
                fragment?.findNavController()?.navigate(action)
                fragment?.setActivityActionBarTitle("Blog of @${profile.account}")
            },
            ProfileMenuItem("Posts", Color.Black, 18, Color.White) {
                val action = ProfileFragmentDirections.actionNavigationProfileToNavigationPostList(profile.account, "posts")
                fragment?.findNavController()?.navigate(action)
                fragment?.setActivityActionBarTitle("Posts of @${profile.account}")
            }
        )
        ProfileMenuRow(
            ProfileMenuItem("Comments", Color.White, 18, Color.Black) {
                val action = ProfileFragmentDirections.actionNavigationProfileToNavigationPostList(profile.account, "comments")
                fragment?.findNavController()?.navigate(action)
                fragment?.setActivityActionBarTitle("Comments of @${profile.account}")
            },
            ProfileMenuItem("Replies", Color.Black, 18, Color.White) {
                val action = ProfileFragmentDirections.actionNavigationProfileToNavigationPostList(profile.account, "replies")
                fragment?.findNavController()?.navigate(action)
                fragment?.setActivityActionBarTitle("Replies of @${profile.account}")
            },
            ProfileMenuItem("History", Color.White, 18, Color.Black) {
                val action = ProfileFragmentDirections.actionNavigationProfileToNavigationAccountHistory(profile.account)
                fragment?.findNavController()?.navigate(action)
                fragment?.setActivityActionBarTitle("History of @${profile.account}")
            }
        )
    }
}

@Composable
@Preview
fun ProfileMenuPreview() {
    ProfileMenu(sampleSteemitProfile)
}

@Composable
fun ProfileMenuRow(
    menuItem1: ProfileMenuItem,
    menuItem2: ProfileMenuItem,
    menuItem3: ProfileMenuItem
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        ProfileMenuCell(menuItem1, this)
        ProfileMenuCell(menuItem2, this)
        ProfileMenuCell(menuItem3, this)
    }
}

@Composable
fun ProfileMenuCell(
    menuItem: ProfileMenuItem,
    rowScope: RowScope,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = rowScope.getMenuCellModifier(menuItem.backgroundColor).clickable {
            menuItem.onClick()
        }
    ) {
        Text(
            text = menuItem.name,
            style = getMenuTextStyle(menuItem.textColor, menuItem.fontSize),
            textAlign = TextAlign.Center
        )
    }

}

private fun getMenuTextStyle(textColor: Color, fontSize: Int): TextStyle {
    return TextStyle(
        color = textColor,
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Bold
    )
}

private fun RowScope.getMenuCellModifier(backgroundColor: Color): Modifier {
    return Modifier
        .weight(1f)
        .size(width = Dp.Unspecified, 100.dp)
        .background(backgroundColor)
}

private val sampleSteemitProfile by lazy {
    SteemitProfile(
        account = "dorian-mobileapp",
        name = "Dorian as Mobile App Developer",
        about = "...",
        followingCount = 100,
        followerCount = 100,
        location = "Seoul, Korea",
        website = "www.steemit.com",
        coverImageURL = "https://cdn.steemitimages.com/DQmUtGtQZGWnosZZrGsC2Dm9Xv8rc7AzomX2ajBKjKwEGcz/photo_2020-07-26%2019.13.27.jpeg"
    )
}
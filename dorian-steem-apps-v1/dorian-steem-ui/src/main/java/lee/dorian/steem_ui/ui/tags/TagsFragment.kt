package lee.dorian.steem_ui.ui.tags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import lee.dorian.dorian_android_ktx.androidx.compose.foundation.lazy.AppendableLazyColumn
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_domain.usecase.ReadRankedPostsUseCase
import lee.dorian.steem_ui.MainViewModel
import lee.dorian.steem_ui.model.State
import lee.dorian.steem_ui.ui.compose.ErrorOrFailure
import lee.dorian.steem_ui.ui.compose.Loading
import lee.dorian.steem_ui.ui.compose.TagInputForm
import lee.dorian.steem_ui.ui.post.PostListItem
import lee.dorian.steem_ui.ui.post.onDownvoteClick
import lee.dorian.steem_ui.ui.post.onPostListItemImageClick
import lee.dorian.steem_ui.ui.post.onPostListItemClick
import lee.dorian.steem_ui.ui.post.onUpvoteClick
import lee.dorian.steem_ui.ui.preview.samplePostItem

@AndroidEntryPoint
class TagsFragment : Fragment() {

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
                TagsScreen()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}

private val tagInfoList = listOf(
    TagScreenSortTabInfo("Trending", "trending"),
    TagScreenSortTabInfo("Created", "created"),
    TagScreenSortTabInfo("Payout", "payout")
)

@Composable
fun TagsScreen(
    viewModel: TagsViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var tag by rememberSaveable { mutableStateOf("") }
    var sort by rememberSaveable { mutableStateOf(tagInfoList[0].sort) }
    var isFirstStart by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TagInputForm(placeholder = "Input a tag.") { newTag ->
            tag = newTag
            viewModel.readRankedPosts(tag, sort)
            keyboardController?.hide()
        }
        TagsSortTabRow { tagTabInfo ->
            sort = tagTabInfo.sort
            viewModel.readRankedPosts(tag, sort)
        }
        TagsContent(
            viewModel,
            onAppend = {
                viewModel.appendRankedPosts(tag, sort)
            },
            Modifier.fillMaxWidth().weight(1f)
        )
    }

    if (isFirstStart or viewModel.isContentEmpty()) {
        viewModel.readRankedPosts(tag, sort)
        isFirstStart = false
    }
}

@Composable
@Preview
fun TagsScreenPreview() {
    TagsScreen(
        viewModel = TagsViewModel(
            ReadRankedPostsUseCase(SteemRepositoryImpl(Dispatchers.IO), Dispatchers.IO)
        )
    )
}

@Composable
fun TagsContent(viewModel: TagsViewModel, onAppend: () -> Unit, modifier: Modifier) {
    val state by viewModel.flowTagsState.collectAsStateWithLifecycle()

    if (state is State.Loading) {
        Loading()
        return
    }
    else if (state !is State.Success) {
        ErrorOrFailure()
        return
    }

    val tagPostList = (state as State.Success).data
    TagsPostList(
        tagPostList,
        viewModel,
        onAppend = onAppend
    )
}

@Composable
@Preview
fun TagsContentPreview() {
    TagsContent(
        viewModel = TagsViewModel(
            ReadRankedPostsUseCase(SteemRepositoryImpl(Dispatchers.IO), Dispatchers.IO)
        ),
        onAppend = {},
        Modifier.fillMaxWidth()
    )
}

@Composable
fun TagsSortTabRow(
    onTabSelected: (TagScreenSortTabInfo) -> Unit,
) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.White,
        contentColor = Color.Black,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = Color.Black
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        tagInfoList.forEachIndexed { index, tagInfo ->
            Tab(
                selected = (selectedTabIndex == index),
                onClick = {
                    selectedTabIndex = index
                    onTabSelected(tagInfoList[index])
                },
                text = { Text(text = tagInfo.title) }
            )
        }
    }
}

@Composable
@Preview
fun TagsSortTabRowPreview() {
    TagsSortTabRow {}
}

@Composable
fun TagsPostList(postList: List<PostItem>, viewModel: TagsViewModel, onAppend: () -> Unit) {
    AppendableLazyColumn(
        onAppend = onAppend
    ) {
        items(postList.size) { index ->
            PostListItem(
                postList[index],
                ::onPostListItemClick,
                ::onPostListItemImageClick,
                ::onUpvoteClick,
                ::onDownvoteClick
            )
        }
    }
}

@Composable
@Preview
fun TagsPostListPreview() {
    TagsPostList(
        postList = listOf(samplePostItem, samplePostItem, samplePostItem),
        viewModel = TagsViewModel(
            ReadRankedPostsUseCase(SteemRepositoryImpl(Dispatchers.IO), Dispatchers.IO)
        ),
        onAppend = {}
    )
}
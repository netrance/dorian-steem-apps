package lee.dorian.steem_ui.ui.voter

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint
import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_ui.ui.compose.CustomTopAppBar
import lee.dorian.steem_ui.ui.profile.ProfileImageActivity

@AndroidEntryPoint
class VoteListActivity : ComponentActivity() {

    companion object {
        const val INTENT_BUNDLE_VOTER_LIST = "voter_list"
    }

    private val viewModel: VoteListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = android.graphics.Color.TRANSPARENT
            )
        )

        val voterArrayList = intent.getSerializableExtra(INTENT_BUNDLE_VOTER_LIST) as ArrayList<ActiveVote>
        viewModel.setVotes(voterArrayList)

        setContent {
            VoteListScreen(
                viewModel = viewModel,
                onProfileImageClick = { account ->
                    startActivity(Intent(this, ProfileImageActivity::class.java).apply {
                        putExtra(ProfileImageActivity.INTENT_BUNDLE_STEEMIT_ACCOUNT, account)
                    })
                }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoteListScreen(
    viewModel: VoteListViewModel = hiltViewModel(),
    onProfileImageClick: (String) -> Unit
) {
    val votes by viewModel.votes.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val filteredVotes = remember(votes, searchQuery) {
        if (searchQuery.isEmpty()) votes
        else votes.filter { it.voter.contains(searchQuery) }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(title = "Vote List")
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Input Steemit account.") },
                trailingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedIndicatorColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredVotes) { vote ->
                    VoteItem(vote, onProfileImageClick)
                }
            }
        }
    }
}

@Composable
fun VoteItem(
    vote: ActiveVote,
    onProfileImageClick: (String) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            AsyncImage(
                model = "https://steemitimages.com/u/${vote.voter}/avatar/small",
                contentDescription = "Profile image of ${vote.voter}",
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .clickable { onProfileImageClick(vote.voter) }
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = vote.voter,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(10.dp))
            if (!vote.weight.isNaN()) {
                Text(
                    text = vote.readWeightAsPercent(),
                    color = Color.Black,
                    fontSize = 15.sp
                )
                Spacer(Modifier.width(10.dp))
            }
            if (!vote.value.isNaN()) {
                Text(
                    text = vote.readValueAsSTU(),
                    color = Color.Black,
                    fontSize = 15.sp
                )
            }
        }
        HorizontalDivider(color = Color.DarkGray, thickness = 1.dp)
    }
}

@Composable
@Preview(showBackground = true)
fun VoteItemPreview() {
    VoteItem(
        vote = ActiveVote("dorian-mobileapp", 10000f, 1.234f),
        onProfileImageClick = {}
    )
}

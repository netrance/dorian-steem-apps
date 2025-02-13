package lee.dorian.steem_ui.ui.post.content

import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_domain.model.Post

val postForTest = Post(
    "Title For Test",
    "https://cdn.steemitimages.com/DQmdGQpHV23GagfH4teLgwfGCRahgbioTZBBj23axEZgpdA/image.png",
    listOf("tag1", "tag2", "tag3", "tag4", "tag5"),
    listOf(),
    "DorianSteemApp",
    "hive-XXXXXX",
    "Community Example",
    "Content sample......",
    "2345-01-23 12:34:56",
    0,
    100.100f,
    100.100f,
    0f,
    false,
    "2345-01-30 12:34:56",
    3,
    0,
    listOf(
        ActiveVote("voter-a", 100f, 50.05f),
        ActiveVote("voter-b", 50f, 25.025f),
        ActiveVote("voter-c", 50f, 25.025f),
    ),
    0f,
    "account",
    25,
    "permlink-sample"
)

val replyListForTest = listOf(
    postForTest,
    postForTest,
    postForTest,
    postForTest,
    postForTest
)
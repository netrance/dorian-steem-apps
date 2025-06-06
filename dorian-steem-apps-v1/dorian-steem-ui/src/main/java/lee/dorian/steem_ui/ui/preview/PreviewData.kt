package lee.dorian.steem_ui.ui.preview

import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_domain.model.Post
import lee.dorian.steem_domain.model.PostItem

val samplePostItem by lazy {
    PostItem(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit...",
        "thumbnail url",
        listOf("image url1", "image url2", "image url3"),
        "content",
        "tar or community",
        "2025-01-23 12:34:56",
        987.654f,
        1,
        0,
        listOf(ActiveVote("voter1", 100f, 987.654f)),
        "author",
        88,
        "testpermlink"
    )
}

val postForTest by lazy {
    Post(
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
}

val replyListForTest by lazy {
    listOf(
        postForTest,
        postForTest,
        postForTest,
        postForTest,
        postForTest
    )
}
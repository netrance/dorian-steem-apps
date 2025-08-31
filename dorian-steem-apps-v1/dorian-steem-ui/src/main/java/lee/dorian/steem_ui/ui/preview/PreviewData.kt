package lee.dorian.steem_ui.ui.preview

import lee.dorian.steem_domain.model.ActiveVote
import lee.dorian.steem_domain.model.Post
import lee.dorian.steem_domain.model.PostItem

val samplePostItem by lazy {
    PostItem(
        title = "Lorem ipsum dolor sit amet, consectetur adipiscing elit...",
        thumbnailURL = "thumbnail url",
        imageURLs = listOf("image url1", "image url2", "image url3"),
        content = "content",
        tagOrCommunity = "tar or community",
        time = "2025-01-23 12:34:56",
        rewards = 987.654f,
        upvoteCount = 1,
        downvoteCount = 0,
        replyCount = 0,
        activeVotes = listOf(ActiveVote("voter1", 100f, 987.654f)),
        account = "author",
        reputation = 88,
        permlink = "testpermlink"
    )
}

val postForTest by lazy {
    Post(
        title = "Title For Test",
        thumbnailURL = "https://cdn.steemitimages.com/DQmdGQpHV23GagfH4teLgwfGCRahgbioTZBBj23axEZgpdA/image.png",
        tags = listOf("tag1", "tag2", "tag3", "tag4", "tag5"),
        imageURLs = listOf(),
        app = "DorianSteemApp",
        communityTag = "hive-XXXXXX",
        communityTitle = "Community Example",
        content = "Content sample......",
        time = "2345-01-23 12:34:56",
        depth = 0,
        rewards = 100.100f,
        authorRewards = 100.100f,
        curationRewards = 0f,
        isPaidout = false,
        paidoutTime = "2345-01-30 12:34:56",
        upvoteCount = 3,
        downvoteCount = 0,
        activeVotes = listOf(
            ActiveVote("voter-a", 100f, 50.05f),
            ActiveVote("voter-b", 50f, 25.025f),
            ActiveVote("voter-c", 50f, 25.025f),
        ),
        promoted = 0f,
        account = "account",
        reputation = 25,
        permlink = "permlink-sample"
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
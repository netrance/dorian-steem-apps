package lee.dorian.steem_ui.ui.history

import lee.dorian.dorian_ktx.read
import lee.dorian.steem_domain.model.AccountHistoryItem
import lee.dorian.steem_domain.model.AccountHistoryItemLink
import lee.dorian.steem_domain.model.DynamicGlobalProperties
import lee.dorian.steem_domain.util.Converter

fun AccountHistoryItem.getLinkList(): List<AccountHistoryItemLink> {
    return when (type) {
        "author_reward" -> getAuthorRewardLinks()
        "comment" -> getCommentLinks()
        "comment_benefactor_reward" -> getCommentBenefactorRewardLinks()
        "curation_reward" -> getCurationRewardLinks()
        "transfer" -> getTransferLinks()
        "transfer_to_vesting" -> getTransferToVestingLinks()
        "vote" -> getVoteLinks()
        else -> listOf()
    }
}

fun AccountHistoryItem.getUserReadableContent(dynamicGlobalProperties: DynamicGlobalProperties? = null): String {
    return when (type) {
        "author_reward" -> getAuthorRewardContent(dynamicGlobalProperties)
        "claim_reward_balance" -> getClaimRewardBalance(dynamicGlobalProperties)
        "comment" -> getCommentContent()
        "comment_benefactor_reward" -> getCommentBenefactorRewardContent(dynamicGlobalProperties)
        "curation_reward" -> getCurationRewardContent(dynamicGlobalProperties)
        "producer_reward" -> getProducerRewardContent()
        "transfer" -> getTransferContent()
        "transfer_to_vesting" -> getTransferToVestingContent()
        "vote" -> getVoteContent()
        else -> content.toString()
    }
}

fun AccountHistoryItem.getAuthorRewardContent(dynamicGlobalProperties: DynamicGlobalProperties? = null): String {
    if (type != "author_reward") {
        return ""
    }

    val author = content.read("author", "")
    val sbdPayout = content.read("sbd_payout", "0 SBD")
    val steemPayout = content.read("steem_payout", "0 STEEM")
    val vestingPayout = content.read("vesting_payout", "0 VEST")
    val spPayout = dynamicGlobalProperties?.let {
        val spReward = Converter.toSteemPowerFromVest(vestingPayout.replace(" VESTS", "").toFloat(), it)
        String.format("%.3f SP", spReward)
    } ?: vestingPayout
    val permlink = content.read("permlink", "")
    return "${sbdPayout}, ${steemPayout}, ${spPayout} from @${author}/${permlink}"
}

fun AccountHistoryItem.getAuthorRewardLinks(): List<AccountHistoryItemLink> {
    val author = content.read("author", "")
    val permlink = content.read("permlink", "")

    return listOf(
        AccountHistoryItemLink("profile", "@${author}", "@${author}"),
        AccountHistoryItemLink("post", "@${author}/${permlink}", "@${author}/${permlink}")
    )
}

fun AccountHistoryItem.getCommentBenefactorRewardContent(dynamicGlobalProperties: DynamicGlobalProperties? = null): String {
    if (type != "comment_benefactor_reward") {
        return ""
    }

    val author = content.read("author", "")
    val sbdPayout = content.read("sbd_payout", "0 SBD")
    val steemPayout = content.read("steem_payout", "0 STEEM")
    val vestingPayout = content.read("vesting_payout", "0 VEST")
    val spPayout = dynamicGlobalProperties?.let {
        val spReward = Converter.toSteemPowerFromVest(vestingPayout.replace(" VESTS", "").toFloat(), it)
        String.format("%.3f SP", spReward)
    } ?: vestingPayout
    val permlink = content.read("permlink", "")
    return "${sbdPayout}, ${steemPayout}, ${spPayout} from @${author}/${permlink}"
}

fun AccountHistoryItem.getCommentBenefactorRewardLinks(): List<AccountHistoryItemLink> {
    if (type != "comment_benefactor_reward") {
        return listOf()
    }

    val author = content.read("author", "")
    val permlink = content.read("permlink", "")

    return listOf(
        AccountHistoryItemLink("profile", "@${author}", "@${author}"),
        AccountHistoryItemLink("post", "@${author}/${permlink}", "@${author}/${permlink}")
    )
}

fun AccountHistoryItem.getCurationRewardContent(dynamicGlobalProperties: DynamicGlobalProperties? = null): String {
    if (type != "curation_reward") {
        return ""
    }

    val curator = content.read("curator", "")
    val commentAuthor = content.read("comment_author", "")
    val commentPermlink = content.read("comment_permlink", "")
    val vestReward = content.read("reward", "")
    val reward = dynamicGlobalProperties?.let {
        val spReward = Converter.toSteemPowerFromVest(vestReward.replace(" VESTS", "").toFloat(), it)
        String.format("%.3f SP", spReward)
    } ?: vestReward

    return "${reward} from @${commentAuthor}/${commentPermlink}"
}

fun AccountHistoryItem.getCurationRewardLinks(): List<AccountHistoryItemLink> {
    if (type != "curation_reward") {
        return listOf()
    }

    val commentAuthor = content.read("comment_author", "")
    val commentPermlink = content.read("comment_permlink", "")
    return listOf(
        AccountHistoryItemLink("profile", "@${commentAuthor}", "@${commentAuthor}"),
        AccountHistoryItemLink("post", "@${commentAuthor}/${commentPermlink}", "@${commentAuthor}/${commentPermlink}")
    )
}

fun AccountHistoryItem.getClaimRewardBalance(dynamicGlobalProperties: DynamicGlobalProperties? = null): String {
    if (type != "claim_reward_balance") {
        return ""
    }

    val account = content.read("account", "")
    val rewardSteem = content.read("reward_steem", "")
    val rewardSbd = content.read("reward_sbd", "")
    val rewardVests = content.read("reward_vests", "")
    val rewardSP = dynamicGlobalProperties?.let {
        val spReward = Converter.toSteemPowerFromVest(rewardVests.replace(" VESTS", "").toFloat(), it)
        String.format("%.3f SP", spReward)
    } ?: rewardVests
    return "Claimed ${rewardSteem}, ${rewardSbd}, ${rewardSP}"
}

fun AccountHistoryItem.getCommentContent(): String {
    if (type != "comment") {
        return ""
    }

    // val parentAuthor = content.read("parent_author", "")
    // val parentPermlink = content.read("parent_permlink", "")
    val author = content.read("author", "")
    val permlink = content.read("permlink", "")
    val title = content.read("title", "")
    // val body = content.read("body", "")
    // val jsonMetadata = content.read("json_metadata", "")
    return "@${author} commented @${author}/${permlink}${
        when (title.isNotEmpty()) {
            true -> "\n\n${title}"
            else -> ""
        }
    }"
}

fun AccountHistoryItem.getCommentLinks(): List<AccountHistoryItemLink> {
    if (type != "comment") {
        return listOf()
    }

    val author = content.read("author", "")
    val permlink = content.read("permlink", "")

    return listOf(
        AccountHistoryItemLink("profile", "@${author}", "@${author}"),
        AccountHistoryItemLink("post", "@${author}/${permlink}", "@${author}/${permlink}")
    )
}

fun AccountHistoryItem.getProducerRewardContent(): String {
    if (type != "producer_reward") {
        return ""
    }

    // val producer = content.read("producer", "")
    val vestingShares = content.read("vesting_shares", "")
    return "${vestingShares}"
}

fun AccountHistoryItem.getTransferContent(): String {
    if (type != "transfer") {
        return ""
    }

    val from = content.read("from", "")
    val to = content.read("to", "")
    val memo = content.read("memo", "")
    val amount = content.read("amount", "0.0 STEEM")
    return "@${from} to @${to}: ${amount}\n\n${memo}"
}

fun AccountHistoryItem.getTransferLinks(): List<AccountHistoryItemLink> {
    if (type != "transfer") {
        return listOf()
    }

    val from = content.read("from", "")
    val to = content.read("to", "")

    return listOf(
        AccountHistoryItemLink("wallet", "@${from}", "@${from}"),
        AccountHistoryItemLink("wallet", "@${to}", "@${to}")
    )
}

fun AccountHistoryItem.getTransferToVestingContent(): String {
    if (type != "transfer_to_vesting") {
        return ""
    }

    val from = content.read("from", "")
    val to = content.read("to", "")
    val amount = content.read("amount", "0.0 SP").replace("STEEM", "SP")
    return "Powered up ${amount} from @${from} to @${to}"
}

fun AccountHistoryItem.getTransferToVestingLinks(): List<AccountHistoryItemLink> {
    if (type != "transfer_to_vesting") {
        return listOf()
    }

    val from = content.read("from", "")
    val to = content.read("to", "")

    return listOf(
        AccountHistoryItemLink("wallet", "@${from}", "@${from}"),
        AccountHistoryItemLink("wallet", "@${to}", "@${to}")
    )
}

fun AccountHistoryItem.getVoteContent(): String {
    if (type != "vote") {
        return ""
    }

    val voter = content.read("voter", "")
    val author = content.read("author", "")
    val permlink = content.read("permlink", "")
    val weight = content.read("weight", 0.0) / 100.0f
    return "@${voter} votes @${author}/${permlink}. (${weight}%)"
}

fun AccountHistoryItem.getVoteLinks(): List<AccountHistoryItemLink> {
    if (type != "vote") {
        return listOf()
    }

    val voter = content.read("voter", "")
    val author = content.read("author", "")
    val permlink = content.read("permlink", "")

    return listOf(
        AccountHistoryItemLink("profile", "@${voter}", "@${voter}"),
        AccountHistoryItemLink("profile", "@${author}", "@${author}"),
        AccountHistoryItemLink("post", "@${author}/${permlink}", "@${author}/${permlink}")
    )
}
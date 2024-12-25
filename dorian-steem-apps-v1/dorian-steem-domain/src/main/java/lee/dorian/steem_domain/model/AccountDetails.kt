package lee.dorian.steem_domain.model

data class AccountDetails(
    // account info
    val id: String = "",
    val name: String = "",
    val recoveryAccount: String = "",       // recovery_account
    val lastAccountRecovery: String = "",   // last_account_revocery
    val lastOwnerUpdate: String = "",       //  last_owner_update
    val lastAccountUpdate: String = "",     // last_account_update
    // asset info
    val steemBalance: String = "",          // balance
    val sbdBalance: String = "",            // sbd_balance
    val steemPower: String = "",            // vesting_shares
    val delegatedSteemPower: String = "",   // delegated_vesting_shares
    val receivedSteemPower: String = "",    // received_vesting_shares
    // rewards info
    val pendingSBDRewards: String = "",     // reward_sbd_balance
    val pendingSteemRewards: String = "",   // reward_steem_balance
    val pendingSPBalance: String = "",      // reward_vesting_balance
    // activity info
    val postCount: Int = 0,           // post_count
    val lastPost: String = "",        // last_post
    val lastRootPost: String = "",    // last_root_post
    // voting info
    val canVote: Boolean = false,     // can_vote
    val lastVoteTime: String = "",    // last_vote_time
    val votingPower: Float = 0f,      // voting_power
    val votingManaRate: Float = 0f,
    val downvotignManaRate: Float = 0f,
    // witness
    val witnessVotes: List<String> = listOf(),   // witness_votes
    val proxy: String = "",
    // others
    val reputation: String = "",
)

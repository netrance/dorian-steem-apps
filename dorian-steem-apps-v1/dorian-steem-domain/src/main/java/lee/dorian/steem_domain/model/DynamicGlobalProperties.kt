package lee.dorian.steem_domain.model

data class DynamicGlobalProperties(
    val total_vesting_fund_steem: String,
    val total_vesting_shares: String,
    val total_reward_fund_steem: String,
    val total_reward_shares2: String,
    val pending_rewarded_vesting_shares: String,
    val pending_rewarded_vesting_steem: String,
)

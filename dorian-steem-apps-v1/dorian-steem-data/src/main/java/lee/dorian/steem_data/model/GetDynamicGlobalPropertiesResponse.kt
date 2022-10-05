package lee.dorian.steem_data.model

data class GetDynamicGlobalPropertiesResponseDTO(
    val jsonrpc: String?,
    val result: GetDynamicGlobalPropertiesDTO?,
    val id: Int?
)

data class GetDynamicGlobalPropertiesDTO(
    val head_block_number: Int?,
    val head_block_id: String?,
    val time: String?,
    val current_witness: String?,
    val total_pow: Int?,
    val num_pow_witnesses: Int?,
    val virtual_supply: String?,
    val current_supply: String?,
    val confidential_supply: String?,
    val init_sbd_supply: String?,
    val current_sbd_supply: String?,
    val confidential_sbd_supply: String?,
    val total_vesting_fund_steem: String?,
    val total_vesting_shares: String?,
    val total_reward_fund_steem: String?,
    val total_reward_shares2: String?,
    val pending_rewarded_vesting_shares: String?,
    val pending_rewarded_vesting_steem: String?,
    val sbd_interest_rate: Int?,
    val sbd_print_rate: Int?,
    val maximum_block_size: Int?,
    val required_actions_partition_percent: Int?,
    val current_aslot: Int?,
    val recent_slots_filled: String?,
    val participation_count: Int?,
    val last_irreversible_block_num: Int?,
    val vote_power_reserve_rate: Int?,
    val delegation_return_period: Int?,
    val reverse_auction_seconds: Int?,
    val available_account_subsidies: Int?,
    val sbd_stop_percent: Int?,
    val sbd_start_percent: Int?,
    val next_maintenance_time: String?,
    val last_budget_time: String?,
    val content_reward_percent: Int?,
    val vesting_reward_percent: Int?,
    val sps_fund_percent: Int?,
    val sps_interval_ledger: String?,
    val downvote_pool_percent: Int?
)
package lee.dorian.steem_data.model

import lee.dorian.steem_domain.model.SteemitWallet
import java.lang.NumberFormatException

data class GetAccountsResponseDTO(
    val jsonrpc: String?,
    val result: Array<SteemitAccountDTO>?,
    val id: Int?
)

data class SteemitAccountDTO(
    val id: String?,
    val name: String?,
    val owner: SteemitKeyDTO?,
    val active: SteemitKeyDTO?,
    val posting: SteemitKeyDTO?,
    val memo_key: String?,
    val json_metadata: String?,
    val posting_json_metadata: String?,
    val proxy: String?,
    val last_owner_update: String?,
    val last_account_update: String?,
    val created: String?,
    val mined: Boolean?,
    val recovery_account: String?,
    val last_account_recovery: String?,
    val reset_account: String?,
    val comment_count: Int?,
    val lifetime_vote_count: Int?,
    val post_count: Int?,
    val can_vote: Boolean?,
    val voting_manabar: ManabarDTO?,
    val downvote_manabar: ManabarDTO?,
    val voting_power: Int?,
    val balance: String?,
    val savings_balance: String?,
    val sbd_balance: String?,
    val sbd_seconds: String?,
    val sbd_seconds_last_update: String?,
    val sbd_last_interest_payment: String?,
    val savings_sbd_balance: String?,
    val savings_sbd_seconds: String?,
    val savings_sbd_seconds_last_update: String?,
    val savings_sbd_last_interest_payment: String?,
    val savings_withdraw_requests: Int?,
    val reward_sbd_balance: String?,
    val reward_steem_balance: String?,
    val reward_vesting_balance: String?,
    val reward_vesting_steem: String?,
    val vesting_shares: String?,
    val delegated_vesting_shares: String?,
    val received_vesting_shares: String?,
    val vesting_withdraw_rate: String?,
    val next_vesting_withdrawal: String?,
    val withdrawn: String?,
    val to_withdraw: String?,
    val withdraw_routes: Int?,
    val curation_rewards: Long?,
    val posting_rewards: Long?,
    val proxied_vsf_votes: Array<Any>?,
    val witnesses_voted_for: Int?,
    val last_post: String?,
    val last_root_post: String?,
    val last_vote_time: String?,
    val post_bandwidth: Int?,
    val pending_claimed_accounts: Int?,
    val vesting_balance: String?,
    val reputation: String?,
    val transfer_history: Array<Any>?,
    val market_history: Array<Any>?,
    val post_history: Array<Any>?,
    val vote_history: Array<Any>?,
    val other_history: Array<Any>?,
    val witness_votes: Array<Any>?,
    val tags_usage: Array<Any>?,
    val guest_bloggers: Array<Any>?
) {

    fun toSteemitWallet(): SteemitWallet {
        val spToBeWithdrawn = try {
            "${to_withdraw?.toDouble() ?: 0} VESTS"
        }
        catch (e: NumberFormatException) {
            "0 VESTS"
        }

        return SteemitWallet(
            name ?: "",
            balance ?: "0 STEEM",
            sbd_balance ?: "0 SBD",
            savings_balance ?: "0 STEEM",
            savings_sbd_balance ?: "0 SBD",
            vesting_shares ?: "0 VESTS",
            delegated_vesting_shares ?: "0 VESTS",
            received_vesting_shares ?: "0 VESTS",
            vesting_withdraw_rate ?: "0 VESTS",
            spToBeWithdrawn
        )
    }

}

data class SteemitKeyDTO(
    val weight_threshold: Int?,
    val account_auths: Array<Any>?,
    val key_auths: Array<Array<Any>>?
)

data class ManabarDTO(
    val current_mana: String?,
    val last_update_time: Long?
)
package lee.dorian.steem_data.model

import com.google.gson.JsonObject
import lee.dorian.steem_data.model.follow.GetFollowCountResponseDTO
import lee.dorian.steem_domain.ext.convertToUserReadableReputation
import lee.dorian.steem_domain.ext.removeSubstring
import lee.dorian.steem_domain.ext.toJsonObject
import lee.dorian.steem_domain.model.AccountDetails
import lee.dorian.steem_domain.model.SteemitProfile
import lee.dorian.steem_domain.model.SteemitWallet
import lee.dorian.steem_domain.util.Converter
import java.lang.NumberFormatException
import java.text.DecimalFormat

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
    val transfer_history: List<Any>?,
    val market_history: List<Any>?,
    val post_history: List<Any>?,
    val vote_history: List<Any>?,
    val other_history: List<Any>?,
    val witness_votes: List<String>?,
    val tags_usage: List<Any>?,
    val guest_bloggers: List<Any>?
) {

    fun toSteemitProfile(followCountResponseDTO: GetFollowCountResponseDTO): SteemitProfile {
        val account = this.name ?: ""
        val followingCount = followCountResponseDTO.result.following_count
        val folowerCount = followCountResponseDTO.result.follower_count

        val jsonMetadata = when {
            (null != this.posting_json_metadata) -> this.posting_json_metadata?.toJsonObject() ?: JsonObject()
            else -> json_metadata?.toJsonObject() ?: JsonObject()
        }
        val jsonProfile = jsonMetadata.getAsJsonObject("profile")
        val name = jsonProfile?.get("name")?.asString ?: ""
        val about = jsonProfile?.get("about")?.asString ?: ""
        val location = jsonProfile?.get("location")?.asString ?: ""
        val website = jsonProfile?.get("website")?.asString ?: ""
        val coverImageURL = jsonProfile?.get("cover_image")?.asString ?: ""

        return SteemitProfile(
            account,
            name,
            about,
            followingCount,
            folowerCount,
            location,
            website,
            coverImageURL
        )
    }

    fun toSteemitAccountDetails(getDynamicGlobalPropertiesDTO: GetDynamicGlobalPropertiesDTO?): AccountDetails {
        val floatVestingShare = vesting_shares?.removeSubstring(" VESTS")?.toFloatOrNull() ?: 0f
        val floatDelegatedVestingShare = delegated_vesting_shares?.removeSubstring(" VESTS")?.toFloatOrNull() ?: 0f
        val floatReceivedVestingShare = received_vesting_shares?.removeSubstring(" VESTS")?.toFloatOrNull() ?: 0f
        val floatRewardVestingBalance = reward_vesting_balance?.removeSubstring(" VESTS")?.toFloatOrNull() ?: 0f
        val floatTotalVestingShare = getDynamicGlobalPropertiesDTO?.total_vesting_shares?.removeSubstring(" VESTS")?.toFloatOrNull() ?: 0f
        val floatTotalVestingFundSteem = getDynamicGlobalPropertiesDTO?.total_vesting_fund_steem?.removeSubstring(" STEEM")?.toFloatOrNull() ?: 0f

        val steemPower = Converter.toSteemPowerFromVest(
            floatVestingShare,
            floatTotalVestingShare,
            floatTotalVestingFundSteem
        )
        val delegatedSteemPower = Converter.toSteemPowerFromVest(
            floatDelegatedVestingShare,
            floatTotalVestingShare,
            floatTotalVestingFundSteem
        )
        val receivedSteemPower = Converter.toSteemPowerFromVest(
            floatReceivedVestingShare,
            floatTotalVestingShare,
            floatTotalVestingFundSteem
        )
        val pendingSPBalance = Converter.toSteemPowerFromVest(
            floatRewardVestingBalance,
            floatTotalVestingShare,
            floatTotalVestingFundSteem
        )

        val floatEffectiveVestingShare = floatVestingShare + floatReceivedVestingShare - floatDelegatedVestingShare
        val currentUpvoteMana = voting_manabar?.current_mana?.toLongOrNull() ?: 0
        val currentDownvoteMana = downvote_manabar?.current_mana?.toLongOrNull() ?: 0
        val maxUpvoteMana = floatEffectiveVestingShare * 1000000
        val maxDownvoteMana = maxUpvoteMana / 4f
        val upvoteManaRate = (currentUpvoteMana / maxUpvoteMana) * 100f
        val downvoteManaRate = (currentDownvoteMana / maxDownvoteMana) * 100f

        return AccountDetails(
            id ?: "",
            name ?: "",
            recovery_account ?: "",
            last_account_recovery ?: "",
            Converter.toLocalTimeFromUTCTime(last_owner_update ?: "", "yyyy-MM-dd HH:mm:ss"),
            Converter.toLocalTimeFromUTCTime(last_account_update ?: "", "yyyy-MM-dd HH:mm:ss"),
            // asset info
            balance ?: "0.000 STEEM",
            sbd_balance ?: "0.000 SBD",
            String.format("%.3f SP", steemPower),
            String.format("%.3f SP", delegatedSteemPower),
            String.format("%.3f SP", receivedSteemPower),
            reward_sbd_balance ?: "0.000 SBD",
            reward_steem_balance ?: "0.000 STEEM",
            String.format("%.3f SP", pendingSPBalance),
            // activity info
            post_count ?: 0,
            Converter.toLocalTimeFromUTCTime(last_post ?: "", "yyyy-MM-dd HH:mm:ss") ?: "",
            Converter.toLocalTimeFromUTCTime(last_root_post ?: "", "yyyy-MM-dd HH:mm:ss") ?: "",
            can_vote ?: true,
            Converter.toLocalTimeFromUTCTime(last_vote_time ?: "", "yyyy-MM-dd HH:mm:ss") ?: "",
            (voting_power?.toFloat() ?: 0f) / 100.0f,
            DecimalFormat("#.##").format(upvoteManaRate).toFloatOrNull() ?: 0f,
            DecimalFormat("#.##").format(downvoteManaRate).toFloatOrNull() ?: 0f,
            witness_votes ?: listOf(),
            proxy ?: "",
            reputation?.convertToUserReadableReputation() ?: "",
        )
    }

    fun toSteemitWallet(): SteemitWallet {
        val vestingToBeWithdrawn = try {
            (to_withdraw?.toFloat() ?: 0f) / 100000f
        }
        catch (e: NumberFormatException) {
            0f
        }
        val remainingSPToBeWithdrawn = try {
            vestingToBeWithdrawn - (withdrawn?.toFloat() ?: 0f / 1000000f)
        }
        catch (e: NumberFormatException) {
            0f
        }

        val floatVestingShare = vesting_shares?.removeSubstring(" VESTS")?.toFloat() ?: 0f
        val floatDelegatedVestingShare = delegated_vesting_shares?.removeSubstring(" VESTS")?.toFloat() ?: 0f
        val floatReceivedVestingShare = received_vesting_shares?.removeSubstring(" VESTS")?.toFloat() ?: 0f
        val floatEffectiveVestingShare = floatVestingShare + floatReceivedVestingShare - floatDelegatedVestingShare
        val floatVestingWithdrawRate = vesting_withdraw_rate?.removeSubstring(" VESTS")?.toFloat() ?: 0f
        val nextPowerDownTime = when {
            (0f == floatVestingWithdrawRate) -> ""
            else -> Converter.toLocalTimeFromUTCTime(next_vesting_withdrawal ?: "", "yyyy-MM-dd HH:mm:ss")
        }

        return SteemitWallet(
            name ?: "",
            balance ?: "0 STEEM",
            sbd_balance ?: "0 SBD",
            savings_balance ?: "0 STEEM",
            savings_sbd_balance ?: "0 SBD",
            vesting_shares ?: "0 VESTS",
            "${floatEffectiveVestingShare} VESTS",
            delegated_vesting_shares ?: "0 VESTS",
            received_vesting_shares ?: "0 VESTS",
            vesting_withdraw_rate ?: "0 VESTS",
            String.format("%.6f VESTS", vestingToBeWithdrawn),
            String.format("%.6f VESTS", remainingSPToBeWithdrawn),
            nextPowerDownTime
        )
    }

    fun toSteemitWallet(getDynamicGlobalPropertiesDTO: GetDynamicGlobalPropertiesDTO?): SteemitWallet {
        if (null == getDynamicGlobalPropertiesDTO) {
            return toSteemitWallet()
        }

        val vestingToBeWithdrawn = try {
            (to_withdraw?.toFloat() ?: 0f) / 1000000f
        }
        catch (e: NumberFormatException) {
            0f
        }
        val remainingVestToBeWithdrawn = try {
            vestingToBeWithdrawn - ((withdrawn?.toFloat() ?: 0f) / 1000000f)
        }
        catch (e: NumberFormatException) {
            0f
        }

        val floatVestingShare = vesting_shares?.removeSubstring(" VESTS")?.toFloat() ?: 0f
        val floatDelegatedVestingShare = delegated_vesting_shares?.removeSubstring(" VESTS")?.toFloat() ?: 0f
        val floatReceivedVestingShare = received_vesting_shares?.removeSubstring(" VESTS")?.toFloat() ?: 0f
        val floatEffectiveVestingShare = floatVestingShare + floatReceivedVestingShare - floatDelegatedVestingShare
        val floatVestingWithdrawRate = vesting_withdraw_rate?.removeSubstring(" VESTS")?.toFloat() ?: 0f
        val floatTotalVestingShare = getDynamicGlobalPropertiesDTO.total_vesting_shares?.removeSubstring(" VESTS")?.toFloat() ?: 0f
        val floatTotalVestingFundSteem = getDynamicGlobalPropertiesDTO.total_vesting_fund_steem?.removeSubstring(" STEEM")?.toFloat() ?: 0f

        val steemPower = Converter.toSteemPowerFromVest(
            floatVestingShare,
            floatTotalVestingShare,
            floatTotalVestingFundSteem
        )
        val effectiveSteemPower = Converter.toSteemPowerFromVest(
            floatEffectiveVestingShare,
            floatTotalVestingShare,
            floatTotalVestingFundSteem
        )
        val delegatedSteemPower = Converter.toSteemPowerFromVest(
            floatDelegatedVestingShare,
            floatTotalVestingShare,
            floatTotalVestingFundSteem
        )
        val receivedSteemPower = Converter.toSteemPowerFromVest(
            floatReceivedVestingShare,
            floatTotalVestingShare,
            floatTotalVestingFundSteem
        )
        val steemPowerWithdrawRate = Converter.toSteemPowerFromVest(
            floatVestingWithdrawRate,
            floatTotalVestingShare,
            floatTotalVestingFundSteem
        )
        val totalSteemPowerToBeWithdrawn = when {
            (0f == floatVestingWithdrawRate) -> 0f
            else -> Converter.toSteemPowerFromVest(
                vestingToBeWithdrawn,
                floatTotalVestingShare,
                floatTotalVestingFundSteem
            )
        }
        val remainingSteemPowerToBeWithdrawn = Converter.toSteemPowerFromVest(
            remainingVestToBeWithdrawn,
            floatTotalVestingShare,
            floatTotalVestingFundSteem
        )

        val nextPowerDownTime = when {
            (0f == floatVestingWithdrawRate) -> ""
            else -> Converter.toLocalTimeFromUTCTime(next_vesting_withdrawal ?: "", "yyyy-MM-dd HH:mm:ss")
        }

        return SteemitWallet(
            name ?: "",
            balance ?: "0 STEEM",
            sbd_balance ?: "0 SBD",
            savings_balance ?: "0 STEEM",
            savings_sbd_balance ?: "0 SBD",
            String.format("%.3f SP", steemPower),
            String.format("%.3f SP", effectiveSteemPower),
            String.format("%.3f SP", delegatedSteemPower),
            String.format("%.3f SP", receivedSteemPower),
            String.format("%.3f SP", steemPowerWithdrawRate),
            String.format("%.3f SP", totalSteemPowerToBeWithdrawn),
            String.format("%.3f SP", remainingSteemPowerToBeWithdrawn),
            nextPowerDownTime
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
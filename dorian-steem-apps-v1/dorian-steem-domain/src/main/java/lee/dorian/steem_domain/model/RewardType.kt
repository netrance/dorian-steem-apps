package lee.dorian.steem_domain.model

enum class RewardType {
    AUTHOR,      // The reward paid to the author of a post. The post is written by the account itself.
    CURATION     // The reward paid for voting on a post. The post is written by someone else.
}

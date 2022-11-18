package lee.dorian.steem_domain.usecase

import io.reactivex.Single
import lee.dorian.steem_domain.model.PostItem
import lee.dorian.steem_domain.repository.SteemRepository

class ReadRankedPostsUseCase(val steemRepository: SteemRepository) {

    operator fun invoke(
        sort: String,
        tag: String,
        observer: String = "",
        limit: Int = 20,
        lastPostItem: PostItem? = null
    ): Single<List<PostItem>> {
        return steemRepository.readRankedPosts(
            sort,
            tag,
            observer,
            limit,
            lastPostItem
        )
    }

}
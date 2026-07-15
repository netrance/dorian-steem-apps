package lee.dorian.steem_ui.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import lee.dorian.steem_data.repository.SteemRepositoryImpl
import lee.dorian.steem_data.repository.SteemWorldRepositoryImpl
import lee.dorian.steem_domain.repository.SteemRepository
import lee.dorian.steem_domain.repository.SteemWorldRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindSteemRepository(
        impl: SteemRepositoryImpl
    ): SteemRepository

    @Binds
    abstract fun bindSteemWorldRepository(
        impl: SteemWorldRepositoryImpl
    ): SteemWorldRepository

}
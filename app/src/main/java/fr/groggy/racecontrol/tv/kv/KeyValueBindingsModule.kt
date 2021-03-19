package fr.groggy.racecontrol.tv.kv

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.groggy.racecontrol.tv.core.credentials.F1CredentialsRepository
import fr.groggy.racecontrol.tv.core.token.F1TokenRepository
import fr.groggy.racecontrol.tv.kv.credentials.SharedPreferencesF1CredentialsRepository
import fr.groggy.racecontrol.tv.kv.token.SharedPreferencesF1TokenRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class KeyValueBindingsModule {

    @Binds
    abstract fun f1CredentialsRepository(repository: SharedPreferencesF1CredentialsRepository): F1CredentialsRepository

    @Binds
    abstract fun f1TokenRepository(repository: SharedPreferencesF1TokenRepository): F1TokenRepository

}

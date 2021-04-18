package fr.groggy.racecontrol.tv

import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.threeten.bp.Clock
import java.net.CookieManager
import java.net.CookiePolicy
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RaceControlTvModule {

    @Provides
    @Singleton
    fun clock(): Clock =
        Clock.systemUTC()

    @Provides
    @Singleton
    fun cookieManager(): CookieManager {
        val manager = CookieManager()
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
        return manager
    }

    @Provides
    fun loggingInterceptor(): HttpLoggingInterceptor {
        return if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
    }

    @Provides
    @Singleton
    fun okHttpClient(cookieManager: CookieManager, loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun moshi(): Moshi =
        Moshi.Builder().build()

    @Provides
    @Singleton
    fun httpDataSourceFactory(okHttpClient: OkHttpClient): HttpDataSource.Factory {
        return OkHttpDataSource.Factory(okHttpClient)
            .setUserAgent(BuildConfig.DEFAULT_USER_AGENT)
    }
}

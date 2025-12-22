package com.suraksha.app.di

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.suraksha.app.data.MapRepositoryImpl
import com.suraksha.app.data.SOSRepositoryImpl
import com.suraksha.app.domain.MapRepository
import com.suraksha.app.domain.SOSRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContentResolver(
        @ApplicationContext context: Context
    ): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideSOSRepository(
        contentResolver: ContentResolver,
        @ApplicationContext context: Context,
        firestore: FirebaseFirestore
    ): SOSRepository {
        return SOSRepositoryImpl(
            contentResolver = contentResolver,
            context = context,
            firestore = firestore
        )
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFusedLocationProvider(
        app: Application
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(app)
    }

    @Provides
    @Singleton
    fun provideMapRepository(
        app: Application,
        client: FusedLocationProviderClient,
        firestore: FirebaseFirestore,
        okHttpClient: OkHttpClient,
    ): MapRepository {
        return MapRepositoryImpl(app, client, firestore,okHttpClient)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
    }

}
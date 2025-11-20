package com.suraksha.app.di

import android.content.ContentResolver
import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.suraksha.app.data.SOSRepositoryImpl
import com.suraksha.app.domain.SOSRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
}
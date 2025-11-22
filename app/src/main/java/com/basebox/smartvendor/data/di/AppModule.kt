package com.basebox.smartvendor.data.di

import com.basebox.smartvendor.data.remote.api.ReceiptAPI
import com.basebox.smartvendor.data.repository.ReceiptRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideReceiptRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        api: ReceiptAPI
    ): ReceiptRepository {
        // Assuming your ReceiptRepository implementation takes these Firebase
        // services as constructor parameters.
        // For example: class ReceiptRepository(auth: FirebaseAuth, firestore: FirebaseFirestore, ...)
        return ReceiptRepository(auth, firestore, storage, api)
    }
}
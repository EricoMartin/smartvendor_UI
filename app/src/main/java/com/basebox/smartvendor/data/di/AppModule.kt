package com.basebox.smartvendor.data.di

import android.content.Context
import androidx.room.Room
import com.basebox.smartvendor.data.datastore.UserPreferences
import com.basebox.smartvendor.data.local.SmartVendorDatabase
import com.basebox.smartvendor.data.local.dao.InventoryDao
import com.basebox.smartvendor.data.local.dao.NotificationDao
import com.basebox.smartvendor.data.local.dao.ReceiptDao
import com.basebox.smartvendor.data.local.dao.SalesDao
import com.basebox.smartvendor.data.local.dao.VendorDao
import com.basebox.smartvendor.data.remote.api.ReceiptAPI
import com.basebox.smartvendor.data.repository.ReceiptRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.storage.FirebaseStorage
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
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        // 🔥 Enable Firestore offline persistence
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(
                PersistentCacheSettings.newBuilder()
                    .setSizeBytes(100L * 1024L * 1024L) // 100MB
                    .build()
            )
            .build()
        firestore.firestoreSettings = settings
        return firestore
    }
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

    @Provides
    @Singleton
    fun provideUserPreferences(
        @ApplicationContext context: Context
    ): UserPreferences = UserPreferences(context)

    @Provides
    @Singleton
    fun provideSmartVendorDatabase(
        @ApplicationContext context: Context
    ): SmartVendorDatabase {
        return Room.databaseBuilder(
            context,
            SmartVendorDatabase::class.java,
            "smart_vendor_db"
        )
            .fallbackToDestructiveMigration() // Use with caution in production
            .build()
    }

    @Provides
    fun provideInventoryDao(db: SmartVendorDatabase): InventoryDao = db.inventoryDao()

    @Provides
    fun provideSalesDao(db: SmartVendorDatabase): SalesDao = db.salesDao()

    @Provides
    fun provideReceiptDao(db: SmartVendorDatabase): ReceiptDao = db.receiptDao()

    @Provides
    fun provideVendorDao(db: SmartVendorDatabase): VendorDao = db.vendorDao()

    @Provides
    fun provideNotificationDao(db: SmartVendorDatabase): NotificationDao = db.notificationDao()

}
package com.store.riderfit.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.store.riderfit.data.local.database.AppDatabase
import com.store.riderfit.data.local.database.dao.UserDao
import com.store.riderfit.data.local.preferences.UserPreferences
import com.store.riderfit.data.remote.firebase.FirebaseAuthService
import com.store.riderfit.data.remote.firebase.FirebaseUserService
import com.store.riderfit.data.remote.firebase.FirestoreService
import com.store.riderfit.data.repository.AuthRepositoryImpl
import com.store.riderfit.data.repository.ProductRepositoryImpl
import com.store.riderfit.data.repository.UserRepositoryImpl
import com.store.riderfit.domain.repository.IAuthRepository
import com.store.riderfit.domain.repository.IProductRepository
import com.store.riderfit.domain.repository.IUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Firebase Services
    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseAuthService(firebaseAuth: FirebaseAuth): FirebaseAuthService =
        FirebaseAuthService(firebaseAuth)

    @Singleton
    @Provides
    fun provideFirebaseUserService(firestore: FirebaseFirestore): FirebaseUserService =
        FirebaseUserService(firestore)

    @Singleton
    @Provides
    fun provideFirestoreService(firestore: FirebaseFirestore): FirestoreService =
        FirestoreService(firestore)

    // Database
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "riderfit_database"
        ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()

    // Preferences
    @Singleton
    @Provides
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences =
        UserPreferences(context)

    // Repositories
    @Singleton
    @Provides
    fun provideAuthRepository(
        firebaseAuthService: FirebaseAuthService,
        userDao: UserDao,
        userPreferences: UserPreferences
    ): IAuthRepository =
        AuthRepositoryImpl(firebaseAuthService, userDao, userPreferences)

    @Singleton
    @Provides
    fun provideUserRepository(
        firebaseUserService: FirebaseUserService,
        userDao: UserDao
    ): IUserRepository =
        UserRepositoryImpl(firebaseUserService, userDao)

    @Singleton
    @Provides
    fun provideProductRepository(
        firestoreService: FirestoreService
    ): IProductRepository =
        ProductRepositoryImpl(firestoreService)
}

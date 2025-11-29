package pe.idat.e_commerce_ef.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pe.idat.e_commerce_ef.data.local.AppDatabase
import pe.idat.e_commerce_ef.data.repository.ProductRepositoryImpl
import pe.idat.e_commerce_ef.data.repository.UserRepositoryImpl
import pe.idat.e_commerce_ef.domain.repository.ProductRepository
import pe.idat.e_commerce_ef.domain.repository.UserRepository
import pe.idat.e_commerce_ef.domain.usecase.GetProductsUseCase
import pe.idat.e_commerce_ef.domain.usecase.LoginUseCase
import pe.idat.e_commerce_ef.domain.usecase.RegisterUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideProductRepository(database: AppDatabase): ProductRepository {
        return ProductRepositoryImpl(database)
    }

    @Provides
    @Singleton
    fun provideUserRepository(firebaseAuth: FirebaseAuth): UserRepository {
        return UserRepositoryImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideGetProductsUseCase(repository: ProductRepository): GetProductsUseCase {
        return GetProductsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(repository: UserRepository): LoginUseCase {
        return LoginUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(repository: UserRepository): RegisterUseCase {
        return RegisterUseCase(repository)
    }
}
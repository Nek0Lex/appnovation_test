package com.example.appnovation_test.modules

import androidx.lifecycle.ViewModel
import com.example.appnovation_test.repositories.BanksRepository
import com.example.appnovation_test.repositories.BanksRepositoryImpl
import com.example.appnovation_test.usecases.BanksUseCase
import com.example.appnovation_test.usecases.BanksUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class BanksModule {
    @Binds
    abstract fun bindBanksRepository(impl: BanksRepositoryImpl): BanksRepository
    
    @Binds
    abstract fun bindBanksUseCase(impl: BanksUseCaseImpl): BanksUseCase
}
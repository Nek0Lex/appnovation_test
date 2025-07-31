package com.example.appnovation_test.usecases

import com.example.appnovation_test.model.BankInfo
import com.example.appnovation_test.repositories.BanksRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BanksUseCaseImpl @Inject constructor(
    private val banksRepository: BanksRepository
) : BanksUseCase {
    override suspend fun invoke(): Result<List<BankInfo>> {
        return withContext(Dispatchers.IO) { banksRepository.invoke() }
    }
}
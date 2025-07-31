package com.example.appnovation_test.usecases

import com.example.appnovation_test.model.BankInfo

interface BanksUseCase {
    suspend operator fun invoke(): Result<List<BankInfo>>
}
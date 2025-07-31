package com.example.appnovation_test.repositories

import com.example.appnovation_test.model.BankInfo

interface BanksRepository {
  suspend fun invoke(): Result<List<BankInfo>>
}
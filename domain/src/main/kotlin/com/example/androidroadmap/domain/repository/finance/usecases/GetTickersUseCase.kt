package com.example.androidroadmap.domain.repository.finance.usecases

import com.example.androidroadmap.domain.repository.finance.repository.FinanceRepository
import javax.inject.Inject

class GetTickersUseCase @Inject constructor(
    private val repository: FinanceRepository
){
    suspend operator fun invoke(limit: Int?) = repository.getTickers(limit)
}
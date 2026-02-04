package com.example.androidroadmap.domain.repository.finance.usecases

import com.example.androidroadmap.domain.repository.finance.repository.FinanceRepository
import javax.inject.Inject

class GetTickerDetails @Inject constructor(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(symbol: String?, date: String?) =
        repository.getTickerDetails(symbol, date)
}
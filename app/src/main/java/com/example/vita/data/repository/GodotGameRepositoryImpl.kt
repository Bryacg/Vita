package com.example.vita.data.repository

import com.example.vita.data.local.datasource.GodotGameDataSource
import com.example.vita.domain.repository.GodotGameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GodotGameRepositoryImpl @Inject constructor(
    private val dataSource: GodotGameDataSource
) : GodotGameRepository {

    override suspend fun readGameResult(): String? {
        return dataSource.readGameResult()
    }

    override suspend fun writeGameResult(result: String) {
        dataSource.writeGameResult(result)
    }

    override suspend fun clearGameResult() {
        dataSource.clearGameResult()
    }
}
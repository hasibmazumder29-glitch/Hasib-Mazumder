package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GatewayConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface GatewayConfigDao {
  @Query("SELECT * FROM gateway_config WHERE id = 1 LIMIT 1")
  fun getConfig(): Flow<GatewayConfig?>

  @Query("SELECT * FROM gateway_config WHERE id = 1 LIMIT 1")
  suspend fun getConfigDirect(): GatewayConfig?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertConfig(config: GatewayConfig)

  @Update
  suspend fun updateConfig(config: GatewayConfig)
}

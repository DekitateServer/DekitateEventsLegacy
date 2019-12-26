package com.dekitateserver.events.data.source

import com.dekitateserver.core.data.source.AbstractDao
import com.dekitateserver.events.data.vo.GachaId
import com.dekitateserver.events.util.Log
import java.sql.SQLException
import java.sql.Timestamp
import java.util.*
import javax.sql.DataSource

class GachaHistoryDao(dataSource: DataSource) : AbstractDao(dataSource) {

    companion object {
        private const val TABLE_NAME = "dekitateevents_gacha_history"
        private const val SQL_CREATE_TABLE_IF_NOT_EXISTS = """
            CREATE TABLE IF NOT EXISTS `$TABLE_NAME` (
              `id` INT NOT NULL AUTO_INCREMENT,
              `uuid` VARCHAR(36) NOT NULL,
              `gacha_id` VARCHAR(128) NOT NULL,
              `gacha_item_id` VARCHAR(128) NOT NULL,
              `created_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
              PRIMARY KEY (`id`)
            )
            """
    }

    init {
        createTableIfNotExists()
    }

    private fun createTableIfNotExists() {
        try {
            connection {
                val st = prepareStatement(SQL_CREATE_TABLE_IF_NOT_EXISTS)
                st.executeUpdate()
                st.close()
            }
        } catch (e: SQLException) {
            Log.error("Failed to create table($TABLE_NAME)", e)
        }
    }

    fun insert(uuid: UUID, gachaId: GachaId, gachaItemId: String): Boolean {
        try {
            connection {
                val st = prepareStatement("INSERT INTO $TABLE_NAME (uuid,gacha_id,gacha_item_id) VALUES (?,?,?)")
                st.setUuid(1, uuid)
                st.setString(2, gachaId.value)
                st.setString(3, gachaItemId)

                val count = st.executeUpdate()
                return count > 0
            }
        } catch (e: SQLException) {
            Log.error("Failed to insert a gacha log", e)
        }

        return false
    }

    fun exists(gachaId: GachaId, gachaItemId: String, start: Timestamp, end: Timestamp): Boolean {
        try {
            connection {
                val st = prepareStatement("SELECT EXISTS(SELECT 1 FROM $TABLE_NAME WHERE (created_date BETWEEN ? AND ?) AND gacha_id=? AND gacha_item_id=?)")
                st.setTimestamp(1, start)
                st.setTimestamp(2, end)
                st.setString(3, gachaId.value)
                st.setString(4, gachaItemId)

                val result = st.executeQuery()
                return result.next() && result.getInt(1) == 1
            }
        } catch (e: SQLException) {
            Log.error("Failed to select exists gacha log", e)
        }

        return false
    }
}

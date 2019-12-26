package com.dekitateserver.events.data.source

import com.dekitateserver.core.data.source.AbstractDao
import com.dekitateserver.events.data.vo.DungeonAction
import com.dekitateserver.events.data.vo.DungeonId
import com.dekitateserver.events.util.Log
import java.sql.SQLException
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*
import javax.sql.DataSource

class DungeonActionHistoryDao(dataSource: DataSource) : AbstractDao(dataSource) {

    companion object {
        private const val TABLE_NAME = "dekitateevents_dungeon_action_history"
        private const val SQL_CREATE_TABLE_IF_NOT_EXISTS = """
            CREATE TABLE IF NOT EXISTS `$TABLE_NAME` (
              `id` INT NOT NULL AUTO_INCREMENT,
              `uuid` VARCHAR(36) NOT NULL,
              `dungeon_id` VARCHAR(128) NOT NULL,
              `action` VARCHAR(36) NOT NULL,
              `actioned_date` DATETIME(3) NOT NULL,
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
            Log.error("Failed to create a table($TABLE_NAME)", e)
        }
    }

    fun insert(uuid: UUID, dungeonId: DungeonId, action: DungeonAction, actionedDate: LocalDateTime): Boolean {
        try {
            connection {
                val st = prepareStatement("INSERT INTO $TABLE_NAME (uuid,dungeon_id,action,actioned_date) VALUES (?,?,?,?)")
                st.setUuid(1, uuid)
                st.setString(2, dungeonId.value)
                st.setString(3, action.name)
                st.setTimestamp(4, Timestamp.valueOf(actionedDate))

                val count = st.executeUpdate()
                return count > 0
            }
        } catch (e: SQLException) {
            Log.error("Failed to insert a history of dungeon action", e)
        }

        return false
    }

    fun selectLatestActionedDate(uuid: UUID, dungeonId: DungeonId, action: DungeonAction): LocalDateTime? {
        try {
            connection {
                val st = prepareStatement("SELECT actioned_date FROM $TABLE_NAME WHERE uuid=? AND dungeon_id=? AND action=? ORDER BY actioned_date DESC LIMIT 1")
                st.setUuid(1, uuid)
                st.setString(2, dungeonId.value)
                st.setString(3, action.name)

                val result = st.executeQuery()
                return if (result.next()) result.getTimestamp(1).toLocalDateTime() else null
            }
        } catch (e: SQLException) {
            Log.error("Failed to select actioned_date", e)
        }

        return null
    }
}

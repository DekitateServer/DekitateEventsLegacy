package com.dekitateserver.events.data.source

import com.dekitateserver.core.data.source.AbstractDao
import com.dekitateserver.events.data.vo.LoginBonusId
import com.dekitateserver.events.util.Log
import java.sql.SQLException
import java.util.*
import javax.sql.DataSource

class LoginBonusHistoryDao(dataSource: DataSource) : AbstractDao(dataSource) {

    companion object {
        private const val TABLE_NAME = "dekitateevents_login_bonus_history"
        private const val SQL_CREATE_TABLE_IF_NOT_EXISTS = """
            CREATE TABLE IF NOT EXISTS `$TABLE_NAME` (
              `id` INT NOT NULL AUTO_INCREMENT,
              `uuid` VARCHAR(36) NOT NULL,
              `login_bonus_id` VARCHAR(128) NOT NULL,
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
            Log.error("Failed to create table", e)
        }
    }

    fun insert(uuid: UUID, loginBonusId: LoginBonusId): Boolean {
        try {
            connection {
                val st = prepareStatement("INSERT INTO $TABLE_NAME (uuid,login_bonus_id) VALUES (?,?)")
                st.setUuid(1, uuid)
                st.setString(2, loginBonusId.value)

                val count = st.executeUpdate()
                return count > 0
            }
        } catch (e: SQLException) {
            Log.error("Failed to insert a login bonus log", e)
        }

        return false
    }

    fun exists(uuid: UUID, loginBonusId: LoginBonusId): Boolean {
        try {
            connection {
                val st = prepareStatement("SELECT EXISTS(SELECT 1 FROM $TABLE_NAME WHERE uuid=? AND login_bonus_id=?)")
                st.setUuid(1, uuid)
                st.setString(2, loginBonusId.value)

                val result = st.executeQuery()
                return result.next() && result.getInt(1) == 1
            }
        } catch (e: SQLException) {
            Log.error("Failed to select", e)
        }

        return false
    }
}

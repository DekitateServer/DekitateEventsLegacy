package com.dekitateserver.events.data.source

import com.dekitateserver.core.data.source.AbstractDao
import com.dekitateserver.events.util.Log
import java.sql.SQLException
import java.sql.Timestamp
import java.util.*
import javax.sql.DataSource

class EventTicketHistoryDao(dataSource: DataSource) : AbstractDao(dataSource) {

    companion object {
        private const val TABLE_NAME = "dekitateevents_event_ticket_history"
        private const val SQL_CREATE_TABLE_IF_NOT_EXISTS = """
            CREATE TABLE IF NOT EXISTS `$TABLE_NAME` (
              `id` INT NOT NULL AUTO_INCREMENT,
              `uuid` VARCHAR(36) NOT NULL,
              `amount` INT NOT NULL,
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

    fun insert(uuid: UUID, amount: Int): Boolean {
        try {
            connection {
                val st = prepareStatement("INSERT INTO $TABLE_NAME (uuid,amount) VALUES (?,?)")
                st.setUuid(1, uuid)
                st.setInt(2, amount)

                val count = st.executeUpdate()
                return count > 0
            }
        } catch (e: SQLException) {
            Log.error("Failed to insert a event ticket log", e)
        }

        return false
    }

    fun getPositiveAmountBetween(uuid: UUID, start: Timestamp, end: Timestamp): Int {
        try {
            connection {
                val st = prepareStatement("SELECT SUM(amount) FROM $TABLE_NAME WHERE (created_date BETWEEN ? AND ?) AND uuid=? AND amount>0")
                st.setTimestamp(1, start)
                st.setTimestamp(2, end)
                st.setUuid(3, uuid)

                val result = st.executeQuery()
                val amount = if (result.next()) result.getInt(1) else -1
                st.close()

                return amount
            }
        } catch (e: SQLException) {
            Log.error("Failed to get amount between", e)
        }

        return -1
    }
}

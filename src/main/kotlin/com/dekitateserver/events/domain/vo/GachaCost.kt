package com.dekitateserver.events.domain.vo

import org.bukkit.configuration.serialization.ConfigurationSerializable

sealed class GachaCost : ConfigurationSerializable {

    object Free : GachaCost() {
        override fun serialize(): Map<String, Any> = emptyMap()

        @Suppress("UNUSED", "UNUSED_PARAMETER")
        @JvmStatic
        fun deserialize(map: Map<String, Any?>) = Free
    }

    class EventTicket(
            val amount: Int
    ) : GachaCost() {
        override fun serialize(): Map<String, Any> = mapOf("amount" to amount)

        companion object {
            @Suppress("UNUSED")
            @JvmStatic
            fun deserialize(map: Map<String, Any?>) = EventTicket(
                    amount = map["amount"] as Int
            )
        }
    }

    class VoteTicket(
            val amount: Int
    ) : GachaCost() {
        override fun serialize(): Map<String, Any> = mapOf("amount" to amount)

        companion object {
            @Suppress("UNUSED")
            @JvmStatic
            fun deserialize(map: Map<String, Any?>) = VoteTicket(
                    amount = map["amount"] as Int
            )
        }
    }

    class Key(
            val keyId: KeyId
    ) : GachaCost() {
        override fun serialize(): Map<String, Any> = mapOf("keyId" to keyId.value)

        companion object {
            @Suppress("UNUSED")
            @JvmStatic
            fun deserialize(map: Map<String, Any?>) = Key(
                    keyId = KeyId(map["keyId"] as String)
            )
        }
    }
}

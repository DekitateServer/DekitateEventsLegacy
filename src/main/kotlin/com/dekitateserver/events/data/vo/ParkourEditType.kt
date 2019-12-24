package com.dekitateserver.events.data.vo

enum class ParkourEditType(
        val id: String,
        val description: String
) {
    NAME("name", "[text] 名前(必須)"),
    REWARD_EVENT_TICKET("reward-event-ticket", "[amount] イベントチケット報酬"),
    JOIN_LOCATION("join-location", "[here/x y z <yaw pitch>] 参加TP位置"),
    JOIN_MESSAGE("join-msg", "[msg] 参加時に表示"),
    JOIN_BROADCAST_MESSAGE("join-broadcast-msg", "[msg] 参加時に全員に表示"),
    CLEAR_LOCATION("clear-location", "[here/x y z <yaw pitch>] クリアTP位置"),
    CLEAR_MESSAGE("clear-msg", "[msg] クリア時に表示"),
    CLEAR_BROADCAST_MESSAGE("clear-broadcast-msg", "[msg] クリア時に全員に表示"),
    EXIT_LOCATION("exit-location", "[here/x y z <yaw pitch>] 退出TP位置"),
    EXIT_MESSAGE("exit-msg", "[msg] 退出時に表示");

    companion object {
        fun find(id: String) = values().find { it.id == id }
    }
}

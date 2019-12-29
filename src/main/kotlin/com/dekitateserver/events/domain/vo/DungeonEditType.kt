package com.dekitateserver.events.domain.vo

enum class DungeonEditType(
        val id: String,
        val description: String
) {
    NAME("name", "[text] 名前(必須)"),
    REWARD_TICKET("reward-ticket", "[amount] イベントチケット報酬"),
    REWARD_GACHA("reward-gacha", "[gachaId] ガチャ報酬"),
    JOIN_LOCATION("join-location", "[here/x y z <yaw pitch>] 参加TP位置"),
    JOIN_MESSAGE("join-msg", "[msg] 参加時に表示"),
    JOIN_BROADCAST_MESSAGE("join-broadcast-msg", "[msg] 参加時に全員に表示"),
    COMPLETE_LOCATION("complete-location", "[here/x y z <yaw pitch>] クリアTP位置"),
    COMPLETE_MESSAGE("complete-msg", "[msg] クリア時に表示"),
    COMPLETE_BROADCAST_MESSAGE("complete-broadcast-msg", "[msg] クリア時に全員に表示"),
    COMPLETE_SOUND("complete-sound", "[on/off] クリアサウンドを再生"),
    EXIT_LOCATION("exit-location", "[here/x y z <yaw pitch>] 退出TP位置"),
    EXIT_MESSAGE("exit-msg", "[msg] 退出時に表示"),
    LOCK_MESSAGE("lock-msg", "[msg] ロック時に表示"),
    LOCK_BROADCAST_MESSAGE("lock-broadcast-msg", "[msg] ロック時に全員に表示"),
    UNLOCK_BROADCAST_MESSAGE("unlock-broadcast-msg", "[msg] ロック解除時に全員に表示");

    companion object {
        fun find(id: String) = values().find { it.id == id }
    }
}

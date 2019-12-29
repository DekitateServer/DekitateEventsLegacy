package com.dekitateserver.events.domain.usecase.gacha

import com.dekitateserver.events.domain.repository.*
import com.dekitateserver.events.domain.usecase.eventticket.TakeEventTicketUseCase
import com.dekitateserver.events.domain.usecase.key.UseKeyUseCase
import com.dekitateserver.events.domain.usecase.voteticket.TakeVoteTicketUseCase
import com.dekitateserver.events.domain.vo.GachaCost
import com.dekitateserver.events.domain.vo.GachaId
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.entity.Player

class ClickGachaSignUseCase(
        server: Server,
        gachaRepository: GachaRepository,
        keyRepository: KeyRepository,
        gachaHistoryRepository: GachaHistoryRepository,
        eventTicketHistoryRepository: EventTicketHistoryRepository,
        voteTicketHistoryRepository: VoteTicketHistoryRepository,
        private val signMetaRepository: SignMetaRepository
) : AbstractGachaSignUseCase() {

    private val playGachaUseCase = PlayGachaUseCase(server, gachaRepository, gachaHistoryRepository)

    private val takeEventTicketUseCase = TakeEventTicketUseCase(eventTicketHistoryRepository, voteTicketHistoryRepository)

    private val takeVoteTicketUseCase = TakeVoteTicketUseCase(voteTicketHistoryRepository)

    private val useKeyUseCase = UseKeyUseCase(keyRepository)

    suspend operator fun invoke(player: Player, location: Location) {
        val signMeta = signMetaRepository.getOrError(location) ?: return

        val gachaIdString = signMeta.getStringOrError(KEY_SIGN_META_GACHA_ID) ?: return
        val gachaCost = signMeta.getOrError<GachaCost>(KEY_SIGN_META_GACHA_COST) ?: return

        val isSuccessful = when (gachaCost) {
            is GachaCost.EventTicket -> takeEventTicketUseCase(player, gachaCost.amount)
            is GachaCost.VoteTicket -> takeVoteTicketUseCase(player, gachaCost.amount)
            is GachaCost.Key -> useKeyUseCase(player, gachaCost.keyId)
            GachaCost.Free -> true
        }

        if (isSuccessful) {
            playGachaUseCase(
                    player = player,
                    gachaId = GachaId(gachaIdString)
            )
        }
    }
}

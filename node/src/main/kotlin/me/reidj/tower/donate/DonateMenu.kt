package me.reidj.tower.donate

import me.func.mod.Anime
import me.func.mod.reactive.ReactiveButton
import me.func.mod.ui.Glow
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.confirmation.Confirmation
import me.func.mod.ui.menu.selection
import me.func.mod.util.command
import me.func.protocol.data.color.GlowColor
import me.reidj.tower.app
import me.reidj.tower.clientSocket
import me.reidj.tower.protocol.SaveUserPackage
import me.reidj.tower.util.PATH
import org.bukkit.entity.Player
import ru.cristalix.core.coupons.ICouponsService
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.network.packages.GetAccountBalancePackage
import ru.cristalix.core.network.packages.MoneyTransactionRequestPackage
import ru.cristalix.core.network.packages.MoneyTransactionResponsePackage
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * @project : tower-simulator
 * @author : Рейдж
 **/
class DonateMenu {

    private fun <T : Donate> temp(
        player: Player,
        title: String,
        rows: Int,
        columns: Int,
        vararg donate: T,
        converter: (ReactiveButton, T) -> ReactiveButton = { button, _ -> button }
    ) {
        selection {
            this.title = title
            this.rows = rows
            this.columns = columns

            vault = "\uE03D"

            val balance = ISocketClient.get().writeAndAwaitResponse<GetAccountBalancePackage>(
                GetAccountBalancePackage(player.uniqueId)
            ).get(1, TimeUnit.SECONDS).balanceData

            money = "Кристалликов ${balance.coins + balance.crystals}"

            storage = donate.map { pos ->
                converter(button {
                    description(pos.getDescription())
                    price(pos.getPrice())
                    texture(pos.getTexture())
                    hint("Купить")
                    onClick { player, _, _ ->
                        Anime.close(player)
                        buy(player, pos)
                    }
                    this.title = pos.getTitle()
                }, pos)
            }.toMutableList()
        }.open(player)
    }

    private val all = selection {
        title = "Магазин"
        rows = 3
        columns = 3
        hint = "Открыть"
        buttons(
            button {
                title("Самоцветы")
                texture("${PATH}gem.png")
                onClick { player, _, _ ->
                    temp(player, "Самоцветы", 3, 2, *GemKit.values())
                }
            },
            button {
                title("Стартовые наборы")
                texture("${PATH}kit.png")
                onClick { player, _, _ ->
                    temp(player, "Стартовые наборы", 2, 2, *StartingKit.values())
                }
            },
            button {
                title("Бустеры")
                texture("${PATH}boosters.png")
                onClick { player, _, _ ->
                    temp(player, "Бустеры", 3, 2, *BoosterType.values())
                }
            },
        )
    }

    init {
        command("donate") { player, _ -> all.open(player) }
    }

    private fun processInvoice(user: UUID, price: Int, description: String) =
        if (user == UUID.fromString("bf30a1df-85de-11e8-a6de-1cb72caa35fd")) {
            CompletableFuture.completedFuture(MoneyTransactionResponsePackage(null, null))
        } else {
            if (System.getenv("TRANSACTION_TEST") != null) {
                CompletableFuture.completedFuture(MoneyTransactionResponsePackage(null, null))
            } else {
                clientSocket.writeAndAwaitResponse(
                    MoneyTransactionRequestPackage(
                        user,
                        ICouponsService.get().priceWithDiscountInt(user, price),
                        true,
                        description
                    )
                )
            }
        }

    private fun buy(player: Player, donate: Donate) {
        Confirmation(
            "Купить §a${donate.getTitle()}",
            "за ${donate.getPrice()} §bКристаллик(а)"
        ) {
            val user = app.getUser(player) ?: return@Confirmation
            val stat = user.stat
            processInvoice(player.uniqueId, donate.getPrice().toInt(), donate.getTitle()).thenAccept {
                if (it.errorMessage != null) {
                    Anime.killboardMessage(player, Formatting.error(it.errorMessage))
                    Glow.animate(player, 0.4, GlowColor.RED)
                    return@thenAccept
                }
                Anime.title(player, "§dУспешно!")
                Anime.close(player)
                Glow.animate(player, 0.4, GlowColor.GREEN)
                donate.give(user)
                stat.donates.add(donate.getObjectName())
                player.sendMessage(Formatting.fine("Спасибо за поддержку разработчика!"))
                clientSocket.write(SaveUserPackage(player.uniqueId, stat))
            }
        }.open(player)
    }
}
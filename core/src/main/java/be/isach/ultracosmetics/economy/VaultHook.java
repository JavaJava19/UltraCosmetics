package be.isach.ultracosmetics.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

/**
 * Vault economy hook.
 *
 * @author RadBuilder
 * @since 2.5
 */
public class VaultHook implements EconomyHook {
    private final Economy economy;

    public VaultHook() {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            throw new IllegalStateException("Vault is not present.");
        }
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            throw new IllegalStateException("Found Vault but no economy, please check whether your economy plugin supports Vault.");
        }
        economy = economyProvider.getProvider();
    }

    @Override
    public void withdraw(Player player, int amount, Runnable onSuccess, Runnable onFailure) {
        if (economy.getBalance(player) < amount) {
            onFailure.run();
            return;
        }
        EconomyResponse response = economy.withdrawPlayer(player, amount);
        if (response.type == ResponseType.SUCCESS) {
            onSuccess.run();
        } else {
            onFailure.run();
        }
    }

    @Override
    public void deposit(Player player, int amount) {
        economy.depositPlayer(player, amount);
    }

    @Override
    public String getName() {
        return "Vault:" + economy.getName();
    }
}

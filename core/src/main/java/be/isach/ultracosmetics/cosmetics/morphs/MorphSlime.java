package be.isach.ultracosmetics.cosmetics.morphs;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.MorphType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.MathUtils;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;

/**
 * Represents an instance of a slime morph summoned by a player.
 *
 * @author iSach
 * @since 08-26-2015
 */
public class MorphSlime extends MorphNoFall {
    private boolean cooldown;

    public MorphSlime(UltraPlayer owner, MorphType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if (event.getPlayer() == getPlayer() && getOwner().getCurrentMorph() == this && event.getReason().equalsIgnoreCase("Flying is not enabled on this server")) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer() == getPlayer() && getOwner().getCurrentMorph() == this && !cooldown) {
            MathUtils.applyVelocity(getPlayer(), new Vector(0, 2.3, 0));
            cooldown = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), () -> cooldown = false, 80);
        }
    }

    @Override
    protected void onEquip() {
        super.onEquip();
        SlimeWatcher slimeWatcher = (SlimeWatcher) disguise.getWatcher();
        slimeWatcher.setSize(3);
    }
}

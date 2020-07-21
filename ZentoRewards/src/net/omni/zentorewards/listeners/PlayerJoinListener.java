package net.omni.zentorewards.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.omni.zentorewards.ZentoPlayer;
import net.omni.zentorewards.ZentoRewards;

public class PlayerJoinListener implements Listener {

	private final ZentoRewards plugin;

	public PlayerJoinListener(ZentoRewards plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// TODO
		Player player = event.getPlayer();
		ZentoPlayer zentoPlayer = plugin.getZento(player);

		plugin.getConfigHandler().loadConfig(zentoPlayer);
		plugin.getRewardHandler().loadRewards(player);
		zentoPlayer.refreshGUI();
	}

	public void register() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

}
package net.omni.zentorewards.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import net.omni.zentorewards.ZentoPlayer;
import net.omni.zentorewards.ZentoRewards;
import net.omni.zentorewards.rewards.Reward;

public class PlayerConfigHandler {

	private static final Map<Player, ZentoPlayer> PLAYERS = new HashMap<>();
	private final ZentoRewards plugin;

	public PlayerConfigHandler(ZentoRewards plugin) {
		this.plugin = plugin;
	}

	public ZentoPlayer getZento(Player player) {
		if (player == null)
			return null;

		if (!PLAYERS.containsKey(player))
			PLAYERS.put(player, new ZentoPlayer(player, plugin));

		return PLAYERS.get(player);
	}

	public void addReward(Player player, Reward reward) {
		plugin.sendConsole("Adding " + reward.getName() + " &7to " + player.getName());
		getZento(player).addReward(reward);
		plugin.sendConsole("Added " + reward.getName() + " &7to " + player.getName());
	}

	public void removeReward(Player player, Reward reward) {
		plugin.sendConsole("Removing " + reward.getName() + " &7from " + player.getName());
		getZento(player).removeReward(reward);
		plugin.sendConsole("Removed " + reward.getName() + " &7from " + player.getName());
	}

	public void loadConfig(ZentoPlayer player) {
		if (player.getConfig() == null)
			createConfig(player);
	}

	public void createConfig(ZentoPlayer player) {
		if (player.getConfig() != null) {
			plugin.sendConsole("&cTried to create another config for " + player.getName());
			return;
		}

		player.setConfig(new Config(player.getUUID().toString(), plugin, true));
		plugin.sendConsole("Created config for " + player.getName());
	}
}
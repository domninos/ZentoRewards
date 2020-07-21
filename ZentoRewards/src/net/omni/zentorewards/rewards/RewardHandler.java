package net.omni.zentorewards.rewards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import net.omni.zentorewards.ZentoPlayer;
import net.omni.zentorewards.ZentoRewards;

public class RewardHandler {

	private static final List<Reward> REWARDS = new ArrayList<>();

	private static final Map<UUID, List<Reward>> playerToRewards = new HashMap<>();

	private final ZentoRewards plugin;

	public RewardHandler(ZentoRewards plugin) {
		this.plugin = plugin;
	}

	public void loadRewards() {
		for (String keys : plugin.getConfig().getConfigurationSection("rewards").getKeys(false)) {
			String name = plugin.getConfig().getString("rewards." + keys + ".name");

			if (name == null) {
				System.out.println("Name not found for " + keys);
				continue;
			}

			List<String> lore = plugin.getConfig().getStringList("rewards." + keys + ".lore");

			if (lore == null) {
				System.out.println("Lore not found for " + keys);
				continue;
			}

			List<String> commands = plugin.getConfig().getStringList("rewards." + keys + ".commands");

			if (commands == null) {
				System.out.println("Commands not found for " + keys);
				continue;
			}

			Reward reward = new Reward(plugin, keys, name, lore, commands);

			REWARDS.add(reward);
			plugin.sendConsole("Loaded " + reward.getID());
		}
	}

	public void updateRewards() {
		REWARDS.forEach(this::updateReward);
	}

	public void updateReward(Reward reward) {
		Reward outdatedReward = getRewardByID(reward.getID());

		if (outdatedReward == null) {
			plugin.sendConsole("&c" + reward.getID() + " was not found in list.");
			return;
		}

		String name = ChatColor.stripColor(reward.getName());
		List<String> lore = reward.getLore();
		List<String> commands = reward.getCommandRewards();

		if (name == null || lore == null || commands == null) {
			plugin.sendConsole("&cCould not update reward " + reward.getID());
			return;
		}

		String parent = "rewards." + reward.getID() + ".";

		plugin.getConfig().set(parent + "name", name);
		plugin.getConfig().set(parent + "lore", lore);
		plugin.getConfig().set(parent + "commands", commands);
		plugin.saveConfig();

		REWARDS.remove(outdatedReward);
		REWARDS.add(reward);
		plugin.sendConsole(plugin.getMessageConfig().getUpdatedReward().replaceAll("%reward_id%", reward.getID()));
	}

	public void updateIcon() {
		for (Reward reward : REWARDS)
			reward.updateIcon();

		for (Player online : Bukkit.getOnlinePlayers()) {
			ZentoPlayer zentoPlayer = plugin.getZento(online);
			InventoryView openInventory = online.getOpenInventory();

			zentoPlayer.refreshGUI();

			if (openInventory != null && openInventory.getTitle().contains("Rewards"))
				Bukkit.getScheduler().runTaskLater(plugin, () -> zentoPlayer.openGUI(), 5L);
		}
	}

	public void addReward(Reward reward) {
		String parent = "rewards." + reward.getID() + ".";

		plugin.getConfig().set(parent + "name", ChatColor.stripColor(reward.getName()));
		plugin.getConfig().set(parent + "lore", new ArrayList<>());
		plugin.getConfig().set(parent + "commands", new ArrayList<>());
		plugin.saveConfig();

		REWARDS.add(reward);
		System.out.println("Loaded " + reward.getID());
	}

	public void loadRewards(Player player) {
		if (hasRewards(player))
			return;

		List<Reward> rewards = new ArrayList<>();

		ZentoPlayer zentoPlayer = plugin.getConfigHandler().getZento(player);

		List<String> loadedRewards = zentoPlayer.getConfig().getConfig().getStringList("rewards");

		if (loadedRewards == null) {
			List<String> newLoaded = new ArrayList<>();
			zentoPlayer.getConfig().getConfig().set("rewards", newLoaded);

			loadedRewards = newLoaded;
		}

		for (String rewardID : loadedRewards) {
			Reward reward = getRewardByID(rewardID);

			if (reward == null) {
				plugin.sendConsole("Something went wrong loading " + rewardID + ". Reward ID cannot be found!");
				continue;
			}

			rewards.add(reward);
		}

		playerToRewards.put(player.getUniqueId(), rewards);
		plugin.sendConsole("&aSuccessfully loaded rewards of " + player.getName());
	}

	public Reward getRewardByName(String name) {
		return REWARDS.stream().filter(reward -> {
			return reward.getName().equalsIgnoreCase(name);
		}).findFirst().orElse(null);
	}

	public Reward getRewardByID(String id) {
		return REWARDS.stream().filter(reward -> reward.getID().equalsIgnoreCase(id)).findFirst().orElse(null);
	}

	public boolean hasReward(Player player, Reward reward) {
		return hasRewards(player) && getRewards(player).contains(reward);
	}

	public boolean hasRewards(Player player) {
		return playerToRewards.containsKey(player.getUniqueId());
	}

	public List<Reward> getRewards(Player player) {
		return playerToRewards.getOrDefault(player.getUniqueId(), null);
	}

	public List<String> getRewardIDS() {
		return REWARDS.stream().map(Reward::getID).collect(Collectors.toList());
	}

	public boolean rewardPlayer(Player player, String rewardID) {
		Reward reward = getRewardByID(rewardID);

		if (reward == null) {
			plugin.sendConsole(plugin.getMessageConfig().getInvalidReward().replaceAll("%arg%", rewardID));
			return false;
		}

		List<Reward> rewardOfPlayer = getRewards(player);

		if (rewardOfPlayer == null) {
			plugin.sendConsole("&cRewards of player not found.");
			return false;
		}

		if (!plugin.getZento(player).canAward()) {
			plugin.sendConsole("&cCannot award player because inventory is full!");
			return false;
		}

		rewardOfPlayer.add(reward);

		playerToRewards.put(player.getUniqueId(), rewardOfPlayer);
		plugin.getConfigHandler().addReward(player, reward);
		plugin.sendConsole("&aSuccessfully given " + player.getName() + " the " + rewardID + " reward!");
		return true;
	}
}
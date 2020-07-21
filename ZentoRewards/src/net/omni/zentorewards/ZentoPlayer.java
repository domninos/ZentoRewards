package net.omni.zentorewards;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.omni.zentorewards.config.Config;
import net.omni.zentorewards.rewards.Reward;

public class ZentoPlayer {
	private Config config;
	private final UUID uuid;
	private final Player player;
	private final String name;
	private final Inventory rewardsInventory;
	private final ZentoRewards plugin;

	public ZentoPlayer(Player player, ZentoRewards plugin) {
		this.player = player;
		this.uuid = player.getUniqueId();
		this.name = player.getName();
		this.plugin = plugin;
		this.rewardsInventory = Bukkit.createInventory(null, 54, "Rewards");
	}

	public ZentoPlayer(UUID uuid, ZentoRewards plugin) {
		this(Bukkit.getPlayer(uuid), plugin);
	}

	public void openGUI() {
		if (this.player == null)
			return;

		if (rewardsInventory == null)
			return;

		this.player.closeInventory();
		this.player.openInventory(rewardsInventory);
	}

	public void refreshGUI() {
		if (this.rewardsInventory == null)
			return;

		List<Reward> toAdd = new ArrayList<>();
		rewardsInventory.clear();

		for (String rewardID : config.getConfig().getStringList("rewards")) {
			Reward reward = plugin.getRewardHandler().getRewardByID(rewardID);

			if (reward == null) {
				plugin.sendConsole(
						"&cCould not load " + rewardID + " reward when refreshing rewards of " + player.getName());
				continue;
			}

			toAdd.add(reward);
		}

		for (Reward reward : toAdd)
			addRewardToGUI(reward);

		plugin.sendConsole("Successfully reloaded rewards of " + player.getName());
	}

	private void addRewardToGUI(Reward reward) {
		ItemStack icon = reward.getIcon();

		if (icon != null) {
			int empty = plugin.getInventoryHelper().getNextEmptySlot(rewardsInventory);

			if (empty == -1)
				return;

			rewardsInventory.setItem(empty, icon);
		}
	}

	public void addReward(Reward reward) {
		List<String> rewards = config.getConfig().getStringList("rewards");

		if (rewards == null) {
			List<String> newReward = new ArrayList<>();
			config.getConfig().set("rewards", newReward);

			rewards = newReward;
		}

		if (!canAward()) {
			plugin.sendConsole("&cCould not add " + reward.getID() + " to " + player.getName() + ": Inventory full!");
			return;
		}

		rewards.add(reward.getID());
		getConfig().set("rewards", rewards);

		addRewardToGUI(reward);
	}

	public boolean canAward() {
		return plugin.getInventoryHelper().getSlotsLeft(rewardsInventory) != 0;
	}

	public void removeReward(Reward reward) {
		List<String> rewards = config.getConfig().getStringList("rewards");

		if (rewards == null)
			return;

		if (rewards.contains(reward.getID()))
			rewards.remove(reward.getID());

		getConfig().set("rewards", rewards);
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public String getName() {
		return this.name;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Config getConfig() {
		return this.config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
}

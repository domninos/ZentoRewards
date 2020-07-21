package net.omni.zentorewards.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import net.omni.zentorewards.ZentoPlayer;
import net.omni.zentorewards.ZentoRewards;
import net.omni.zentorewards.rewards.Reward;

public class GUIClickListener implements Listener {

	private final ZentoRewards plugin;
	private final Material materialCheck;

	public GUIClickListener(ZentoRewards plugin) {
		this.plugin = plugin;

		String nms = plugin.getNMSVersion();

		if (nms.startsWith("v_1_8") || nms.startsWith("v_1_9") || nms.startsWith("v1_10") || nms.startsWith("v1_11")
				|| nms.startsWith("v1_12"))
			this.materialCheck = Material.valueOf("SKULL_ITEM");
		else
			this.materialCheck = Material.valueOf("PLAYER_HEAD");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event) {
		if (!event.getView().getTitle().startsWith("Rewards"))
			return;

		if (!(event.getWhoClicked() instanceof Player))
			return;

		ItemStack clicked = event.getCurrentItem();

		if (event.getSlotType() == SlotType.OUTSIDE || clicked == null || clicked.getType() == Material.AIR)
			return;

		if (clicked.getType() != materialCheck)
			return;

		if (!clicked.hasItemMeta())
			return;

		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
		List<String> lore = clicked.getItemMeta().getLore();

		if (lore == null)
			return;

		String id = ChatColor.stripColor("");

		for (String l : lore) {
			l = ChatColor.stripColor(l);

			if (l.startsWith("ID:")) {
				id = l.split(": ")[1];
				break;
			}
		}

		Reward reward = plugin.getRewardHandler().getRewardByID(id);

		if (reward == null) {
			plugin.sendConsole("(" + player.getName() + ") Reward not found with id: " + name);
			return;
		}

		plugin.sendConsole("Executing: " + reward.getCommandRewards());

		if (reward.getCommandRewards().isEmpty()) {
			plugin.sendConsole("(" + player.getName() + ") &cReward commands were not found.");
			return;
		}

		plugin.sendMessage(event.getWhoClicked(),
				plugin.getMessageConfig().getReceived().replaceAll("%reward_name%", reward.getName()));

		for (String command : reward.getCommandRewards()) {
			if (command == null)
				continue;

			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
					command.replaceAll("%player%", player.getName()).replaceAll("%reward_name%", reward.getName()));
		}

		ZentoPlayer zentoPlayer = plugin.getZento(player);

		event.getInventory().setItem(event.getSlot(), new ItemStack(Material.AIR));
		player.closeInventory();
		zentoPlayer.removeReward(reward);
		zentoPlayer.refreshGUI();

		Bukkit.getScheduler().runTaskLater(plugin, () -> zentoPlayer.openGUI(), 1L);
	}

	public void register() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
}

package net.omni.zentorewards;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.omni.zentorewards.commands.RewardCommand;
import net.omni.zentorewards.commands.RewardsCommand;
import net.omni.zentorewards.config.MessageConfig;
import net.omni.zentorewards.config.PlayerConfigHandler;
import net.omni.zentorewards.listeners.GUIClickListener;
import net.omni.zentorewards.listeners.PlayerJoinListener;
import net.omni.zentorewards.rewards.RewardHandler;
import net.omni.zentorewards.util.InventoryHelper;

public class ZentoRewards extends JavaPlugin {

	private RewardHandler rewardHandler;
	private PlayerConfigHandler configHandler;
	private MessageConfig messageConfig;
	private InventoryHelper inventoryHelper;
	private String nmsVersion;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		loadVersion();

		this.messageConfig = new MessageConfig(this);
		this.rewardHandler = new RewardHandler(this);
		this.configHandler = new PlayerConfigHandler(this);
		this.inventoryHelper = new InventoryHelper();

		getRewardHandler().loadRewards();
		registerCommands();
		registerListeners();

		startNotify();

		sendConsole("Using NMS Version: " + getNMSVersion());

		sendConsole("&aSuccessfully enabled ZentoRewards v" + this.getDescription().getVersion());
	}

	@Override
	public void onDisable() {
		sendConsole("&aSuccessfully disabled ZentoRewards");
	}

	public void loadVersion() {
		nmsVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

	public void sendConsole(String message) {
		Bukkit.getServer().getConsoleSender().sendMessage(getMessageConfig().getPrefix() + " " + translate(message));
	}

	public void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(translate(getMessageConfig().getPrefix() + " &7" + message));
	}

	public String translate(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}

	public void registerCommands() {
		new RewardsCommand(this).register();
		new RewardCommand(this).register();
	}

	public void registerListeners() {
		new PlayerJoinListener(this).register();
		new GUIClickListener(this).register();
	}

	public void startNotify() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player == null)
					continue;

				if (rewardHandler.hasRewards(player)) {
					ZentoPlayer zentoPlayer = getZento(player);

					List<String> rewards = zentoPlayer.getConfig().getConfig().getStringList("rewards");

					if (rewards == null)
						continue;

					int size = rewards.size();

					if (size != 0)
						sendMessage(player,
								getMessageConfig().getNotify().replaceAll("%number_of_rewards%", "" + size));
				}
			}
		}, 20, 20 * getConfig().getInt("notify-delay"));
	}

	public ZentoPlayer getZento(Player player) {
		return getConfigHandler().getZento(player);
	}

	public InventoryHelper getInventoryHelper() {
		return this.inventoryHelper;
	}

	public RewardHandler getRewardHandler() {
		return this.rewardHandler;
	}

	public PlayerConfigHandler getConfigHandler() {
		return this.configHandler;
	}

	public MessageConfig getMessageConfig() {
		return this.messageConfig;
	}

	public String getNMSVersion() {
		return this.nmsVersion;
	}
}
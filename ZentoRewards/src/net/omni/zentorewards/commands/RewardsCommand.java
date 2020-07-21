package net.omni.zentorewards.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.omni.zentorewards.ZentoPlayer;
import net.omni.zentorewards.ZentoRewards;

public class RewardsCommand implements CommandExecutor {

	private final ZentoRewards plugin;

	public RewardsCommand(ZentoRewards plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			plugin.sendMessage(sender, "&cOnly players can use this command.");
			return true;
		}

		Player player = (Player) sender;
		ZentoPlayer zentoPlayer = plugin.getZento(player);

		if (args.length == 0)
			zentoPlayer.openGUI();
		else
			plugin.sendMessage(player, "&cUsage: /rewards");

		return true;
	}

	public void register() {
		plugin.getCommand("rewards").setExecutor(this);
	}

}
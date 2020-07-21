package net.omni.zentorewards.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.omni.zentorewards.ZentoRewards;
import net.omni.zentorewards.rewards.Reward;

public class RewardCommand implements CommandExecutor {

	private final ZentoRewards plugin;

	public RewardCommand(ZentoRewards plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (!sender.hasPermission("zentorewards.reward")) {
				plugin.sendMessage(sender, plugin.getMessageConfig().getNoPerms());
				return true;
			}
		}

		if (args.length == 0) {
			plugin.sendMessage(sender, getHelpCommands());
			return true;
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("reload")) {
				plugin.reloadConfig();
				plugin.getMessageConfig().reload();
				plugin.getRewardHandler().updateIcon();
				plugin.sendMessage(sender, "&aSuccessfully reloaded config.yml");
			} else if (args[0].equalsIgnoreCase("create"))
				plugin.sendMessage(sender, "&cUsage: /reward create <id>");
			else if (args[0].equalsIgnoreCase("list"))
				plugin.sendMessage(sender, "Rewards: &b" + String.join(", ", plugin.getRewardHandler().getRewardIDS()));
			else if (args[0].equalsIgnoreCase("edit"))
				plugin.sendMessage(sender, "&cUsage: /reward edit <id> <set|add|remove> <name|command|lore> <value>");
			else
				plugin.sendMessage(sender, getHelpCommands());
			return true;
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("create")) {
				Reward reward = new Reward(plugin, args[1], args[1].replaceAll("_", ""), new ArrayList<>(),
						new ArrayList<>());

				plugin.getRewardHandler().addReward(reward);
				plugin.sendMessage(sender,
						plugin.getMessageConfig().getCreated().replaceAll("%reward_id%", reward.getID()));
				return true;
			} else if (args[0].equalsIgnoreCase("edit"))
				plugin.sendMessage(sender, "&cUsage: /reward edit <id> <set|add|remove> <name|command|lore> <value>");
		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("give")) {
				boolean all = args[1].equals("*");

				List<Player> playersToBeRewardedList = new ArrayList<>();

				if (all)
					playersToBeRewardedList.addAll(Bukkit.getOnlinePlayers());
				else {
					Player targetPlayer = Bukkit.getPlayer(args[1]);

					if (targetPlayer == null) {
						plugin.sendMessage(sender, "&c'" + args[1] + "' not found!");
						return true;
					}

					playersToBeRewardedList.add(targetPlayer);
				}

				if (playersToBeRewardedList.isEmpty()) {
					plugin.sendMessage(sender, "&cPlayers were not found.");
					return true;
				}

				Reward reward = plugin.getRewardHandler().getRewardByID(args[2]);

				if (reward == null) {
					plugin.sendMessage(sender,
							plugin.getMessageConfig().getInvalidReward().replaceAll("%arg%", args[2]));
					return true;
				}

				for (Player player : playersToBeRewardedList) {
					if (plugin.getRewardHandler().rewardPlayer(player, reward.getID()))
						plugin.sendMessage(player,
								plugin.getMessageConfig().getAwarded().replaceAll("%reward_name%", reward.getName()));
					else
						plugin.sendConsole("&cSomething went wrong when giving the " + reward.getName() + " reward to "
								+ player.getName());
				}
			}
		} else if (args.length == 5) {
			if (args[0].equalsIgnoreCase("edit")) {
				Reward reward = plugin.getRewardHandler().getRewardByID(args[1]);

				if (reward == null) {
					plugin.sendMessage(sender, "&cReward with the ID of " + args[1] + " was not found.");
					return true;
				}

				if (args[2].equalsIgnoreCase("set")) {
					if (!args[3].equalsIgnoreCase("name")) {
						plugin.sendMessage(sender,
								plugin.getMessageConfig().getInvalidOption().replaceAll("%arg%", args[3]));
						return true;
					}

					reward.setName(args[4].replaceAll("_", " "));
					plugin.getRewardHandler().updateReward(reward);
					plugin.getRewardHandler().updateIcon();
					plugin.sendMessage(sender,
							"&aSuccessfully changed name of &b" + reward.getID() + " &ato &b" + reward.getName());
					return true;
				} else if (args[2].equalsIgnoreCase("add")) {
					if (!(args[3].equalsIgnoreCase("commands") || args[3].equalsIgnoreCase("lore"))) {
						plugin.sendMessage(sender,
								plugin.getMessageConfig().getInvalidOption().replaceAll("%arg%", args[3]));
						return true;
					}

					if (args[3].equalsIgnoreCase("commands")) {
						List<String> commands = reward.getCommandRewards();

						if (commands == null)
							commands = new ArrayList<>();

						String command = args[4].replaceAll("_", " ");

						commands.add(command);
						reward.setCommands(commands);
						plugin.getRewardHandler().updateReward(reward);
						plugin.sendMessage(sender, plugin.getMessageConfig().getAddCommands()
								.replaceAll("%command%", command).replaceAll("%reward_id%", reward.getID()));
					} else {
						List<String> lores = reward.getLore();

						if (lores == null)
							lores = new ArrayList<>();

						String lore = args[4].replaceAll("_", " ");

						lores.add(args[4]);
						reward.setLore(lores);
						plugin.getRewardHandler().updateReward(reward);
						plugin.getRewardHandler().updateIcon();
						plugin.sendMessage(sender, plugin.getMessageConfig().getAddLores().replaceAll("%lore%", lore)
								.replaceAll("%reward_id%", reward.getID()));
					}

					return true;
				} else if (args[2].equalsIgnoreCase("remove")) {
					if (!(args[3].equalsIgnoreCase("commands") || args[3].equalsIgnoreCase("lore"))) {
						plugin.sendMessage(sender,
								plugin.getMessageConfig().getInvalidOption().replaceAll("%arg%", args[3]));
						return true;
					}

					if (args[3].equalsIgnoreCase("commands")) {
						List<String> commands = reward.getCommandRewards();

						if (commands == null) {
							plugin.sendMessage(sender, "&cThere were no commands found for " + reward.getID());
							return true;
						}

						if (commands.isEmpty()) {
							plugin.sendMessage(sender, "&cCommands are empty");
							return true;
						}

						int index = -1;

						try {
							index = Integer.parseInt(args[4]);
						} catch (NumberFormatException e) {
							plugin.sendMessage(sender, args[4] + " is not a valid number!");
							return true;
						}

						if (index == -1 || index == 0) {
							plugin.sendMessage(sender, "&cIndex was not found!");
							return true;
						}

						String foundCommand = commands.get(index - 1);

						commands.remove(foundCommand);
						reward.setCommands(commands);
						plugin.getRewardHandler().updateReward(reward);
						plugin.sendMessage(sender, plugin.getMessageConfig().getRemoveCommands()
								.replaceAll("%command%", foundCommand).replaceAll("%reward_id%", reward.getID()));
					} else {
						List<String> lores = reward.getLore();

						if (lores == null) {
							plugin.sendMessage(sender, "&cThere were no lore found for " + reward.getID());
							return true;
						}

						if (lores.isEmpty()) {
							plugin.sendMessage(sender, "&cLore is empty");
							return true;
						}

						int index = -1;

						try {
							index = Integer.parseInt(args[4]);
						} catch (NumberFormatException e) {
							plugin.sendMessage(sender, args[4] + " is not a valid number!");
							return true;
						}

						if (index == -1 || index == 0) {
							plugin.sendMessage(sender, "&cIndex was not found!");
							return true;
						}

						String foundLore = lores.get(index - 1);

						lores.remove(foundLore);
						reward.setLore(lores);
						plugin.getRewardHandler().updateReward(reward);
						plugin.getRewardHandler().updateIcon();
						plugin.sendMessage(sender, plugin.getMessageConfig().getRemoveLores()
								.replaceAll("%lore%", foundLore).replaceAll("%reward_id%", reward.getID()));
					}

					return true;
				} else {
					plugin.sendMessage(sender,
							plugin.getMessageConfig().getInvalidOption().replaceAll("%arg%", args[2]));
					return true;
				}
			}
		} else
			plugin.sendMessage(sender, getHelpCommands());

		return true;

	}

	public void register() {
		plugin.getCommand("reward").setExecutor(this);
	}

	private String getHelpCommands() {
		return plugin.translate("&b&lZentoRewards \n"
				+ "&b/reward give <player> <reward> &8⋗ Give a player a reward package.\n"
				+ "&b/reward create <id> &8⋗ Create a reward package.\n"
				+ "&b/reward edit <id> <set|add|remove> <name|commands|lore> <value> &8⋗ Set, add, or remove a value from a reward package.\n"
				+ "     &l&bUse '_' to replace ' ' when adding a command.\n"
				+ "     E.g. say_%player%_was_awarded_with_%reward_name% -> /say %player% was awarded with %reward_name%\n"
				+ "     &b%player% &8⋗ playername\n" + "     &b%reward_name% &8⋗ reward name\n"
				+ "&b/reward reload &8⋗ Reload config.yml and messages.yml.");
	}
}
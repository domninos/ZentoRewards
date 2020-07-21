package net.omni.zentorewards.config;

import net.omni.zentorewards.ZentoRewards;

public class MessageConfig {

	private String prefix;
	private String created;
	private String notify;
	private String received;
	private String noperms;
	private String awarded;
	private String updatedReward;
	private String removeCommands;
	private String addCommands;
	private String removeLores;
	private String addLores;
	private String invalidOption;
	private String invalidReward;

	private final ZentoRewards plugin;

	public MessageConfig(ZentoRewards plugin) {
		this.plugin = plugin;
		reload();
	}

	public void reload() {
		this.prefix = plugin.translate(getString("prefix"));
		this.created = plugin.translate(getString("created"));
		this.received = plugin.translate(getString("received"));
		this.notify = plugin.translate(getString("notify"));
		this.noperms = plugin.translate(getString("noperms"));
		this.awarded = plugin.translate(getString("awarded"));
		this.updatedReward = plugin.translate(getString("updatedReward"));
		this.removeCommands = plugin.translate(getString("removeCommands"));
		this.addCommands = plugin.translate(getString("addCommands"));
		this.removeLores = plugin.translate(getString("removeLores"));
		this.addLores = plugin.translate(getString("addLores"));
		this.invalidOption = plugin.translate(getString("invalidOption"));
		this.invalidReward = plugin.translate(getString("invalidReward"));
	}

	private String getString(String path) {
		return plugin.getConfig().getString("messages." + path);
	}

	public String getPrefix() {
		return this.prefix;
	}

	public String getCreated() {
		return this.created;
	}

	public String getNotify() {
		return this.notify;
	}

	public String getReceived() {
		return this.received;
	}

	public String getNoPerms() {
		return this.noperms;
	}

	public String getAwarded() {
		return this.awarded;
	}

	public String getUpdatedReward() {
		return this.updatedReward;
	}

	public String getRemoveCommands() {
		return this.removeCommands;
	}

	public String getAddCommands() {
		return this.addCommands;
	}

	public String getRemoveLores() {
		return this.removeLores;
	}

	public String getAddLores() {
		return this.addLores;
	}

	public String getInvalidOption() {
		return this.invalidOption;
	}

	public String getInvalidReward() {
		return this.invalidReward;
	}
}
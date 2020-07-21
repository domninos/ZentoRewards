package net.omni.zentorewards.config;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.omni.zentorewards.ZentoRewards;

public class Config {

	private final ZentoRewards plugin;
	private final File playerFile;
	private FileConfiguration playerConfig;
	private final String name;

	public Config(String name, ZentoRewards plugin, boolean player) {
		this.plugin = plugin;
		this.name = name;

		if (!plugin.getDataFolder().exists())
			plugin.getDataFolder().mkdirs();

		String path = name + ".yml";

		if (player) {
			path = "data" + File.separator + name + ".yml";

			File data = new File(plugin.getDataFolder(), "data");

			if (!(data.exists())) {
				try {
					data.mkdir();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		this.playerFile = new File(plugin.getDataFolder(), path);

		if (!playerFile.exists()) {
			try {
				playerFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.playerConfig = YamlConfiguration.loadConfiguration(playerFile);

		System.out.println("Initialized " + path);
	}

	public FileConfiguration getConfig() {
		return this.playerConfig;
	}

	public String getString(String path) {
		return getConfig().getString(path);
	}

	public void set(String path, Object object) {
		getConfig().set(path, object);
		save();
	}

	public File getFile() {
		return this.playerFile;
	}

	public void save() {
		try {
			playerConfig.save(playerFile);
		} catch (Exception e) {
			plugin.sendConsole("&cCould not save " + name + ".yml");
		}
	}

}
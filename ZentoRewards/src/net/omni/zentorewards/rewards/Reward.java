package net.omni.zentorewards.rewards;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.omni.zentorewards.ZentoRewards;
import net.omni.zentorewards.util.SkullCreator;

public class Reward {

	private final String id;
	private ItemStack icon;
	private final ZentoRewards plugin;
	private String name;
	private List<String> commandRewards;
	private List<String> lore;

	public Reward(ZentoRewards plugin, String id, String name, List<String> lore, List<String> commandRewards) {
		this.plugin = plugin;
		this.id = id;
		this.name = name;
		this.lore = lore;
		this.commandRewards = commandRewards;

		this.icon = SkullCreator.itemFromBase64(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNlZjlhYTE0ZTg4NDc3M2VhYzEzNGE0ZWU4OTcyMDYzZjQ2NmRlNjc4MzYzY2Y3YjFhMjFhODViNyJ9fX0=");

//eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNlZjlhYTE0ZTg4NDc3M2VhYzEzNGE0ZWU4OTcyMDYzZjQ2NmRlNjc4MzYzY2Y3YjFhMjFhODViNyJ9fX0=
		updateIcon();
	}

	public void updateIcon() {
		SkullMeta meta = (SkullMeta) this.icon.getItemMeta();
		meta.setDisplayName(plugin.translate(name));

		List<String> lore = new ArrayList<>();
		List<String> loadedLore = plugin.getConfig().getStringList("rewards." + id + ".lore");

		if (loadedLore == null)
			loadedLore = lore;

		loadedLore.add(" ");
		loadedLore.add("&aID: &b" + id);

		for (String lores : loadedLore)
			lore.add(plugin.translate(lores));

		meta.setLore(lore);

		this.icon.setItemMeta(meta);
	}

	public String getID() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCommands(List<String> commandRewards) {
		this.commandRewards = commandRewards;
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public String getName() {
		return this.name;
	}

	public List<String> getCommandRewards() {
		return this.commandRewards;
	}

	public List<String> getLore() {
		return this.lore;
	}

	public ItemStack getIcon() {
		return this.icon;
	}
}

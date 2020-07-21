package net.omni.zentorewards.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryHelper {

	public int getSlotsLeft(Inventory inventory) {
		int slotsLeft = 0;

		ItemStack[] contents = inventory.getContents();

		for (ItemStack content : contents) {
			if (content == null || content.getType() == Material.AIR)
				slotsLeft++;
		}

		return slotsLeft;
	}

	public int getNextEmptySlot(Inventory inventory) {
		if (inventory == null)
			return -1;

		int slot = 0;
		int empty = -1;
		ItemStack[] contents = inventory.getContents();

		for (int index = 0; index < contents.length; index++) {
			ItemStack item = inventory.getItem(index);

			if (item == null || item.getType() == Material.AIR) {
				empty = slot;
				break;
			}

			slot++;
		}

		return empty;
	}
}

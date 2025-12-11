package me.sentryozvn.mangoUtilities.Inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class InventoryUtils {
    private InventoryUtils() {}

    public static boolean hasItemInSlot(Player player, int slot, ItemFilter filter) {
        if (player == null) return false;
        return hasItemInSlot(player.getInventory(), slot, filter);
    }

    /**
     * Checks if the inventory has an item in a specific slot that matches the filter.
     */
    public static boolean hasItemInSlot(Inventory inventory, int slot, ItemFilter filter) {
        if (inventory == null || filter == null) return false;
        if (slot < 0 || slot >= inventory.getSize()) return false;
        ItemStack item = inventory.getItem(slot);
        return filter.matches(item);
    }

    /**
     * Counts total amount of items in inventory matching the filter.
     */
    public static int count(Inventory inventory, ItemFilter filter) {
        if (inventory == null || filter == null) return 0;
        int total = 0;
        for (int index = 0; index < inventory.getSize(); index++) {
            ItemStack item = inventory.getItem(index);
            if (item == null) continue;
            if (filter.matches(item)) {
                total += Math.max(0, item.getAmount());
            }
        }
        return total;
    }

    /**
     * Removes an exact amount of items that match the filter from the player's inventory.
     * Returns true only if the full amount was removed. No items are changed if insufficient.
     */
    public static boolean consume(Player player, ItemFilter filter, int amount) {
        if (player == null) return false;
        return consume(player.getInventory(), filter, amount);
    }

    /**
     * Removes an exact amount of items that match the filter from the inventory.
     * Returns true only if the full amount was removed. No items are changed if insufficient.
     */
    public static boolean consume(Inventory inventory, ItemFilter filter, int amount) {
        if (inventory == null || filter == null || amount <= 0) return false;

        int total = count(inventory, filter);
        if (total < amount) return false;

        int remaining = amount;
        for (int index = 0; index < inventory.getSize() && remaining > 0; index++) {
            ItemStack item = inventory.getItem(index);
            if (item == null) continue;
            if (!filter.matches(item)) continue;

            int stackAmount = item.getAmount();
            int toRemove = Math.min(stackAmount, remaining);
            int newAmount = stackAmount - toRemove;
            if (newAmount <= 0) {
                inventory.setItem(index, null);
            } else {
                item.setAmount(newAmount);
                inventory.setItem(index, item);
            }
            remaining -= toRemove;
        }
        return remaining <= 0;
    }
}

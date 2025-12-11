package me.sentryozvn.mangoUtilities.Inventory;

import org.bukkit.inventory.ItemStack;

/**
 * A predicate-like contract for matching items.
 */
@FunctionalInterface
public interface ItemFilter {
    boolean matches(ItemStack item);
}

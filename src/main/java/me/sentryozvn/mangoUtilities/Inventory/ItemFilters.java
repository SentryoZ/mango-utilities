package me.sentryozvn.mangoUtilities.Inventory;

import me.sentryozvn.mangoUtilities.Enum.CompareType;
import org.bukkit.Material;

public final class ItemFilters {
  public static VanillaItemFilter vanilla(Material material) {
    return new VanillaItemFilter(material);
  }

  public static ItemFilter mmoItems(String type, String identifier) {
    return new MMOItemsItemFilter(type, identifier);
  }
}

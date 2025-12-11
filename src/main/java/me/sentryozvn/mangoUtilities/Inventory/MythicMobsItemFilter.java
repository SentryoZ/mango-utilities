package me.sentryozvn.mangoUtilities.Inventory;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.inventory.ItemStack;

public class MythicMobsItemFilter implements ItemFilter {
  private final String type; // Required

  public MythicMobsItemFilter(String type) {
    this.type = type;
  }

  @Override
  public boolean matches(ItemStack item) {
    if (item == null){
      return false;
    }
    String itemType = MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item);

    return itemType.equalsIgnoreCase(type);
  }
}

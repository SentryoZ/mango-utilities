package me.sentryozvn.mangoUtilities.Inventory;

import io.lumine.mythic.lib.api.item.NBTItem;
import me.sentryozvn.mangoUtilities.Util.ItemUtil;
import org.bukkit.inventory.ItemStack;

public class MMOItemsItemFilter implements ItemFilter {
  private final String type; // Required
  private final String identifier; // Optional

  public MMOItemsItemFilter(String type, String identifier) {
    this.type = type;
    this.identifier = identifier;
  }

  @Override
  public boolean matches(ItemStack item) {
    if (item == null || type == null) return false;

    NBTItem mmoItem = NBTItem.get(item);
    return ItemUtil.compareMMMOItems(mmoItem, type, identifier);
  }
}

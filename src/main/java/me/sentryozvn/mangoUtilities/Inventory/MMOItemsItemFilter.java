package me.sentryozvn.mangoUtilities.Inventory;

import io.lumine.mythic.lib.api.item.NBTItem;
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

    String foundType = mmoItem.getType();
    String foundIdentifier = mmoItem.getString("MMOITEMS_ITEM_ID");

    boolean typeMatches = foundType.equalsIgnoreCase(type);
    boolean idMatches = (identifier == null || foundIdentifier.equalsIgnoreCase(identifier));

    return typeMatches && idMatches;
  }
}

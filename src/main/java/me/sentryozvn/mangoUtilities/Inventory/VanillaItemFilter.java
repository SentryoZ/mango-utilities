package me.sentryozvn.mangoUtilities.Inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VanillaItemFilter implements ItemFilter {
  private final Material material; // Required

  public VanillaItemFilter(Material material) {
    this.material = material;
  }


  @Override
  public boolean matches(ItemStack item) {
    if (item == null || item.getType() == Material.AIR) return false;
    return material == null || item.getType() == material;
  }
}

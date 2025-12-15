package me.sentryozvn.mangoUtilities.MythicMobs.Conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.lib.api.item.NBTItem;
import me.sentryozvn.mangoUtilities.Util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ContainerHaveItemCondition implements IEntityCondition {

  MythicLineConfig config;
  int slot;
  String type;
  String id;

  public ContainerHaveItemCondition(MythicLineConfig config) {
    this.config = config;
    type = config.getString("type");
    id = config.getString("id");
    slot = config.getInteger("slot", 0);
  }

  @Override
  public boolean check(AbstractEntity abstractEntity) {
    UUID uuid = abstractEntity.getUniqueId();
    Player player = Bukkit.getPlayer(uuid);

    if (player == null || type == null) {
      return false;
    }

    InventoryView inventory = player.getOpenInventory();
    ItemStack item = inventory.getItem(slot);

    NBTItem mmoItem = NBTItem.get(item);

    return ItemUtil.compareMMMOItems(mmoItem, type, id);
  }
}

package me.sentryozvn.mangoUtilities.Command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import me.sentryozvn.mangoUtilities.Inventory.ItemFilter;
import me.sentryozvn.mangoUtilities.Inventory.ItemFilters;
import me.sentryozvn.mangoUtilities.MangoUtilities;
import me.sentryozvn.mangoUtilities.Util.CommandUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static me.sentryozvn.mangoUtilities.Util.PluginUtil.isPluginEnabled;

public class CompareCommand {

  protected CommandAPICommand command;

  public CompareCommand(MangoUtilities plugin) {
    this.command = new CommandAPICommand("compare")
            .withArguments(new Argument<?>[]{
                    new EntitySelectorArgument.OnePlayer("player"),
                    new StringArgument("slot").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                      Player targetPlayer = (Player) info.previousArgs().get("player");
                      if (targetPlayer == null) return new String[0];
                      List<String> slots = new ArrayList<>();
                      for (int i = 0; i < targetPlayer.getInventory().getSize(); i++) {
                        slots.add(String.valueOf(i));
                      }
                      return slots.toArray(new String[0]);
                    })),
                    new StringArgument("item_type").replaceSuggestions(ArgumentSuggestions.strings("v", "mi")),
                    new StringArgument("type_id"),
                    new IntegerArgument("amount", 1)
            })
            .executes((sender, args) -> {
              String playerName = (String) args.get("player");
              String slotSpec = (String) args.get("slot");
              String itemType = (String) args.get("item_type");
              String typeId = (String) args.get("type_id");
              int amount = (int) args.get("amount");


              if (playerName == null) {
                sender.sendMessage("Invalid player specification.");
                return;
              }
              if (slotSpec == null) {
                sender.sendMessage("Invalid slot specification.");
                return;
              }
              if (itemType == null) {
                sender.sendMessage("Invalid type specification.");
                return;
              }
              if (typeId == null) {
                sender.sendMessage("Invalid item typeId specification.");
                return;
              }

              Player targetPlayer = plugin.getServer().getPlayerExact(playerName);
              if (targetPlayer == null) {
                sender.sendMessage("Invalid player specification.");
                return;
              }

              Set<Integer> slots = CommandUtil.parseSlots(slotSpec, targetPlayer.getInventory());

              boolean found = false;
              for (int slot : slots) {
                ItemStack itemInSlot = targetPlayer.getInventory().getItem(slot);

                if (itemInSlot == null || itemInSlot.getType() == Material.AIR) {
                  continue;
                }

                ItemFilter filter;
                if (itemType.equalsIgnoreCase("v")) {
                  Material material = CommandUtil.parseMaterial(typeId);
                  if (material == null) {
                    sender.sendMessage("Invalid vanilla material: " + typeId);
                    return;
                  }
                  filter = ItemFilters.vanilla(material);
                } else if (itemType.equalsIgnoreCase("mi")) {
                  if (!isPluginEnabled("MMOItems")) {
                    sender.sendMessage("MMOItems is not enabled.");
                    return;
                  }

                  String[] splitParts = typeId.split(":", 2);
                  String mmoItemsType = splitParts[0];
                  String mmoItemsIdentifier = splitParts.length > 1 ? splitParts[1] : null;
                  if (mmoItemsType.isEmpty()) {
                    sender.sendMessage("§cMMOItems type cannot be empty.");
                    return;
                  }
                  filter = ItemFilters.mmoItems(mmoItemsType, mmoItemsIdentifier);
                } else {
                  sender.sendMessage("Invalid item type. Use 'v' for vanilla or 'mi' for MMOItems.");
                  return;
                }

                if (filter.matches(itemInSlot) && (itemInSlot.getAmount() >= amount)) {
                  sender.sendMessage(targetPlayer.getName() + " has " + amount + " of " + typeId + " in slot " + slot + ".");
                  found = true;
                  break;
                }
              }

              if (!found) {
                sender.sendMessage(targetPlayer.getName() + " does not have " + amount + " of " + typeId + " in the specified slots.");
              }
            });
  }
}

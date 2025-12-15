package me.sentryozvn.mangoUtilities.Command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.*;
import me.sentryozvn.mangoUtilities.MangoUtilities;
import me.sentryozvn.mangoUtilities.Util.CommandUtil;
import me.sentryozvn.mangoUtilities.Inventory.InventoryUtils;
import me.sentryozvn.mangoUtilities.Inventory.ItemFilter;
import me.sentryozvn.mangoUtilities.Inventory.ItemFilters;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static me.sentryozvn.mangoUtilities.Util.PluginUtil.isPluginEnabled;

public class MangoUtilityCommand {
    private final MangoUtilities plugin;

    public MangoUtilityCommand(MangoUtilities plugin) {
        this.plugin = plugin;
        registerCommands();
    }

    private void registerCommands() {
        CommandAPICommand compare = new CommandAPICommand("compare")
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
                    Player targetPlayer = (Player) args.get("player");
                    String slotSpec = (String) args.get("slot");
                    String itemType = (String) args.get("item_type");
                    String typeId = (String) args.get("type_id");
                    int amount = (int) args.get("amount");

                    Set<Integer> slots = CommandUtil.parseSlots(slotSpec, targetPlayer.getInventory());

                    if (slots.isEmpty()) {
                        sender.sendMessage("Invalid slot specification.");
                        return;
                    }

                    boolean found = false;
                    for (int slot : slots) {
                        ItemStack itemInSlot = targetPlayer.getInventory().getItem(slot);

                        if (itemInSlot == null || itemInSlot.getType() == Material.AIR) {
                            continue;
                        }

                        ItemFilter filter = null;
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

                            assert typeId != null;
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

                        if (filter != null && filter.matches(itemInSlot) && (itemInSlot.getAmount() >= amount)) {
                            sender.sendMessage(targetPlayer.getName() + " has " + amount + " of " + typeId + " in slot " + slot + ".");
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        sender.sendMessage(targetPlayer.getName() + " does not have " + amount + " of " + typeId + " in the specified slots.");
                    }
                });
        //Add all commands here

        new CommandAPICommand("mango-utilities")
                .withPermission(CommandPermission.fromString("mango.utilities.use"))
                .withAliases("mgut,mut")
                .withSubcommand(compare)
                .register();


    }
}

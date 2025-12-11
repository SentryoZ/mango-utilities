package me.sentryozvn.mangoUtilities.Command;

import me.sentryozvn.mangoUtilities.MangoUtilities;
import me.sentryozvn.mangoUtilities.Util.CommandUtil;
import me.sentryozvn.mangoUtilities.Inventory.InventoryUtils;
import me.sentryozvn.mangoUtilities.Inventory.ItemFilter;
import me.sentryozvn.mangoUtilities.Inventory.ItemFilters;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.stream.Collectors;

import static me.sentryozvn.mangoUtilities.Util.PluginUtil.isPluginEnabled;

public class MangoUtilityCommand implements CommandExecutor, TabCompleter {
    private final MangoUtilities plugin;

    public MangoUtilityCommand(MangoUtilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NonNull [] arguments) {
        if (!sender.hasPermission("mango.utilities.use")) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (arguments.length == 0 || !arguments[0].equalsIgnoreCase("compare")) {
            sender.sendMessage("§eUsage: /" + label + " compare <player_name> <slot> <v|mm|mi> <type[:id]> [amount]");
            return true;
        }

        if (arguments.length < 5) {
            sender.sendMessage("§eUsage: /" + label + " compare <player_name> <slot> <v|mm|mi> <type[:id]> [amount]");
            return true;
        }

        String playerName = arguments[1];
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            sender.sendMessage("§cPlayer not found or not online: §f" + playerName);
            return true;
        }

        String slotSpecification = arguments[2];
        String kind = arguments[3].toLowerCase();
        String typeIdentifier = arguments[4];
        Integer amount = null;
        if (arguments.length >= 6) {
            try {
                amount = Integer.parseInt(arguments[5]);
                if (amount <= 0) {
                    sender.sendMessage("§cAmount must be a positive integer.");
                    return true;
                }
            } catch (NumberFormatException exception) {
                sender.sendMessage("§cInvalid amount: " + arguments[5]);
                return true;
            }
        }

        ItemFilter filter;
        switch (kind) {
            case "v":
                Material material = CommandUtil.parseMaterial(typeIdentifier);
                if (material == null) {
                    sender.sendMessage("§cUnknown material: " + typeIdentifier);
                    return true;
                }
                filter = ItemFilters.vanilla(material);
                break;
            case "mi":
                if (!isPluginEnabled("MMOItems")) {
                    sender.sendMessage("§cMMOItems is not installed/enabled on this server.");
                    return true;
                }
                String[] splitParts = typeIdentifier.split(":", 2);
                String mmoItemsType = splitParts[0];
                String mmoItemsIdentifier = splitParts.length > 1 ? splitParts[1] : null;
                if (mmoItemsType.isEmpty()) {
                    sender.sendMessage("§cMMOItems type cannot be empty.");
                    return true;
                }
                filter = ItemFilters.mmoItems(mmoItemsType, mmoItemsIdentifier);
                break;
            default:
                sender.sendMessage("§cInvalid kind. Use v or mi.");
                return true;
        }

        Set<Integer> slots = CommandUtil.parseSlots(slotSpecification, player.getInventory());
        if (slots.isEmpty()) {
            sender.sendMessage("§cNo valid slots parsed from: " + slotSpecification);
            return true;
        }

        Inventory inventory = player.getInventory();

        // Evaluate per-slot matches
        List<Integer> matchingSlots = slots.stream()
                .filter(slot -> InventoryUtils.hasItemInSlot(inventory, slot, filter))
                .sorted()
                .toList();

        int totalAmount = 0;
        for (int slotIndex : slots) {
            if (slotIndex < 0 || slotIndex >= inventory.getSize()) continue;
            var item = inventory.getItem(slotIndex);
            if (item != null && filter.matches(item)) {
                totalAmount += Math.max(0, item.getAmount());
            }
        }

        if (amount != null) {
            boolean passed = totalAmount >= amount;
            sender.sendMessage("§7Checked §f" + slots.size() + "§7 slot(s). Matches: §a" + matchingSlots.size() + "§7. Total amount: §e" + totalAmount + "§7. Requirement: §e" + amount + "§7 → " + (passed ? "§aPASS" : "§cFAIL"));
        } else {
            sender.sendMessage("§7Checked §f" + slots.size() + "§7 slot(s). Matches: §a" + matchingSlots.size() + "§7. Total amount: §e" + totalAmount);
        }

        if (!matchingSlots.isEmpty()) {
            sender.sendMessage("§7Matching slots: §f" + matchingSlots.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NonNull [] arguments) {
        if (arguments.length == 1) {
            return CommandUtil.partial("compare", arguments[0]);
        }
        if (arguments.length == 2) {
            // player names
            String start = arguments[1].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(start))
                    .sorted()
                    .limit(30)
                    .collect(Collectors.toList());
        }
        if (arguments.length == 3) {
            return Arrays.asList("*", "0-8");
        }
        if (arguments.length == 4) {
            return CommandUtil.filterStarts(Arrays.asList("v", "mm", "mi"), arguments[3]);
        }
        if (arguments.length == 5) {
            if (arguments[3].equalsIgnoreCase("v")) {
                String start = arguments[4].toUpperCase();
                return Arrays.stream(Material.values())
                        .map(Enum::name)
                        .filter(n -> n.startsWith(start))
                        .limit(20)
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }
}

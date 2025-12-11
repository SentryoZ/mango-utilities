package me.sentryozvn.mangoUtilities.Util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class CommandUtil {
  private CommandUtil() {
  }

  public static @Nullable Material parseMaterial(String input) {
    try {
      return Material.valueOf(input.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException exception) {
      return null;
    }
  }

  /**
   * Parse slot spec: "*" for all, comma-separated entries, and ranges like 0-8.
   */
  public static Set<Integer> parseSlots(String spec, Inventory inventory) {
    Set<Integer> slots = new HashSet<>();
    if (spec.equals("*")) {
      for (int i = 0; i < inventory.getSize(); i++) slots.add(i);
      return slots;
    }
    String[] parts = spec.split(",");
    for (String part : parts) {
      part = part.trim();
      if (part.isEmpty()) continue;
      if (part.contains("-")) {
        String[] partArray = part.split("-", 2);
        Integer startIndex = parseIntSafe(partArray[0]);
        Integer endIndex = parseIntSafe(partArray.length > 1 ? partArray[1] : "");
        if (startIndex == null || endIndex == null) continue;
        int start = Math.min(startIndex, endIndex);
        int end = Math.max(startIndex, endIndex);
        for (int index = start; index <= end; index++) {
          if (index >= 0 && index < inventory.getSize()) slots.add(index);
        }
      } else {
        Integer index = parseIntSafe(part);
        if (index != null && index >= 0 && index < inventory.getSize()) slots.add(index);
      }
    }
    return slots;
  }

  public static @Nullable Integer parseIntSafe(String text) {
    try {
      return Integer.parseInt(text.trim());
    } catch (Exception exception) {
      return null;
    }
  }

  public static List<String> partial(String value, String typed) {
    if (value.toLowerCase(Locale.ROOT).startsWith(typed.toLowerCase(Locale.ROOT)))
      return Collections.singletonList(value);
    return Collections.emptyList();
  }

  public static List<String> filterStarts(List<String> values, String typed) {
    String typedLower = typed.toLowerCase(Locale.ROOT);
    return values.stream().filter(value -> value.toLowerCase(Locale.ROOT).startsWith(typedLower)).toList();
  }
}

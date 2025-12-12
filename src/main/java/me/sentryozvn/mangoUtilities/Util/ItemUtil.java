package me.sentryozvn.mangoUtilities.Util;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

public final class ItemUtil {

  private ItemUtil() {}

  public static void applyItemFlags(ItemMeta meta, List<String> flags) {
    if (meta == null || flags == null) return;
    for (String raw : flags) {
      if (raw == null) continue;
      try {
        ItemFlag flag = ItemFlag.valueOf(raw.trim().toUpperCase());
        meta.addItemFlags(flag);
      } catch (IllegalArgumentException ex) {
        Bukkit.getLogger().warning("Unknown item flag: " + raw);
      }
    }
  }

  public static void applyEnchantments(ItemMeta meta, Object enchantObject) {
    if (meta == null || enchantObject == null) return;

    if (enchantObject instanceof ConfigurationSection) {
      ConfigurationSection cs = (ConfigurationSection) enchantObject;
      for (String key : cs.getKeys(false)) {
        int level = cs.getInt(key, 1);
        addEnchant(meta, key, level);
      }
      return;
    }

    if (enchantObject instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> map = (Map<String, Object>) enchantObject;
      for (Map.Entry<String, Object> e : map.entrySet()) {
        int level = 1;
        if (e.getValue() instanceof Number) level = ((Number) e.getValue()).intValue();
        addEnchant(meta, e.getKey(), level);
      }
      return;
    }

    if (enchantObject instanceof List) {
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>) enchantObject;
      for (Object o : list) {
        if (o == null) continue;
        String s = String.valueOf(o);
        String name = s;
        int level = 1;
        int idx = s.indexOf(':');
        if (idx >= 0) {
          name = s.substring(0, idx);
          try {
            level = Integer.parseInt(s.substring(idx + 1));
          } catch (NumberFormatException ignored) {}
        }
        addEnchant(meta, name, level);
      }
    }
  }

  private static void addEnchant(ItemMeta meta, String name, int level) {
    if (name == null) return;
    String keyRaw = name.trim();
    Enchantment enchantment = resolveEnchantment(keyRaw);
    if (enchantment == null) {
      Bukkit.getLogger().warning("Unknown enchantment: " + keyRaw);
      return;
    }
    if (level <= 0) level = 1;
    meta.addEnchant(enchantment, level, true);
  }

  private static Enchantment resolveEnchantment(String keyRaw) {
    // Try namespaced key form first
    Enchantment ench;
    NamespacedKey key = NamespacedKey.fromString(keyRaw.toLowerCase());
    if (key != null) {
      ench = Enchantment.getByKey(key);
      if (ench != null) return ench;
    }

    // Try minecraft namespace
    ench = Enchantment.getByKey(NamespacedKey.minecraft(keyRaw.toLowerCase()));
    if (ench != null) return ench;

    // Fallback to legacy byName
    return Enchantment.getByName(keyRaw.toUpperCase());
  }
}

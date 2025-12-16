package me.sentryozvn.mangoUtilities.Container;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.sentryozvn.mangoUtilities.MangoUtilities;
import me.sentryozvn.mangoUtilities.Util.CommandUtil;
import me.sentryozvn.mangoUtilities.Util.ItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class ContainerBuilder {
  private final String saveKey;
  private final YamlConfiguration containerConfig;
  private ChestGui gui;
  protected HashMap<Integer, ItemStack> saveStorage = new HashMap<>();
  private Set<Integer> storageSlots = new HashSet<>();
  private MangoUtilities plugin;

  ContainerBuilder(YamlConfiguration containerConfig, String saveKey) {
    this.containerConfig = containerConfig;
    this.saveKey = saveKey;
    this.plugin = MangoUtilities.getInstance();
    loadSavedItems();
  }

  protected void initContainer() {
    int rows = containerConfig.getInt("rows", 6);
    String title = containerConfig.getString("title", "Container");

    // destroyWhenClose: true/false <- Item in gui will be voided when close
    boolean destroyWhenClose = containerConfig.getBoolean("destroyWhenClose", false);
    // persist: true/false <- Item will stay in gui instead of return back to player, will be overridden by destroyWhenClose
    boolean persist = containerConfig.getBoolean("persist", false);

    this.gui = new ChestGui(rows, title);
    StaticPane panel = new StaticPane(0, 0, 9, rows);
    @Nullable ConfigurationSection slots = containerConfig.getConfigurationSection("contents");

    if (slots != null) {
      slots.getKeys(false).forEach(key -> {
        String configKey = "contents." + key;
        buildItemSlot(configKey, panel);
      });
    }

    gui.setOnTopClick(event -> {
      int slot = event.getRawSlot();

      SkillManager skillManager = MythicBukkit.inst().getSkillManager();
      Optional<Skill> skill = skillManager.getSkill("test");
      
    });


    if (!destroyWhenClose) {
      gui.setOnClose(event -> {
        for (int slotIndex : storageSlots) {
          final ItemStack storageItem = gui.getInventory().getItem(slotIndex);
          if (storageItem != null && !storageItem.getType().isAir()) {
            if (persist) {
              saveStorage.put(slotIndex, storageItem);
            } else {
              event.getPlayer().getInventory().addItem(storageItem.clone());
            }
          }
        }
        saveItems();
      });
    }
  }

  /**
   * slot: 5 / 1-2 / 1,2,3 / 1-5,7,8<br>
   * material: "STONE"
   * storage: true/false <- Can put item in this slot or not<br>
   * runSkill: "skillNameHere" <- Mythic Mobs skill name<br>
   * restrict: SWORD:CUTLASS <- Restrict which item can put in storage <TYPE[:ID]>
   */

  private void buildItemSlot(String configKey, StaticPane panel) {
    Inventory inventory = this.gui.getInventory();

    ConfigurationSection section = containerConfig.getConfigurationSection(configKey);
    if (section != null) {
      // slot: 5 / 1-2 / 1,2,3 / 1-5,7,8
      String slots = section.getString("slots");
      if (slots == null) {
        return;
      }

      String parseMaterial = section.getString("material", "STONE");
      Material material = Material.getMaterial(parseMaterial);
      if (material == null) material = Material.STONE;

      ItemStack itemStack = new ItemStack(material);
      ItemMeta meta = itemStack.getItemMeta();

      List<String> loreLines = section.getStringList("lore");
      List<Component> loreBuilder = new ArrayList<>();
      for (String lore : loreLines) {
        loreBuilder.add(Component.text(lore));
      }
      meta.lore(loreBuilder);

      int customModelData = section.getInt("customModelData");
      meta.setCustomModelData(customModelData);

      List<String> flags = section.getStringList("flags");
      ItemUtil.applyItemFlags(meta, flags);

      Object enchantObject = section.get("enchantments");
      ItemUtil.applyEnchantments(meta, enchantObject);

      itemStack.setItemMeta(meta);

      GuiItem guiItem = new GuiItem(itemStack);

      Set<Integer> parsedSlots = CommandUtil.parseSlots(slots, inventory);

      for (int slotIndex : parsedSlots) {
        panel.addItem(guiItem, Slot.fromIndex(slotIndex));
      }
    }
  }

  private void saveItems() {
    if (saveStorage.isEmpty()) {
      return;
    }

    File storageFile = new File(plugin.getDataFolder() + "/storage", saveKey + ".yml");
    YamlConfiguration storageConfig = new YamlConfiguration();

    saveStorage.forEach((slot, item) -> storageConfig.set("items." + slot, item));

    try {
      storageConfig.save(storageFile);
    } catch (IOException e) {
      plugin.getLogger().log(Level.SEVERE, "Could not save storage for container " + saveKey, e);
    }
  }

  private void loadSavedItems() {
    File storageFile = new File(plugin.getDataFolder() + "/storage", saveKey + ".yml");
    if (!storageFile.exists()) {
      return;
    }

    YamlConfiguration storageConfig = YamlConfiguration.loadConfiguration(storageFile);
    ConfigurationSection itemsSection = storageConfig.getConfigurationSection("items");

    if (itemsSection != null) {
      itemsSection.getKeys(false).forEach(slotKey -> {
        int slot = Integer.parseInt(slotKey);
        ItemStack item = itemsSection.getItemStack(slotKey);
        saveStorage.put(slot, item);
      });
    }
  }
}

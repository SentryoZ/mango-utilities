package me.sentryozvn.mangoUtilities;

import me.sentryozvn.mangoUtilities.Command.MangoUtilityCommand;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class MangoUtilities extends JavaPlugin {

  private static MangoUtilities instance;

  @Override
  public void onEnable() {
    instance = this;
    // register commands
    PluginCommand command = getCommand("mango-utility");
    if (command != null) {
      MangoUtilityCommand executor = new MangoUtilityCommand(this);
      command.setExecutor(executor);
      command.setTabCompleter(executor);
    }
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }

  public static MangoUtilities getInstance() {
    return instance;
  }
}

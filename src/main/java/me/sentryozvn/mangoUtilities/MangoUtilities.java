package me.sentryozvn.mangoUtilities;

import me.sentryozvn.mangoUtilities.Command.MangoUtilityCommand;
import me.sentryozvn.mangoUtilities.PlacholderAPI.MangoPlaceholder;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import static me.sentryozvn.mangoUtilities.Util.PluginUtil.isPluginEnabled;

public final class MangoUtilities extends JavaPlugin {

  private static MangoUtilities instance;

  @Override
  public void onEnable() {
    instance = this;
    // register commands
    new MangoUtilityCommand(this);

    if (isPluginEnabled("PlaceholderAPI")) { //
      new MangoPlaceholder().register(); //
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

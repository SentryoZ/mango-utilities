package me.sentryozvn.mangoUtilities.Util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PluginUtil {

  public static boolean isPluginEnabled(String name) {
    Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
    return plugin != null && plugin.isEnabled();
  }

}

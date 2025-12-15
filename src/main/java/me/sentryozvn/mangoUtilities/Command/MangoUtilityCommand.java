package me.sentryozvn.mangoUtilities.Command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import me.sentryozvn.mangoUtilities.MangoUtilities;

public class MangoUtilityCommand {
  private final MangoUtilities plugin;

  public MangoUtilityCommand(MangoUtilities plugin) {
    this.plugin = plugin;
    registerCommands();
  }

  private void registerCommands() {
    new CommandAPICommand("mango-utilities")
            .withPermission(CommandPermission.fromString("mango.utilities.use"))
            .withAliases("mgut,mut")
            .withSubcommand(new CompareCommand(plugin).command)
            .register();


  }
}

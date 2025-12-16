package me.sentryozvn.mangoUtilities.PlacholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.sentryozvn.mangoUtilities.MangoUtilities;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.party.AbstractParty;
import net.Indyuce.mmocore.party.provided.Party;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static java.util.logging.Level.SEVERE;

public class MangoPlaceholder extends PlaceholderExpansion {

  @NotNull
  @Override
  public String getIdentifier() {
    return "mg";
  }

  @NotNull
  @Override
  public String getAuthor() {
    return "SenZ";
  }

  @NotNull
  @Override
  public String getVersion() {
    return MangoUtilities.getInstance().getDescription().getVersion();
  }


  public String onRequest(OfflinePlayer player, @NonNull String identifier) {
    if (!PlayerData.has(player.getUniqueId()))
      return null;
    final PlayerData playerData = PlayerData.get(player);

    if (identifier.startsWith("party_member_excluded_self_")) {
      final int n = Integer.parseInt(identifier.substring(27)) - 1;
      final @Nullable AbstractParty party = playerData.getParty();
      if (party == null) return "";
      if (n >= party.countMembers()) return "";
      List<PlayerData> partyMembers = party.getOnlineMembers();
      partyMembers.remove(playerData);
      final @Nullable PlayerData member = partyMembers.get(n);
      if (member == null) return "";
      return member.getPlayer().getName();
    } else if (identifier.startsWith("is_party_leader")) {
      final @Nullable AbstractParty party = playerData.getParty();
      if (party instanceof Party) {
        PlayerData owner = ((Party) party).getOwner();
        return owner == playerData ? "true" : "false";
      } else {
        MangoUtilities.getInstance().getLogger().log(SEVERE, "Didn't support other party plugin yet");
        return "false";
      }
    }

    return "";
  }

}

package com.dnyferguson.mineablespawners.commands;

import com.dnyferguson.mineablespawners.MineableSpawners;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TabCompleter for the /mineablespawners (ms) command.
 * Provides suggestions for subcommands, online players, entity types, and common amounts.
 */
public class CommandsTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        MineableSpawners plugin = MineableSpawners.getPlugin();
        // If no arguments have been typed yet, suggest top-level subcommands
        if (args.length == 0) {
            addMainSubcommands(plugin, sender, suggestions);
            Collections.sort(suggestions);
            return suggestions;
        }

        if (args.length == 1) {
            // Suggest subcommands based on permissions and configuration
            addMainSubcommands(plugin, sender, suggestions);
        } else {
            String sub = args[0].toLowerCase();

            if (sub.equals("give")) {
                if (args.length == 2) {
                    // suggest online players
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        suggestions.add(p.getName());
                    }
                } else if (args.length == 3) {
                    // suggest entity types
                    for (EntityType t : EntityType.values()) {
                        suggestions.add(t.name().toLowerCase());
                    }
                } else if (args.length == 4) {
                    // suggest common amounts
                    suggestions.add("1");
                    suggestions.add("2");
                    suggestions.add("4");
                    suggestions.add("8");
                    suggestions.add("16");
                    suggestions.add("32");
                    suggestions.add("64");
                }

            } else if (sub.equals("set")) {
                if (args.length == 2) {
                    for (EntityType t : EntityType.values()) {
                        suggestions.add(t.name().toLowerCase());
                    }
                }

            }
        }

        // Filter suggestions by what the user is currently typing (the last argument)
        String last = args[args.length - 1].toLowerCase();
        if (!last.isEmpty()) {
            suggestions.removeIf(s -> !s.toLowerCase().startsWith(last));
        }

        Collections.sort(suggestions);
        return suggestions;
    }

    private void addMainSubcommands(MineableSpawners plugin, CommandSender sender, List<String> suggestions) {
        if (!plugin.getConfigurationHandler().getBoolean("give", "require-permission") || sender.hasPermission("mineablespawners.give")) {
            suggestions.add("give");
        }
        if (!plugin.getConfigurationHandler().getBoolean("set", "require-permission") || sender.hasPermission("mineablespawners.set")) {
            suggestions.add("set");
        }
        if (!plugin.getConfigurationHandler().getBoolean("types", "require-permission") || sender.hasPermission("mineablespawners.types")) {
            suggestions.add("types");
        }
        if (sender.hasPermission("mineablespawners.reload")) {
            suggestions.add("reload");
        }
    }
}


package com.dnyferguson.mineablespawners.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.dnyferguson.mineablespawners.MineableSpawners;
import com.dnyferguson.mineablespawners.utils.Chat;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SpawnerExplodeListener implements Listener {
    private final MineableSpawners plugin;

    public SpawnerExplodeListener(MineableSpawners plugin) {
        this.plugin = plugin;
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSpawnerExplode(EntityExplodeEvent e) {
        if (!plugin.getConfigurationHandler().getBoolean("explode", "drop")) {
            return;
        }

        if (e.getLocation().getWorld() != null && plugin.getConfigurationHandler().getList("explode", "blacklisted-worlds").contains(e.getLocation().getWorld().getName())) {
            return;
        }

        if (e.getEntityType() == EntityType.WIND_CHARGE || e.getEntityType() == EntityType.BREEZE_WIND_CHARGE) return;

        for (Block block : e.blockList()) {
            if (!block.getType().equals(XMaterial.SPAWNER.parseMaterial())) {
                continue;
            }

            double dropChance = plugin.getConfigurationHandler().getDouble("explode", "chance")/100;

            if (dropChance != 1) {
                double random = Math.random();
                if (random >= dropChance) {
                    return;
                }
            }

            CreatureSpawner spawner = (CreatureSpawner) block.getState();

            ItemStack item = new ItemStack(XMaterial.SPAWNER.parseMaterial());
            ItemMeta meta = item.getItemMeta();
            String mobFormatted = Chat.uppercaseStartingLetters(spawner.getSpawnedType().toString());

            if (meta != null) {
                meta.setDisplayName(plugin.getConfigurationHandler().getMessage("global", "name").replace("%mob%", mobFormatted));
                List<String> newLore = new ArrayList<>();
                if (plugin.getConfigurationHandler().getList("global", "lore") != null && plugin.getConfigurationHandler().getBoolean("global", "lore-enabled")) {
                    for (String line : plugin.getConfigurationHandler().getList("global", "lore")) {
                        newLore.add(Chat.format(line).replace("%mob%", mobFormatted));
                    }
                    meta.setLore(newLore);
                }
                item.setItemMeta(meta);
            }

            /*int spawnerStackCount = 1;

            if (plugin.getServer().getPluginManager().getPlugin("InsanityFactions") != null) {
                String world = spawner.getWorld().getName();
                int x = spawner.getX();
                int y = spawner.getY();
                int z = spawner.getZ();

                String translation = PlaceholderAPI.setPlaceholders(null, "%insanityfactions_spawnerstackcount_" + world + "_" + x + "_" + y + "_" + z + "%");
                try {
                    spawnerStackCount = Integer.parseInt(translation);
                } catch (NumberFormatException ignored) {}
            }

            if (spawnerStackCount == 0) spawnerStackCount = 1;
            item.setAmount(spawnerStackCount);*/

            NBTItem nbti = new NBTItem(item);
            nbti.setString("ms_mob", spawner.getSpawnedType().name());

            item = nbti.getItem();

            if (block.getLocation().getWorld() != null) {
                block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
            }
        }
    }
}

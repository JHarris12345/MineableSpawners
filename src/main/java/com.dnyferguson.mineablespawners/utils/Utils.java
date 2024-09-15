package com.dnyferguson.mineablespawners.utils;

import com.dnyferguson.mineablespawners.MineableSpawners;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static MineableSpawners plugin = MineableSpawners.getPlugin();

    // Returns true if no issues and false if some items needed to be dropped due to a full inventory
    public static boolean givePlayerItem(Player player, ItemStack item, Location dropIfFullLocation, boolean ignoreMaxStackSizes) {
        HashMap<Integer, ItemStack> leftOver = new HashMap<>();
        if (ignoreMaxStackSizes) {
            leftOver = player.getInventory().addItem(item);

        } else {
            int amount = item.getAmount();
            item.setAmount(1);

            boolean hadToDrop = false;
            for (int i=0; i<amount; i++) {
                if (!player.getInventory().addItem(item).isEmpty()) {
                    player.getWorld().dropItem(dropIfFullLocation, item);
                    hadToDrop = true;
                }
            }
            return !hadToDrop;
        }

        if (!leftOver.isEmpty()) {
            for (ItemStack itemStack : leftOver.values()) {
                if (itemStack.getAmount() <= itemStack.getMaxStackSize()) {
                    player.getWorld().dropItem(dropIfFullLocation, item);

                } else {
                    int stacks = (int) Math.floor((double) itemStack.getAmount() / itemStack.getMaxStackSize());
                    int singles = itemStack.getAmount() - (stacks * itemStack.getMaxStackSize());

                    item.setAmount(itemStack.getMaxStackSize());
                    for (int i=0; i<stacks; i++) {
                        player.getWorld().dropItem(dropIfFullLocation, item);
                    }

                    item.setAmount(singles);
                    if (singles == 0) continue;

                    player.getWorld().dropItem(dropIfFullLocation, item);
                }
            }

            return false;

        } else return true;
    }

    public static void setPersistentData(ItemStack item, String key, PersistentDataType persistentDataType, Object value) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), persistentDataType, value);
        item.setItemMeta(meta);
    }

    public static Object getPersistentData(ItemMeta meta, String key, PersistentDataType persistentDataType, Plugin plugin) {
        if (meta == null || plugin == null) return null;
        Object object = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, key), persistentDataType);

        return object;
    }

    public static String colour(String string) {
        Pattern pattern = Pattern.compile("&?#[A-Fa-f0-9]{6}");
        Matcher matcher = pattern.matcher(string);
        String output = ChatColor.translateAlternateColorCodes('&', string);

        while (matcher.find()) {
            String color = string.substring(matcher.start(), matcher.end());
            output = output.replace(color, "" + net.md_5.bungee.api.ChatColor.of(color.replace("&", "")));
        }

        return output;
    }
}

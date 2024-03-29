package org.splendid.antiblock;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.ChatColor;

import java.util.List;

public final class Antiblock extends JavaPlugin {

    private final FileConfiguration config = getConfig();

    public void onEnable() {
        getLogger().info("Antiblock enabled!" + ChatColor.WHITE + "Blocks that players are prohibited from breaking: " + ChatColor.YELLOW + getProtectedBlocks().toString());
        config.options().copyDefaults(true);
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new BlockListener(this, config), this);
    }

    public void onDisable() {
        getLogger().info("Antiblock disabled!" + getProtectedBlocks().toString());
    }

    public List<String> getProtectedBlocks() {
        return config.getStringList("protectedBlocks");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("antiblockinfo")) {
            FileConfiguration config = getConfig();
            List<String> blockedBlocks = config.getStringList("blockedBlocks");
            if (blockedBlocks.isEmpty()) {
                sender.sendMessage(config.getString("messages.noBlockedBlocks"));
            } else {
                sender.sendMessage(config.getString("messages.blockedBlocksHeader"));
                for (String blockedBlock : blockedBlocks) {
                    sender.sendMessage(blockedBlock);
                }
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("antiblockhelp")) {
            sender.sendMessage("Antiblock plugin commands:");
            sender.sendMessage("/antiblockinfo - Shows blocked blocks");
            sender.sendMessage("/antiblockhelp - Shows help message");
            sender.sendMessage("/antiblockreload - Reloads config file");
            sender.sendMessage("/antiblockbypass - Toggles bypass");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("antiblockbypass")) {
            if (sender.isOp()) {
                sender.sendMessage("Bypass operation is successful.");
            } else {
                sender.sendMessage("You must be op to use this command.");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("antiblockreload")) {
            if (sender.hasPermission("antiblock.reload")) {
                reloadConfig();
                sender.sendMessage("Antiblock config file has been renewed!");
            } else {
                sender.sendMessage("You do not have permission to use this command!");
            }
            return true;
        }
        return false;
    }
    public class BlockListener implements Listener {

        private final JavaPlugin plugin;
        private final FileConfiguration config;

        public BlockListener(JavaPlugin plugin, FileConfiguration config) {
            this.plugin = plugin;
            this.config = config;
        }

        @EventHandler
        public void onBlockPlace(BlockPlaceEvent event) {
            Player player = event.getPlayer();
            if (config.getStringList("protectedBlocks").contains(event.getBlockPlaced().getType().toString())) {
                event.setCancelled(true);
                String rawMessage = config.getString("blockPlaceError");
                String coloredMessage = ChatColor.translateAlternateColorCodes('&', rawMessage);
                player.sendMessage(coloredMessage);
            }
        }

        @EventHandler
        public void onBlockBreak(BlockBreakEvent event) {
            Player player = event.getPlayer();
            if (config.getStringList("protectedBlocks").contains(event.getBlock().getType().toString())) {
                event.setCancelled(true);
                String rawMessage = config.getString("blockBreakError");
                String coloredMessage = ChatColor.translateAlternateColorCodes('&', rawMessage);
                player.sendMessage(coloredMessage);
            }
        }
    }
}
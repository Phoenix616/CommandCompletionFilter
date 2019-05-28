package de.themoep.commandcompletionfilter;

/*
 * CommandCompletionFilter
 * Copyright (c) 2019 Max Lee aka Phoenix616 (mail@moep.tv)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class CommandCompletionFilter extends JavaPlugin implements Listener {

    private Map<String, String> commandPermissions = new HashMap<>();

    private Map<String, String> commandGroups = new HashMap<>();
    private Map<String, String> pluginGroups = new HashMap<>();
    private Map<String, String> permissionGroups = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadConfig() {
        saveDefaultConfig();
        reloadConfig();

        ConfigurationSection commands = getConfig().getConfigurationSection("commands");
        for (String command : commands.getKeys(false)) {
            commandPermissions.put(command.toLowerCase(), commands.getString(command));
        }

        ConfigurationSection groups = getConfig().getConfigurationSection("groups");
        for (String groupName : groups.getKeys(false)) {
            ConfigurationSection group = groups.getConfigurationSection(groupName);
            if (group != null) {
                permissionGroups.put(groupName.toLowerCase(), group.getString("permission"));
                if (group.isList("commands")) {
                    for (String command : group.getStringList("commands")) {
                        commandGroups.put(command.toLowerCase(), groupName.toLowerCase());
                    }
                }
                if (group.isList("plugins")) {
                    for (String plugin : group.getStringList("plugins")) {
                        pluginGroups.put(plugin.toLowerCase(), groupName.toLowerCase());
                    }
                }
            } else {
                getLogger().warning(groupName + " is not a valid config section!");
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            if ("reload".equalsIgnoreCase(args[0]) && sender.hasPermission("commandcompletionfilter.command.reload")) {
                loadConfig();
                sender.sendMessage(ChatColor.YELLOW + "Config reloaded!");
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent event) {
        for (Iterator<String> it = event.getCommands().iterator(); it.hasNext();) {
            String permission = getPermission(it.next());
            if (permission != null && !event.getPlayer().hasPermission(permission)) {
                it.remove();
            }
        }
    }

    private String getPermission(String commandName) {
        PluginCommand command = getServer().getPluginCommand(commandName);
        String pluginName = null;
        if (command == null && commandName.contains(":")) {
            String[] parts = commandName.split(":", 2);
            if (parts.length > 1) {
                pluginName = parts[0].toLowerCase();
                commandName = parts[1].toLowerCase();
                command = getServer().getPluginCommand(commandName);
                if (command != null && !command.getPlugin().getName().equalsIgnoreCase(pluginName)) {
                    command = null;
                }
            }
        } else if (command != null) {
            pluginName = command.getPlugin().getName().toLowerCase();
            commandName = command.getName().toLowerCase();
        } else {
            commandName = commandName.toLowerCase();
        }
        commandName = commandName.replace("/", "");

        String permission = commandPermissions.get(commandName);
        if (permission == null) {
            String groupName = commandGroups.get(commandName);
            if (groupName == null && pluginName != null) {
                groupName = pluginGroups.get(pluginName);
            }
            if (groupName != null) {
                permission = permissionGroups.get(groupName);
            }
        }

        if (permission != null) {
            return permission
                    .replace("%plugin%", pluginName != null ? pluginName : "unknownplugin")
                    .replace("%command%", commandName);
        }

        return command != null ? command.getPermission() : null;
    }
}

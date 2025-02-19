package me.ancliz.hardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.ancliz.hardcore.Hardcore;
import me.ancliz.hardcore.MMFormatter;
import me.ancliz.hardcore.actions.WorldAction;

@SuppressWarnings("deprecation")
public class CommandHardcore implements CommandExecutor {
    private WorldAction worldAction;

    public CommandHardcore() {
        worldAction = new WorldAction();
    }

    private boolean handleNew(Player player, String[] args) {
        Environment environment = Environment.NORMAL;
        if(args.length != 3) {
            return false;
        }

        switch(args[1].toLowerCase()) {
            case "nether": environment = Environment.NETHER;  break;
            case "end":    environment = Environment.THE_END; break;
        }
        
        worldAction.createWorld(args[2], environment);
        player.sendMessage(MMFormatter.pluginMessage(args[2] + " created."));
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(args.length < 1) {
            help(sender, args);
            return false;
        }

        String subCommand = args[0];
        Command cmd = getCommand(subCommand);
        boolean valid = true;

        if(cmd == null) {
            return false;
        }

        if(cmd == Command.VERSION) {
            sender.sendMessage(MMFormatter.pluginMessage(Hardcore.getInstance().getDescription().getVersion()));
        } else if(cmd == Command.HELP) {
            help(sender, args);
        } else if(cmd == Command.WORLD) {
            Player player = (Player) sender;
            player.sendMessage(MMFormatter.pluginMessage("You are currently in: " + player.getWorld().getName()));
        } else if(cmd == Command.NEW) {
            valid = handleNew((Player) sender, args);
        } else if(cmd == Command.GOTO) {
            worldAction.teleportToWorld(args[1], (Player) sender);
        } else if(cmd == Command.UNLOAD) {
            try {
                worldAction.unloadWorld(args[1]);
                sender.sendMessage(MMFormatter.pluginMessage(args[1] + " unloaded."));
            } catch(NullPointerException e) {
                sender.sendMessage(MMFormatter.pluginMessage("World does not exist."));
            }
        } else if(cmd == Command.DELETE) {
            if(worldAction.deleteWorld(args[1])) {
                sender.sendMessage(MMFormatter.pluginMessage("World deleted."));
            } else {
                sender.sendMessage(MMFormatter.pluginMessage("World is still loaded, unable to delete."));
            }
        } else if(cmd == Command.LIST) {
            sender.sendMessage(MMFormatter.pluginMessage("Worlds: " + Bukkit.getWorlds().toString()));
        } else {
            valid = false;
        }

        return valid;

    }
    
    private void help(CommandSender sender, String[] args) {
        int maxPageLines = 9;
        int commandsLength = Command.values().length;
        int totalPages = commandsLength / maxPageLines + (commandsLength % maxPageLines == 0 ? 0 : 1);

        try {
            int page = Integer.parseInt(args[1]);
            if(page > totalPages) {
                throw new NumberFormatException();
            }
            sender.sendMessage(MMFormatter.help(page, totalPages, maxPageLines));
        } catch(NumberFormatException e) {
            sender.sendMessage(MMFormatter.format("Unknown Chapter", ChatColor.DARK_RED));
        } catch(ArrayIndexOutOfBoundsException e) {
            sender.sendMessage(MMFormatter.help(1, totalPages, maxPageLines));
        }
    }

    private Command getCommand(String commandStr) {
        Command command = null;
        for(Command cmd : Command.values()) {
            for(String alias : cmd.aliases()) {
                if(commandStr.equalsIgnoreCase(alias)) {
                    command = cmd;
                    break;
                }
            }
            if(command != null) {
                break;
            }
        }

        return command;
    }
    
}
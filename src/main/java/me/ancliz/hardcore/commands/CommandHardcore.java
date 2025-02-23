package me.ancliz.hardcore.commands;

import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.ancliz.hardcore.Hardcore;
import me.ancliz.hardcore.MMFormatter;
import me.ancliz.hardcore.actions.WorldAction;
import me.ancliz.hardcore.util.LoggerWrapper;

@SuppressWarnings("deprecation")
public class CommandHardcore implements CommandExecutor {
    private LoggerWrapper logger = new LoggerWrapper(LogManager.getLogger());
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
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] arguments) {
        if(arguments.length < 1) {
            help(sender, arguments);
            return false;
        }
        
        Player player = null;

        if(sender instanceof Player) {
            player = (Player) sender;
        }

        String subCommand = arguments[0];
        String[] args = arguments.length > 1 ? Arrays.copyOfRange(arguments, 2, arguments.length) : new String[0];
        Command cmd = getCommand(subCommand);
        boolean valid = true;

        if(cmd == null) {
            return false;
        }

        logger.trace("args: {}", Arrays.toString(args));

        if(cmd == Command.VERSION) {
            player.sendMessage(MMFormatter.pluginMessage(Hardcore.getInstance().getDescription().getVersion()));
        } else if(cmd == Command.RELOAD) {
            Hardcore.getInstance().reload();
        }
        else if(cmd == Command.HELP) {
            help(player, arguments);
        } else if(cmd == Command.WORLD) {
            player.sendMessage(MMFormatter.pluginMessage("You are currently in: " + player.getWorld().getName()));
        } else if(cmd == Command.NEW) {
            valid = handleNew(player, arguments);
        } else if(cmd == Command.GOTO) {
            if(args.length == 0) {
                worldAction.teleportToWorld(arguments[1], player);
            } else {
                double[] coords = new double[3];
                try {
                    coords[0] = Double.parseDouble(args[0]);
                    coords[1] = args.length == 2 ? player.getLocation().getY() : Double.parseDouble(args[1]);
                    coords[2] = Double.parseDouble(args[args.length == 2 ? 1 : 2]);
                } catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    player.sendMessage(MMFormatter.pluginMessage("Invalid coordinates."));
                    valid = false;
                }
                worldAction.teleportToWorld(arguments[1], player, coords);
            }
        } else if(cmd == Command.UNLOAD) {
            try {
                worldAction.unloadWorld(arguments[1]);
                player.sendMessage(MMFormatter.pluginMessage(arguments[1] + " unloaded."));
            } catch(NullPointerException e) {
                player.sendMessage(MMFormatter.pluginMessage("World does not exist."));
            }
        } else if(cmd == Command.DELETE) {
            if(worldAction.deleteWorld(arguments[1])) {
                player.sendMessage(MMFormatter.pluginMessage("World deleted."));
            } else {
                player.sendMessage(MMFormatter.pluginMessage("World is still loaded, unable to delete."));
            }
        } else if(cmd == Command.LIST) {
            player.sendMessage(MMFormatter.pluginMessage("Worlds: " + Bukkit.getWorlds().toString()));
        } else {
            valid = false;
        }

        return valid;

    }
    
    private void help(CommandSender player, String[] args) {
        int maxPageLines = 9;
        int commandsLength = Command.values().length;
        int totalPages = commandsLength / maxPageLines + (commandsLength % maxPageLines == 0 ? 0 : 1);

        try {
            int page = Integer.parseInt(args[1]);
            if(page > totalPages) {
                throw new NumberFormatException();
            }
            player.sendMessage(MMFormatter.help(page, totalPages, maxPageLines));
        } catch(NumberFormatException e) {
            player.sendMessage(MMFormatter.format("Unknown Chapter", ChatColor.DARK_RED));
        } catch(ArrayIndexOutOfBoundsException e) {
            player.sendMessage(MMFormatter.help(1, totalPages, maxPageLines));
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
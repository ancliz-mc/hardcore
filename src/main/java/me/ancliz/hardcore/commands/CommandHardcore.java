package me.ancliz.hardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.ancliz.hardcore.Hardcore;
import me.ancliz.hardcore.actions.WorldAction;
import me.ancliz.minecraft.MMFormatter;
import me.ancliz.minecraft.commands.CommandManager;
import me.ancliz.minecraft.commands.DefaultCommandHandler;

@SuppressWarnings("deprecation")
public class CommandHardcore extends DefaultCommandHandler {
    private WorldAction worldAction;

    public CommandHardcore(CommandManager commandManager) {
        super(commandManager);
        formatter = new MMFormatter(Hardcore.getInstance().getName(), commandManager);
        worldAction = new WorldAction();
        commandManager.registerHandler("hardcore.version", this::version);
        commandManager.registerHandler("hardcore.help", this::help);
        commandManager.registerHandler("hardcore.world", this::handleWorld);
        commandManager.registerHandler("hardcore.new", this::handleNew);
        commandManager.registerHandler("hardcore.goto", this::handleGoto);
        commandManager.registerHandler("hardcore.unload", this::handleUnload);
        commandManager.registerHandler("hardcore.delete", this::handleDelete);
        commandManager.registerHandler("hardcore.list", this::handleList);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(args.length < 1) {
            help(sender, args);
            return false;
        }

        return super.onCommand(sender, command, label, args);
    }

    private boolean version(CommandSender sender, String[] args) {
        sender.sendMessage(formatter.pluginMessage(Hardcore.getInstance().getDescription().getVersion()));
        return true;
    }

    private boolean handleWorld(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player) || args.length > 0) {
            return false;
        }
        player.sendMessage(formatter.pluginMessage("You are currently in: " + player.getWorld().getName()));
        return true;
    }

    private boolean handleNew(CommandSender sender, String[] args) {
        Environment environment = Environment.NORMAL;
        if(args.length < 2 || args.length > 3) {
            return false;
        }

        switch(args[0].toLowerCase()) {
            case "nether": environment = Environment.NETHER;  break;
            case "end":    environment = Environment.THE_END; break;
        }
        
        worldAction.createWorld(args[1], environment);
        sender.sendMessage(formatter.pluginMessage(args[1] + " created."));
        return true;
    }

    private boolean handleGoto(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            return true;
        }

        if(args.length == 1) {
            return worldAction.teleportToWorld(args[0], player);
        } else {
            double[] coords = new double[3];
            try {
                coords[0] = Double.parseDouble(args[0]);
                coords[1] = args.length == 2 ? player.getLocation().getY() : Double.parseDouble(args[1]);
                coords[2] = Double.parseDouble(args[args.length == 2 ? 1 : 2]);
            } catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
                player.sendMessage(formatter.pluginMessage("Invalid coordinates."));
                return false;
            }
            worldAction.teleportToWorld(args[0], player, coords);
            return true;
         }

    }

    private boolean handleUnload(CommandSender sender, String[] args) {
        String message;
        try {
            worldAction.unloadWorld(args[0]);
            message = args[0] + " unloaded.";
        } catch(NullPointerException e) {
            message = "World does not exist.";
        }

        if(sender instanceof Player player) {
            player.sendMessage(formatter.pluginMessage(message));
        } else {
            sender.sendMessage(message);
        }

        return true;
    }

    private boolean handleDelete(CommandSender sender, String[] args) {
        boolean deleted = worldAction.deleteWorld(args[0]);
        String message = deleted ? "World deleted." : "World is still loaded, unable to delete.";

        if(sender instanceof Player player) {
            player.sendMessage(formatter.pluginMessage(message));
        } else {
            sender.sendMessage(message);
        }

        return true;
    }

    private boolean handleList(CommandSender sender, String[] args) {
        if(sender instanceof Player player) {
            player.sendMessage(formatter.pluginMessage("Worlds: " + Bukkit.getWorlds()));
        } else {
            sender.sendMessage("Worlds: " + Bukkit.getWorlds());
        }

        return true;
    }

    private boolean help(CommandSender sender, String[] args) {
        int maxPageLines = 9;
        int commandsLength = commandManager.getTopLevel().size() + commandManager.getLevelOne().size();
        int totalPages = commandsLength / maxPageLines + (commandsLength % maxPageLines == 0 ? 0 : 1);

        try {
            int page = Integer.parseInt(args[0]);
            if(page > totalPages) {
                throw new NumberFormatException();
            }
            sender.sendMessage(formatter.help(page, totalPages, maxPageLines));
        } catch(NumberFormatException e) {
            sender.sendMessage(formatter.format("Unknown Chapter", ChatColor.DARK_RED));
        } catch(ArrayIndexOutOfBoundsException e) {
            sender.sendMessage(formatter.help(1, totalPages, maxPageLines));
        }

        return true;
    }

}
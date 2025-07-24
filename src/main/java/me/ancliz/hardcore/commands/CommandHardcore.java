package me.ancliz.hardcore.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.ancliz.hardcore.Hardcore;
import me.ancliz.hardcore.actions.WorldAction;
import me.ancliz.minecraft.commands.CommandManager;
import me.ancliz.minecraft.commands.DefaultCommandExecutor;

public class CommandHardcore extends DefaultCommandExecutor {
    private WorldAction worldAction;

    public CommandHardcore(CommandManager commandManager) {
        super(commandManager);
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
        messageSender.sendMessage(sender, Hardcore.getInstance().getDescription().getVersion(), formatter::pluginMessage);
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
        messageSender.sendMessage(sender, args[1] + " created.", formatter::pluginMessage);
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

        messageSender.sendMessage(sender, message, formatter::pluginMessage);

        return true;
    }

    private boolean handleDelete(CommandSender sender, String[] args) {
        boolean deleted = worldAction.deleteWorld(args[0]);
        String message = deleted ? "World deleted." : "World is still loaded, unable to delete.";
        messageSender.sendMessage(sender, message, formatter::pluginMessage);
        return true;
    }

    private boolean handleList(CommandSender sender, String[] args) {
        List<String> worldNames = new ArrayList<>();
        List<ChatColor> worldColours = new ArrayList<>();
        worldColours.add(ChatColor.GOLD);
        worldColours.add(ChatColor.WHITE);

        for(World world : Bukkit.getWorlds()) {
            worldNames.add(formatter.D + world.getName() + "::" + world.getEnvironment());

            ChatColor color = switch (world.getEnvironment()) {
                case NORMAL -> ChatColor.DARK_GREEN;
                case NETHER -> ChatColor.RED;
                case THE_END -> ChatColor.DARK_PURPLE;
                default -> ChatColor.GRAY;
            };
            worldColours.add(color);
        }

        String[] worlds = worldNames.toArray(String[]::new);
        ChatColor[] colours = worldColours.toArray(ChatColor[]::new);
        messageSender.sendMessage(sender, formatter.D + "     Worlds loaded\n" + formatter.D +"==================\n" + String.join("\n", worlds), colours);
        return true;
    }

    private boolean help(CommandSender sender, String[] args) {
        if(sender instanceof Player player) {
            int maxPageLines = 9;
            int commandsLength = commandManager.getTopLevel().size() + commandManager.getLevelOne().size();
            int totalPages = commandsLength / maxPageLines + (commandsLength % maxPageLines == 0 ? 0 : 1);

            try {
                int page = Integer.parseInt(args[0]);
                if(page > totalPages) {
                    throw new NumberFormatException();
                }
                player.sendMessage(formatter.help(page, totalPages, maxPageLines));
            } catch(NumberFormatException e) {
                player.sendMessage(formatter.format("Unknown Chapter", ChatColor.DARK_RED));
            } catch(ArrayIndexOutOfBoundsException e) {
                player.sendMessage(formatter.help(1, totalPages, maxPageLines));
            }
        } else {
            sender.sendMessage("help is currently not formatted for the console.");
        }
       
        return true;
    }

}
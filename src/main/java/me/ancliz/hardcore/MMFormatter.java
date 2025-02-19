package me.ancliz.hardcore;

import org.apache.logging.log4j.LogManager;
import org.bukkit.ChatColor;

import me.ancliz.hardcore.commands.Command;
import me.ancliz.hardcore.exceptions.MinecraftMessageFormatterException;
import me.ancliz.hardcore.util.LoggerWrapper;


/**
 * Minecraft message formatting
 */
@SuppressWarnings("deprecation")
public class MMFormatter {
    private static final LoggerWrapper logger = new LoggerWrapper(LogManager.getLogger());
    private static final String plugin = Hardcore.getInstance().getName();


    /** Delimiter for ChatColor substitution */
    public static final char D = '␚';

    public static String format(String message, ChatColor ... colours) {
        int count = message.length() - message.replace(String.valueOf(D), "").length();

        try {
            if(count == 0) {
                message = D + message;
            } else if(count < colours.length) {
                throw new MinecraftMessageFormatterException("Number of colours is greater than the number of colour characters.");
            } else if (count > colours.length) {
                throw new MinecraftMessageFormatterException("Number of colours is less than the number of colour characters.");
            }
        } catch(MinecraftMessageFormatterException e) {
            logger.warn(e.getMessage() + "\n" + logger.stackTrace(e, 5) + "\n" + e.getCause());
        }

        for(ChatColor colour : colours) {
            message = message.replaceFirst(String.valueOf(D), colour.toString());
        }

        return message;
    }

    public static String pluginMessage(String message) {
        return ChatColor.RED + plugin + ": " + ChatColor.WHITE + message;
    }
    public static String error(String message) {
        return ChatColor.RED + "Error: " + ChatColor.DARK_RED + message;
    }

    public static String warn(String message) {
        return ChatColor.YELLOW + "Warning: " + ChatColor.RED + message;
    }

    public static String broadcast(String message) {
        return format(D+"["+D+"Broadcast"+D+"] " +D+ message, ChatColor.GOLD, ChatColor.DARK_RED, ChatColor.GOLD, ChatColor.GREEN);
    }

    public static String help(int page, int totalPages, int maxPageLines) {
        StringBuilder builder = new StringBuilder();

        String line1 = format(D+"---- "+D+"Help: "+ capitalise(plugin) + " "+D+"-- "+D+"Page " +D+ page +D+ "/" +D+ totalPages +D+ "----\n",
                                ChatColor.YELLOW, ChatColor.GOLD, ChatColor.YELLOW,
                                ChatColor.GOLD, ChatColor.RED, ChatColor.GOLD, ChatColor.RED, ChatColor.YELLOW);

        builder.append(line1);
        buildPage(builder, Command.values(), page, maxPageLines);

        if(page != totalPages) {
            builder.append(
                format(D+"Type "+D+"/" + plugin + " help " + (page+1) +D+ " to read the next page.",
                        ChatColor.GOLD, ChatColor.RED, ChatColor.GOLD)
            );
        }

        return builder.toString();
    }

    private static void buildPage(StringBuilder builder, Command[] commands, int page, int maxPageLines) {
        int currentLine = 0;
        for(int i = page * maxPageLines - maxPageLines; currentLine < maxPageLines; ++i) {
            try {
                Command cmd = commands[i];
                String usage = cmd.usage().equals("") ? cmd.name().toLowerCase() : cmd.usage();
                builder.append(
                    format(D+"/"+ plugin + usage +D+ ": " + cmd.description() + "\n",
                            ChatColor.GOLD, ChatColor.WHITE)
                );
                ++currentLine;
            } catch(IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    private static String capitalise(String w) {
        return w.substring(0, 1).toUpperCase() + w.substring(1);
    }

    public static String green(String message) {
        return format(message, ChatColor.GREEN);
    }

    public static String success(String message) {
        return green(message);
    }

    public static String red(String message) {
        return format(message, ChatColor.RED);
    }
    
}
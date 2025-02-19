package me.ancliz.hardcore.commands.completers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TabCompleterHardcore implements TabCompleter {
    private final List<String> list = populate();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 1) {
            return new ArrayList<>();
        }

        return list.stream().filter(cmd -> cmd.toLowerCase().startsWith(args[0].toLowerCase())).toList();
    }

    private List<String> populate() {
        List<String> list = new ArrayList<>();
        for(me.ancliz.hardcore.commands.Command cmd : Arrays.stream(me.ancliz.hardcore.commands.Command.values()).toList()) {
            list.add(cmd.toString().toLowerCase());
        }
        return list;
    }

}
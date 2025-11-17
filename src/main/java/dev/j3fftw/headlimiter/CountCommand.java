package dev.j3fftw.headlimiter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

public class CountCommand implements CommandExecutor {

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        if (args.length == 1 && sender instanceof Player player) {

            Utils.count(player.getChunk(), result -> {
                StringBuilder message = new StringBuilder();

                if (Utils.canBypass(player)) {
                    message.append(result.getTotal() >= Utils.getMaxHeads(player) ? ChatColor.RED : ChatColor.GREEN)
                        .append("You can bypass the limits")
                        .append('\n');
                }

                message.append(ChatColor.GOLD)
                    .append("Current count: ")
                    .append(result.getTotal())
                    .append("/")
                    .append(Utils.getMaxHeads(player))
                    .append('\n');

                for (Map.Entry<String, Integer> entry : result.getCounts().entrySet()) {
                    if (entry.getValue() > 0) {
                        message.append("  ")
                            .append(ChatColor.GRAY)
                            .append(entry.getKey())
                            .append(": ")
                            .append(ChatColor.YELLOW)
                            .append(entry.getValue())
                            .append('\n');
                    }
                }
                player.sendMessage(message.toString());
            });
        } else {
            sender.sendMessage(ChatColor.GOLD + "/hl count"
                + ChatColor.GRAY + " - Counts how many heads are in this chunk"
            );
        }

        return true;
    }

}

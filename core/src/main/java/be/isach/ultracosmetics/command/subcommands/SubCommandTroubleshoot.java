package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.util.Problem;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class SubCommandTroubleshoot extends SubCommand {

    public SubCommandTroubleshoot(UltraCosmetics ultraCosmetics) {
        super("troubleshoot", "Identifies issues with UltraCosmetics", "", ultraCosmetics);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        Set<Problem> problems = ultraCosmetics.getProblems();
        if (problems.size() == 0) {
            sender.sendMessage(ChatColor.GREEN + "UltraCosmetics is not currently aware of any problems :)");
        } else {
            sender.sendMessage(ChatColor.RED + "UltraCosmetics currently has " + problems.size() + " minor problems.");
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + "(Click on a problem to see its wiki page)");
            }
            Audience audience = MessageManager.getAudiences().sender(sender);
            problems.forEach(p -> audience.sendMessage(p.getSummary().color(NamedTextColor.YELLOW)));
        }
        sendSupportMessage(sender);
    }

    public static void sendSupportMessage(CommandSender sender) {
        sender.sendMessage("");
        String version = UltraCosmeticsData.get().getPlugin().getUpdateChecker().getCurrentVersion().versionClassifierCommit();
        sender.sendMessage("You are running UC " + version + " on " + Bukkit.getName() + " " + Bukkit.getVersion());
        sender.sendMessage(ChatColor.GREEN + "If you need help, join the Discord for support: https://discord.gg/mDSbzGPykk");
        sender.sendMessage(ChatColor.GREEN + "When you join, share a screenshot of this message.");
    }
}

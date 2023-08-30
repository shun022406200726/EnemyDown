package plugin.enemydown.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player player){
      return onExecutePlayerCommand(player);
    }else {
      return onExecuteNPCCommand(sender);
    }

  }
  public abstract boolean onExecutePlayerCommand(Player player);
  public abstract boolean onExecuteNPCCommand(CommandSender sender);
}

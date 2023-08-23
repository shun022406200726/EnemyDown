package plugin.enemydown.command;

import java.util.List;
import java.util.SplittableRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnemyDownCommand implements CommandExecutor {


  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player player){

      ItemStack customSword = new ItemStack(Material.DIAMOND_SWORD);
      ItemMeta meta = customSword.getItemMeta();
      meta.setDisplayName("最強の剣");
      meta.addEnchant(Enchantment.DAMAGE_ALL, 10, true); // エンチャントを追加
// 他のアイテムのカスタマイズ（属性やロアなど）も可能です
      customSword.setItemMeta(meta);
      player.getInventory().addItem(customSword);




      World world=player.getWorld();
      player.setHealth(20);
      player.setFoodLevel(20);

      world.spawnEntity(getEnemySpawnLocation(player, world), getEnemy());
    }
    return false;
  }

  private static Location getEnemySpawnLocation(Player player, World world) {
    Location playerLocation= player.getLocation();
    int randomX=new SplittableRandom().nextInt(20)-10;
    int randomZ=new SplittableRandom().nextInt(20)-10;
    double x = playerLocation.getX()+randomX;
    double y = playerLocation.getY();
    double z = playerLocation.getZ()+randomZ;

    return new Location(world, x, y, z);
  }

  private static EntityType getEnemy() {
    List<EntityType> enemyList=List.of(EntityType.ZOMBIE,EntityType.SKELETON);
    int random=new SplittableRandom().nextInt(2);
    EntityType enemy = enemyList.get(random);
    return enemy;
  }
}

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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class EnemyDownCommand implements CommandExecutor {


  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player player){
      PlayerInventory inventory = player.getInventory();
      inventory.setHelmet(new ItemStack(Material.NETHERITE_HELMET));
      inventory.setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
      inventory.setBoots(new ItemStack(Material.NETHERITE_BOOTS));
      inventory.setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
      inventory.setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));

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

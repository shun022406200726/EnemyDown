package plugin.enemydown.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SplittableRandom;
import org.bukkit.Bukkit;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import plugin.enemydown.Main;
import plugin.enemydown.data.PlayerScore;

public class EnemyDownCommand implements CommandExecutor , Listener {

  private Main main;
  private int gameTime=20;
  List<PlayerScore>playerScoreList=new ArrayList<>();

  public EnemyDownCommand(Main main) {
    this.main=main;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player player){
      if(playerScoreList.isEmpty()){
        addNewPlayer(player);
      }else{
        for(PlayerScore playerScore:playerScoreList){
          if(!playerScore.getPlayerName().equals(player.getName())){
            addNewPlayer(player);
          }
        }
      }



      initPlayerStatus(player);

      World world=player.getWorld();
      gameTime=20;
      Bukkit.getScheduler().runTaskTimer(main,Runnable->{
        if (gameTime<=0){
          Runnable.cancel();
          player.sendMessage("終了");
          return;
        }
        world.spawnEntity(getEnemySpawnLocation(player, world), getEnemy());
        gameTime -=5;
      },0,5*20);



    }
    return false;
  }

  private void addNewPlayer(Player player) {
    PlayerScore newPlayer=new PlayerScore();
    newPlayer.setPlayerName(player.getName());
    playerScoreList.add(newPlayer);
  }

  @EventHandler
public void onEnemyDeath(EntityDeathEvent e) {
  Player player=e.getEntity().getKiller();
    if (Objects.isNull(player) || playerScoreList.isEmpty()) {
      return;
    }
    for(PlayerScore playerScore:playerScoreList){
      if(playerScore.getPlayerName().equals(player.getName())){
        playerScore.setScore(playerScore.getScore()+10);
        player.sendMessage("敵を倒した！" + playerScore.getScore() + "点！");
      }

    }


  }



/**
 * 初期状態へ更新
 */
  private static void initPlayerStatus(Player player) {
    player.setHealth(20);
    player.setFoodLevel(20);
    PlayerInventory inventory = player.getInventory();
    inventory.setHelmet(new ItemStack(Material.NETHERITE_HELMET));
    inventory.setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
    inventory.setBoots(new ItemStack(Material.NETHERITE_BOOTS));
    inventory.setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
    inventory.setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));
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

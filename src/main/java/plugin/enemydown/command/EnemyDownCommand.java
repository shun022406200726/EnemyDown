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
import org.bukkit.entity.LivingEntity;
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

  List<PlayerScore>playerScoreList=new ArrayList<>();

  public EnemyDownCommand(Main main) {
    this.main=main;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player player){
      PlayerScore nowPlayer = getPlayerScore(player);

      initPlayerStatus(player);

      World world=player.getWorld();
      nowPlayer.setGameTime(20);
      Bukkit.getScheduler().runTaskTimer(main,Runnable->{
        if (nowPlayer.getGameTime()<=0){
          Runnable.cancel();
          player.sendTitle("終了",
              nowPlayer.getPlayerName()+"  "+nowPlayer.getScore()+"点!",
              5,60,10);
          nowPlayer.setScore(0);
          List<Entity> nearbyEnemies = player.getNearbyEntities(50, 0, 50);
          for(Entity enemy:nearbyEnemies){
            switch (enemy.getType()) {
              case ZOMBIE, SKELETON, WITCH -> enemy.remove();
            }
          }
          return;
        }
        world.spawnEntity(getEnemySpawnLocation(player, world), getEnemy());
        nowPlayer.setGameTime(nowPlayer.getGameTime()-5);
      },0,5*20);



    }
    return false;
  }


/**
 * 新規のプレイヤー情報の追加
 * @param player　コマンドを実行したプレイヤー
 * @return 新規プレイヤー
 */

  private PlayerScore addNewPlayer(Player player) {
    PlayerScore newPlayer=new PlayerScore();
    newPlayer.setPlayerName(player.getName());
    playerScoreList.add(newPlayer);
    return newPlayer;
  }

  @EventHandler
public void onEnemyDeath(EntityDeathEvent e) {
    LivingEntity enemy = e.getEntity();
    Player player= enemy.getKiller();
    if (Objects.isNull(player) || playerScoreList.isEmpty()) {
      return;
    }
    int point = switch (enemy.getType()) {
      case ZOMBIE -> 10;
      case SKELETON -> 20;
      case WITCH -> 30;
      default -> 0;
    };

    for(PlayerScore playerScore:playerScoreList) {
      if (playerScore.getPlayerName().equals(player.getName())) {
        playerScore.setScore(playerScore.getScore() + point);
        player.sendMessage("敵を倒した！" + playerScore.getScore() + "点！");
      }
    }
    }

/**
 * 現在実行しているプレイヤーの情報を取得する
 * @param player コマンドを実行したプレイヤー
 * @return　現在実行しているプレイヤーのスコア情報
 *
 */
    private PlayerScore getPlayerScore(Player player) {
      if(playerScoreList.isEmpty()){
        return addNewPlayer(player);
      }else{
        for(PlayerScore playerScore:playerScoreList){
          if(!playerScore.getPlayerName().equals(player.getName())){
            return addNewPlayer(player);
          }else{
            return playerScore;
          }
        }
      }
      return null;
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
    List<EntityType> enemyList=List.of(EntityType.ZOMBIE,EntityType.SKELETON,EntityType.WITCH);
    return enemyList.get(new SplittableRandom().nextInt(enemyList.size()));
  }
}

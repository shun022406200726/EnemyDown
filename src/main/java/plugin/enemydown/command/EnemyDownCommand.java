package plugin.enemydown.command;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SplittableRandom;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import plugin.enemydown.PlayerScoreData;
import plugin.enemydown.Main;
import plugin.enemydown.data.ExecutingPlayer;
import plugin.enemydown.mapper.PlayerScoreMapper;
import plugin.enemydown.mapper.data.PlayerScore;


public class  EnemyDownCommand extends BaseCommand implements Listener {

  public static final int GAME_TIME = 20;
  public static final String EASY = "easy";
  public static final String NORMAL = "normal";
  public static final String HARD = "hard";
  public static final String NONE = "none";
  public static final String LIST = "list";
  private Main main;
  private PlayerScoreData playerScoreData =new PlayerScoreData();
  List<ExecutingPlayer> executingPlayerList =new ArrayList<>();
  List<Entity>spawnEntityList =new ArrayList<>();


  public EnemyDownCommand(Main main) {
    this.main=main;




  }
  @Override
  public boolean onExecuteNPCCommand(CommandSender sender, Command command, String label, String[] args) {
    return false;
  }
  @Override
  public boolean onExecutePlayerCommand(Player player, Command command, String label, String[] args) {
    if (args.length == 1 && (LIST.equals(args[0]))) {
      sendPlayerScoreList(player);
      return false;
    }
    String difficulty = getDifficulty(player, args);
    if (difficulty.equals(NONE)) {
      return false;
    }
    ExecutingPlayer nowPlayer = getPlayerScore(player);

    initPlayerStatus(player);

    gamePlay(player, nowPlayer, difficulty);
    return true;
  }
  private void sendPlayerScoreList(Player player) {
    List<PlayerScore> playerScoreList = playerScoreData.selectList();
    for(PlayerScore playerScore:playerScoreList){
      player.sendMessage(playerScore.getId() + "|"
          + playerScore.getPlayer_name() + "|" + playerScore.getScore()
          + "|" + playerScore.getDifficulty() + "|"
          + playerScore.getRegistered_at().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }}
/**
 *難易度をコマンド引数から取得
 * @param player コマンドを実行したプレイヤー
 * @param args コマンド引数
 * @return 難易度
 */
  private String getDifficulty(Player player, String[] args) {
    String difficulty=EASY;
    if (args.length == 1 && (EASY.equals(args[0])||NORMAL.equals(args[0])||HARD.equals(args[0]))) {
      return args[0];
    }
    player.sendMessage(ChatColor.RED+"難易度は？ easy normal hard");
    return NONE;
  }





  /**
 * 新規のプレイヤー情報の追加
 * @param player　コマンドを実行したプレイヤー
 * @return 新規プレイヤー
 */

  private ExecutingPlayer addNewPlayer(Player player) {
    ExecutingPlayer newPlayer=new ExecutingPlayer(player.getName());
    executingPlayerList.add(newPlayer);
    return newPlayer;
  }

  @EventHandler
public void onEnemyDeath(EntityDeathEvent e) {
    LivingEntity enemy = e.getEntity();
    Player player= enemy.getKiller();

    if (Objects.isNull(player) || spawnEntityList.stream()
        .noneMatch(entity -> entity.equals(enemy))) {
      return;
    }

     executingPlayerList.stream()
        .filter(p -> p.getPlayerName().equals(player.getName()))
        .findFirst()
        .ifPresent(p ->{
          int point = switch (enemy.getType()) {
            case ZOMBIE -> 10;
            case SKELETON -> 20;
            case WITCH -> 30;
            default -> 0;
        };
          p.setScore(p.getScore() + point);
          player.sendMessage("敵を倒した！" + p.getScore() + "点！");
        });
    }

/**
 * 現在実行しているプレイヤーの情報を取得する
 * @param player コマンドを実行したプレイヤー
 * @return　現在実行しているプレイヤーのスコア情報
 *
 */
    private ExecutingPlayer getPlayerScore(Player player) {
      ExecutingPlayer executingPlayer =new ExecutingPlayer(player.getName());
      if(executingPlayerList.isEmpty()){
        executingPlayer = addNewPlayer(player);
      }else{
        executingPlayer = executingPlayerList.stream().findFirst()
            .map(ps -> ps.getPlayerName().equals(player.getName())
                ? ps
                : addNewPlayer(player)).orElse(executingPlayer);
      }
      executingPlayer.setGameTime(GAME_TIME);
      executingPlayer.setScore(0);
      removePotionEffect(player);
      return executingPlayer;
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
/**
 * ゲームを始める
 * @param player　コマンドを実行したプレイヤー
 * @param nowExecutingPlayer プレイヤースコア情報
 * @param difficulty 難易度
 */
  private void gamePlay(Player player, ExecutingPlayer nowExecutingPlayer,String difficulty) {
    Bukkit.getScheduler().runTaskTimer(main,Runnable->{
      if (nowExecutingPlayer.getGameTime()<=0){
        Runnable.cancel();

        player.sendTitle("終了",
            nowExecutingPlayer.getPlayerName()+"  "+ nowExecutingPlayer.getScore()+"点!",
            5,60,10);
//        String url="jdbc:mysql://localhost:3306/spigot_server";
//        String user="root";
//        String path ="shun0224mysql";
//
//        try(Connection con = DriverManager.getConnection(url,user,path);
//            Statement statement=con.createStatement()) {
//          statement.executeUpdate(
//              "insert player_score(player_name, score, difficulty, registered_at)"
//                  + "values('" + nowExecutingPlayer.getPlayerName() + "'," + nowExecutingPlayer.getScore() +",'"
//                  +difficulty +"',now());");
//        }catch (SQLException e){
//          e.printStackTrace();
//        }

        spawnEntityList.forEach(Entity::remove);

        spawnEntityList.clear();

        removePotionEffect(player);


        try (SqlSession session = sqlSessionFactory.openSession(true)) {
          PlayerScoreMapper mapper = session.getMapper(PlayerScoreMapper.class);
          mapper.insert(
              new PlayerScore(nowExecutingPlayer.getPlayerName()
                  ,nowExecutingPlayer.getScore()
                  ,difficulty));
        }
        return;
      }
      Entity spawnEntity = player.getWorld().spawnEntity(getEnemySpawnLocation(player), getEnemy(difficulty));
      spawnEntityList.add(spawnEntity);
      nowExecutingPlayer.setGameTime(nowExecutingPlayer.getGameTime()-5);
    },0,5*20);
  }
  private static Location getEnemySpawnLocation(Player player) {
    Location playerLocation= player.getLocation();
    int randomX=new SplittableRandom().nextInt(20)-10;
    int randomZ=new SplittableRandom().nextInt(20)-10;
    double x = playerLocation.getX()+randomX;
    double y = playerLocation.getY();
    double z = playerLocation.getZ()+randomZ;

    return new Location(player.getWorld(), x, y, z);
  }
/**
 * ランダムで敵を抽選してその結果を取得します
 * @param difficulty 難易度
 * @return 敵
 */
  private static EntityType getEnemy(String difficulty) {
    List<EntityType> enemyList = switch (difficulty) {
      case NORMAL -> List.of(EntityType.ZOMBIE, EntityType.SKELETON);
      case HARD -> List.of(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.WITCH);
      default -> List.of(EntityType.ZOMBIE);
    };

    return enemyList.get(new SplittableRandom().nextInt(enemyList.size()));
  }

  /**
   * プレイヤーの状態異常を無効化
   * @param player コマンドを実行したプレイヤー
   */
  private void removePotionEffect(Player player) {
    player.getActivePotionEffects().stream()
        .map(PotionEffect::getType)
        .forEach(player::removePotionEffect);
  }
}

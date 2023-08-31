package plugin.enemydown.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
@Getter
@Setter
public class PlayerScore {

  private String playerName;
  private int score;
  private int gameTime;

  public PlayerScore(String playerName) {
    this.playerName=playerName;
  }
}

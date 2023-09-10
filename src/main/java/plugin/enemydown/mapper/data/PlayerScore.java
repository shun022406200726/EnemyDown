package plugin.enemydown.mapper.data;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * プレイヤーの情報を扱うオブジェクト
 * DBに存在するテーブルと連動する
 */

@Getter
@Setter
@NoArgsConstructor
public class PlayerScore {
  private int id;
  private String player_name;
  private int score;
  private String difficulty;
  private LocalDateTime registered_at;

  public PlayerScore(String playerName,int score,String difficulty){
    this.player_name=playerName;
    this.score=score;
    this.difficulty=difficulty;
  }
}

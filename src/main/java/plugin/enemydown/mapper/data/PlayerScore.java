package plugin.enemydown.mapper.data;

import lombok.Getter;
import lombok.Setter;

/**
 * プレイヤーの情報を扱うオブジェクト
 * DBに存在するテーブルと連動する
 */

@Getter
@Setter
public class PlayerScore {
  private int id;
  private String player_name;
  private int score;
  private String difficulty;
  private  String registered_at;

}

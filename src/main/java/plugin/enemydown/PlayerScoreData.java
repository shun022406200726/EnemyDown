package plugin.enemydown;

import java.io.InputStream;
import java.util.List;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import plugin.enemydown.mapper.PlayerScoreMapper;
import plugin.enemydown.mapper.data.PlayerScore;

/**
 * DB接続やそれに付随する登録や更新を行うクラス
 */
public class PlayerScoreData {

  private SqlSessionFactory sqlSessionFactory;

  public PlayerScoreData() {
    try {
      InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
      this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
/**
 * player_scoreテーブルから一覧情報を取得
 * @return スコア情報の一覧
 */
  public List<PlayerScore> selectList() {
    try (SqlSession session = sqlSessionFactory.openSession()) {
      PlayerScoreMapper mapper = session.getMapper(PlayerScoreMapper.class);
      return mapper.selectList();
    }
  }
}

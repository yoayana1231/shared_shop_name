package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.co.sss.shop.entity.ViewHistories;

public interface RecommendRepository extends JpaRepository<ViewHistories, Integer> {
	
	// ログインユーザのいちばん最新の閲覧商品情報を検索
	// ROWNUM：検索結果に対して1から割り振る
	@Query(value = "SELECT * FROM( "
			+ "SELECT * FROM view_histories WHERE user_id=:userId ORDER BY viewed_at DESC"
			+ ") WHERE ROWNUM = 1", nativeQuery = true)
	ViewHistories findLatestViewHistoryByUserId(@Param("userId")Integer userId);
	
	
	// 他ユーザと商品1つを条件に同カテゴリの商品を新しい順に取得する
	@Query(value = "SELECT v FROM ViewHistories v INNER JOIN Item i ON v.item.id = i.id "
			+ "INNER JOIN Category c ON v.item.category.id = c.id "
			+ "WHERE i.category.id =:categoryId AND v.user.id <>:userId "
			+ "AND v.deleteFlag = 0 AND i.deleteFlag = 0 AND c.deleteFlag = 0"
			+ "ORDER BY v.viewedAt DESC")
	List<ViewHistories> findOtherUsersViewHistoriesByCategory
			(@Param("categoryId") Integer categoryId, @Param("userId") Integer userId);
	
	// 他ユーザの閲覧履歴から新しい順で20件取得する
	@Query(value = "SELECT * FROM("
			+ "SELECT v.* FROM view_histories v INNER JOIN items i ON v.item_id = i.id "
			+ "INNER JOIN categories c ON i.category_id = c.id "
			+ "WHERE user_id<>:userId AND v.delete_flag = 0 AND i.delete_flag = 0 AND c.delete_flag = 0 "
			+ "ORDER BY viewed_at DESC) WHERE ROWNUM <= 20", nativeQuery = true)
	List<ViewHistories> findTop20OtherUsersLatestViewHistories(@Param("userId") Integer userId);
}

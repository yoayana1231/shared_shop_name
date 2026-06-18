package jp.co.sss.shop.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.co.sss.shop.entity.Reviews;

public interface ReviewsRepository extends JpaRepository<Reviews, Integer> {

	// 商品IDと削除フラグを条件にした検索
	List<Reviews> findByItemIdAndDeleteFlag(Integer itemId, Integer i);

	// 商品IDとユーザーIDと削除フラグを条件にした検索
	Reviews findByItemIdAndUserIdAndDeleteFlag(Integer itemId, Integer userId, Integer i);

	// レビューIDと削除フラグを条件にした検索
	Reviews findByIdAndDeleteFlag(Integer id, Integer i);

	// 評価ごとの件数を取得するクエリ
	@Query("SELECT r.rating, COUNT(r) FROM Reviews r WHERE r.deleteFlag = 0 GROUP BY r.rating")
	List<Object[]> countCountsByRating();

	// 評価ごとにグループ化して件数を集計するクエリ
	// 【修正】特定の商品ID（itemId）かつ削除されていないレビューのみを集計する
	@Query("SELECT r.rating AS rating, COUNT(r) AS count FROM Reviews r " +
			"WHERE r.item.id = :itemId AND r.deleteFlag = 0 " +
			"GROUP BY r.rating ORDER BY r.rating DESC")
	List<Map<String, Object>> countReviewsGroupByRatingAndItemId(@Param("itemId") Integer itemId);

	// レビュー数が多い順に商品（Item）とレビュー数を取得する
	@Query("SELECT r.item, COUNT(r) AS reviewCount FROM Reviews r WHERE r.deleteFlag = 0 GROUP BY r.item ORDER BY reviewCount DESC")
	List<Object[]> findTopItemsByReviewCount();

}

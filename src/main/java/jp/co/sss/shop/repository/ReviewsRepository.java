package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.shop.entity.Reviews;

public interface ReviewsRepository extends JpaRepository<Reviews, Integer> {
	
	// 商品IDと削除フラグを条件にした検索
	List<Reviews> findByItemIdAndDeleteFlag(Integer itemId, Integer i);
	
	// 商品IDとユーザーIDと削除フラグを条件にした検索
	Reviews findByItemIdAndUserIdAndDeleteFlag
				(Integer itemId, Integer userId, Integer i);
	
	// レビューIDと削除フラグを条件にした検索
	Reviews findByIdAndDeleteFlag(Integer id, Integer i);

}

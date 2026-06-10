package jp.co.sss.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.shop.entity.Reviews;

public interface ReviewsRepository extends JpaRepository<Reviews, Integer> {
	
	// 商品IDとユーザーIDと削除フラグを条件にした検索
		Reviews findByItemIdAndUserIdAndDeleteFlag
			(Integer itemId, Integer userId, Integer i);

}

package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.shop.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer>{
	
	// 商品IDとユーザーIDを条件にした検索
	Favorite findByItemIdAndUserId(Integer itemId, Integer userId);
	
	// ユーザIDと削除フラグを条件にした検索
	List<Favorite>findByUserIdAndDeleteFlag(Integer useId, Integer i);

}

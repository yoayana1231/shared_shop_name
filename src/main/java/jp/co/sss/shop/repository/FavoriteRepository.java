package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.co.sss.shop.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer>{
	
	// 商品IDとユーザーIDを条件にした検索
	Favorite findByItemIdAndUserId(Integer itemId, Integer userId);
	
	// ユーザIDと削除フラグを条件にした検索
	List<Favorite>findByUserIdAndDeleteFlag(Integer useId, Integer i);
	
	// ログイン中のユーザが登録している商品IDだけを検索
	@Query("SELECT f.item.id FROM Favorite f WHERE f.user.id = :userId "
			+ "AND f.deleteFlag = 0")
	List<Integer>findItemIdsByUserId(@Param("userId") Integer userId);

}

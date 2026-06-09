package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.shop.entity.Favorite;
import jp.co.sss.shop.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer>{
	
	// 商品IDとユーザーIDを条件にした検索
	Favorite findByItemIdAndUserId(Integer itemId, Integer userId);
	
	// お気に入り全件検索(ユーザー毎)
	List<Favorite>findByUser(User user);

}

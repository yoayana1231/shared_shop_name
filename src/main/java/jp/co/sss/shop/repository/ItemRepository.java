package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Item;

/**
 * itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

	/**
	 * 商品情報を登録日付順に取得 管理者機能で利用
	 * @param deleteFlag 削除フラグ
	 * @param pageable ページング情報
	 * @return 商品エンティティのページオブジェクト
	 */
	@Query("SELECT i FROM Item i INNER JOIN i.category c WHERE i.deleteFlag =:deleteFlag ORDER BY i.insertDate DESC,i.id DESC")
	Page<Item> findByDeleteFlagOrderByInsertDateDescPage(
	        @Param(value = "deleteFlag") int deleteFlag, Pageable pageable);

	/**
	 * 商品IDと削除フラグを条件に検索（管理者機能で利用）
	 * @param id 商品ID
	 * @param deleteFlag 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByIdAndDeleteFlag(Integer id, int deleteFlag);

	/**
	 * 商品名と削除フラグを条件に検索 (ItemValidatorで利用)
	 * @param name 商品名
	 * @param notDeleted 削除フラグ
	 * @return 商品エンティティ
	 */
	public Item findByNameAndDeleteFlag(String name, int notDeleted);
	
//	売れ筋順
//	売れてる奴だけ表示
//	SELECT * FROM items ORDER BY (SELECT SUM(quantity) FROM order_items GROUP BY item_id) DESC;
//	@Query("SELECT i FROM Item i ORDER BY (SELECT SUM(oi.quantity)  FROM OrderItem oi GROUP BY oi.item.id) DESC")
	@Query("SELECT i FROM Item i INNER JOIN OrderItem oi ON oi.item = i GROUP BY i.id, i.name, i.price, i.description, i.image, i.stock, i.deleteFlag, i.insertDate, i.category.id ORDER BY SUM(oi.quantity) DESC")
	List<Item> findByQuantityDesc();
	
//	売れてないやつも表示
	@Query("SELECT i FROM Item i LEFT JOIN OrderItem oi ON oi.item = i GROUP BY i.id, i.name, i.price, i.description, i.image, i.stock, i.deleteFlag, i.insertDate, i.category.id ORDER BY SUM(oi.quantity) DESC NULLS LAST")
	List<Item> findAllByQuantityDesc();

	
//	新着順のリポジトリ
	List<Item> findAllByOrderByInsertDateDesc();

	
}

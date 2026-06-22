package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.entity.ViewHistories;

public interface ViewHistoriesRepository extends JpaRepository<ViewHistories, Integer> {

	List<ViewHistories> findByUser(UserBean user);

	ViewHistories findByUserAndItem(User user, Item item);
	
	@Query(value = "SELECT v FROM ViewHistories v INNER JOIN Item i ON v.item.id = i.id "
			+ "INNER JOIN Category c ON i.category.id = c.id "
			+ "WHERE v.user =:user AND v.deleteFlag = 0 AND i.deleteFlag = 0 AND c.deleteFlag = 0")
	List<ViewHistories> findByUserOrderByViewedAtDesc(User user);

}

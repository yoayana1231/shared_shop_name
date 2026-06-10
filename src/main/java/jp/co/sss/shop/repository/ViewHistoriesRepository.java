package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.entity.ViewHistories;

public interface ViewHistoriesRepository extends JpaRepository<ViewHistories, Integer> {

	List<ViewHistories> findByUser(UserBean user);

	ViewHistories findByUserAndItem(User user, Item item);

	List<ViewHistories> findByUserOrderByViewedAtDesc(User user);

}

package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.entity.ViewHistories;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.ReviewsRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.repository.ViewHistoriesRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.RecommendsService;

public class ClientItemShowControllerOsusume {
	@Autowired
	ItemRepository itemRepository;

	/*
	 * カテゴリ情報
	 */
	@Autowired
	CategoryRepository categoryRepository;

	// ViewHistoryリポジトリ
	@Autowired
	ViewHistoriesRepository viewHistoriesRepository;

	// Userリポジトリ
	@Autowired
	UserRepository userRepository;
	
	// Reviewリポジトリ
	@Autowired
	ReviewsRepository reviewRepository;
	
	// recommendサービス
	@Autowired
	RecommendsService recommendsService;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;
	@RequestMapping(path = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model, HttpSession session) {
		
		// カテゴリ表示用の検索
		model.addAttribute("categories", categoryRepository.findAll());
		// アイテム全件検索
		model.addAttribute("items", itemRepository.findAll());
		
		// 市川実装	閲覧履歴 / 吉永実装 おすすめ表示
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean != null) {
			User user = userRepository.getReferenceById(userBean.getId());
			
			// 閲覧履歴表示
			List<ViewHistories> histories = viewHistoriesRepository.findByUserOrderByViewedAtDesc(user);
			model.addAttribute("histories", histories);
			
			// おすすめ表示
			recommendsService.recommend(model, session);
		}
		
		return "index";
	}
}

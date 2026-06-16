package jp.co.sss.shop.controller.client.item;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Reviews;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.entity.ViewHistories;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.ReviewsRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.repository.ViewHistoriesRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.RecommendsService;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ClientItemShowController {
	/**
	 * 商品情報
	 */
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

	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
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

	//	売れてない奴も表示する（商品一覧）
	@RequestMapping(path = "/client/item/list/{sortType}", method = { RequestMethod.GET })
	public String clientItem(@PathVariable int sortType, Model model) {
		
		model.addAttribute("categories", categoryRepository.findAll());
		model.addAttribute("items", itemRepository.findAllByQuantityDesc());
		
		//		新着順
		if (sortType == 1) {
			model.addAttribute("items", itemRepository.findAllByOrderByInsertDateDesc());
			//		売れ筋順	
		} else {
			model.addAttribute("items", itemRepository.findAllByQuantityDesc());
		}
		return "client/item/list";
	}

	/*
	 * 吉永作成 一覧表示 カテゴリ検索
	 * 
	 * @param model Viewとの値受渡し
	 * @return "client/item/list" 商品一覧
	 */
	@GetMapping(path = "/client/item/list/category/{categoryId}")
	public String categorySort(@PathVariable Integer categoryId, Model model) {
		
		model.addAttribute("categories", categoryRepository.findAll());
		List<Item> items = itemRepository.findByCategoryId(categoryId);
		model.addAttribute("items", items);

		return "client/item/list";
	}

	//石田実装 あいまい検索用コントローラー
	//name属性 search
	//新規追加リポジトリメソッド findByNameContaining
	@RequestMapping("/client/item/list/search")
	public String clientItemListSearch(String search, Model model) {
		model.addAttribute("categories", categoryRepository.findAll());
		model.addAttribute("items", itemRepository.findByNameContaining(search));
		return "client/item/list";
	}

	/**
	 * 市川実装	商品詳細画面用コントローラー
	 * 吉永実装 レビュー表示部分
	 * 
	 * @param id
	 * @param model		Viewへの値受渡し
	 * @param session	ログイン確認
	 * @return			商品詳細画面
	 */
	@GetMapping("/client/item/detail/{id}")
	public String clientItemDetail(@PathVariable Integer id, Model model, HttpSession session) {

		Item item = itemRepository.getReferenceById(id);
		ItemBean itemBean = new ItemBean();
		itemBean.setId(item.getId());
		itemBean.setName(item.getName());
		itemBean.setPrice(item.getPrice());
		itemBean.setDescription(item.getDescription());
		itemBean.setStock(item.getStock());
		itemBean.setImage(item.getImage());
		itemBean.setCategoryId(item.getCategory().getId());
		itemBean.setCategoryName(item.getCategory().getName());

		model.addAttribute("item", itemBean);

		UserBean userBean = (UserBean) session.getAttribute("user");

		User user = null;

		if (userBean != null) {
			user = userRepository.getReferenceById(userBean.getId());
		}

		// ログインしている（一般会員である）場合のみ、閲覧履歴を保存・更新する
		if (user != null) {
			saveOrUpdateViewHistory(user, item);
		}
		
		//レビュー表示
		//詳細画面を表示している商品IDかつ削除フラグが0のものを検索
		List<Reviews> itemReviews =
				reviewRepository.findByItemIdAndDeleteFlag(id, 0);
		
		//リクエストスコープに格納
		model.addAttribute("itemReviews", itemReviews);
		
		return "client/item/detail";
	}

	/**
	 * 市川実装	閲覧履歴用メソッド
	 * 新規追加リポジトリメソッド	findByUserAndItem(User user, Item item);
	 * 
	 * @param user	ログインしているユーザー
	 * @param item	閲覧した商品
	 */
	private void saveOrUpdateViewHistory(User user, Item item) {
		// すでに同じユーザーが同じ商品の履歴を持っているか確認
		ViewHistories history = viewHistoriesRepository.findByUserAndItem(user, item);

		if (history != null) {
			history.setViewedAt(LocalDateTime.now());
			viewHistoriesRepository.save(history);
		} else {
			ViewHistories newHistory = new ViewHistories();
			newHistory.setUser(user);
			newHistory.setItem(item);
			newHistory.setViewedAt(LocalDateTime.now());
			viewHistoriesRepository.save(newHistory);
		}

	}
	
}
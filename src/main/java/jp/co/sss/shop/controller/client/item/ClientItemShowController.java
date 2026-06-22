package jp.co.sss.shop.controller.client.item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import jp.co.sss.shop.repository.FavoriteRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.ReviewsRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.repository.ViewHistoriesRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.RecommendsService;
import jp.co.sss.shop.util.Constant;

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
	
	// Favoriteリポジトリ
	@Autowired
	FavoriteRepository favoriteRepository;

	// recommendサービス
	@Autowired
	RecommendsService recommendsService;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;
	
	/*
	 * カテゴリ検索表示用 フラグ
	 * @param  true:一覧表示中  false:カテゴリ検索中
	 */
	boolean isAllList = false;

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
		// 売れ筋検索
		model.addAttribute("items", itemRepository.findAllByQuantityDesc());

		// 市川実装	閲覧履歴 / 吉永実装 おすすめ表示
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean != null) {
			User user = userRepository.getReferenceById(userBean.getId());
			
			// ログイン中ならお気に入りリストの商品IDを取得
			List<Integer> favoriteItemIds = favoriteRepository.findItemIdsByUserId(userBean.getId());
			model.addAttribute("favoriteItemIds", favoriteItemIds);

			// 閲覧履歴表示
			List<ViewHistories> histories = viewHistoriesRepository.findByUserOrderByViewedAtDesc(user);
			model.addAttribute("histories", histories);

			// おすすめ表示
			recommendsService.recommend(model, session);
		}

		//		レビュー数ランキング
		List<Object[]> reviewRanking = reviewRepository.findTopItemsByReviewCount();

		// 画面で扱いやすいように Map のリストに詰め替える
		List<Map<String, Object>> rankingList = new ArrayList<>();
		int rank = 1;

		for (Object[] reviewsRanking : reviewRanking) {
			Item item = (Item) reviewsRanking[0];
			Long count = (Long) reviewsRanking[1];

			Map<String, Object> reviewsRank = new HashMap<>();
			reviewsRank.put("rank", rank++); // 順位 (1, 2, 3...)
			reviewsRank.put("item", item); // 商品エンティティ
			reviewsRank.put("reviewCount", count); // レビュー件数

			rankingList.add(reviewsRank);
		}

		// "rankingList" という名前でViewに渡す
		model.addAttribute("rankingList", rankingList);

		return "index";
	}

	//	売れてない奴も表示する（商品一覧）
	@RequestMapping(path = "/client/item/list/{sortType}", method = { RequestMethod.GET })
	public String clientItem(@PathVariable int sortType, 
			HttpSession session, Model model) {
		
		model.addAttribute("categories", categoryRepository.findAll());
		model.addAttribute("items", itemRepository.findAllByQuantityDesc());
		
		// カテゴリ検索表示用のフラグをtrueにする
		isAllList = true;
		model.addAttribute("flag", isAllList);

		if (sortType == 1) {
			// 新着順
			model.addAttribute("items", itemRepository.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED));
		} else {
			// 売れ筋順	
			model.addAttribute("items", itemRepository.findAllByQuantityDesc());
		}
		
		// ログイン中のユーザー情報を取得
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean != null) {
			Integer userId = userBean.getId();
			// ログイン中ならお気に入りリストの商品IDを取得
			List<Integer> favoriteItemIds = favoriteRepository.findItemIdsByUserId(userId);
	        model.addAttribute("favoriteItemIds", favoriteItemIds);
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
	public String categorySort(@PathVariable Integer categoryId,
			HttpSession session, Model model) {

		// カテゴリ全件検索
		model.addAttribute("categories", categoryRepository.findAll());
		
		// カテゴリ毎の検索結果をリクエストスコープに保存
		List<Item> items = itemRepository.findByCategoryIdAndDeleteFlag(categoryId, Constant.NOT_DELETED);
		model.addAttribute("items", items);
		
		model.addAttribute("id", categoryId);
		
		// ログイン中のユーザー情報を取得
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean != null) {
			Integer userId = userBean.getId();
			// ログイン中ならお気に入りリストの商品IDを取得
			List<Integer> favoriteItemIds = favoriteRepository.findItemIdsByUserId(userId);
	        model.addAttribute("favoriteItemIds", favoriteItemIds);
		}

		return "client/item/list";
	}

	//石田実装 あいまい検索用コントローラー
	//name属性 search
	//新規追加リポジトリメソッド findByNameContaining
	@RequestMapping("/client/item/list/search")
	public String clientItemListSearch(String search, HttpSession session, Model model) {
		
		// searchがnull→全件検索
		// カテゴリ検索表示用のフラグをtrueにする
		if (search == "") {
			isAllList = true;
			model.addAttribute("flag", isAllList);
		}
		
		model.addAttribute("categories", categoryRepository.findAll());
		model.addAttribute("items", itemRepository.findByNameContainingAndDeleteFlag(search, Constant.NOT_DELETED));
		
		// ログイン中のユーザー情報を取得
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean != null) {
			Integer userId = userBean.getId();
			// ログイン中ならお気に入りリストの商品IDを取得
			List<Integer> favoriteItemIds = favoriteRepository.findItemIdsByUserId(userId);
	        model.addAttribute("favoriteItemIds", favoriteItemIds);
		}
		
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

		//  ここからレビューの平均	1〜5のカウントをすべて「0」で初期化したMapを作る
		Map<Integer, Long> ratingCounts = new HashMap<>();
		for (int i = 1; i <= 5; i++) {
			ratingCounts.put(i, 0L);
		}

		// データベースから集計結果を取得
		List<Object[]> results = reviewRepository.countCountsByRating();

		// 取得したデータをMapに上書きする
		for (Object[] result : results) {
			Integer rating = (Integer) result[0];
			Long count = (Long) result[1];

			// 1〜5の範囲内であればMapを更新
			if (rating != null && rating >= 1 && rating <= 5) {
				ratingCounts.put(rating, count);
			}
		}

		List<Map<String, Object>> ratingData = reviewRepository.countReviewsGroupByRatingAndItemId(id);

		// 総レビュー数と、星の総合レビューを計算
		long totalCount = 0;
		double totalStars = 0;

		for (Map<String, Object> row : ratingData) {
			int rating = Integer.parseInt(row.get("rating").toString());
			long count = (Long) row.get("count");

			totalCount += count;
			totalStars += (rating * count); // 評価 × 件数（例：★5が3件なら15点）
		}

		// 平均点の計算（0件の場合は0.0）
		double averageRating = totalCount > 0 ? totalStars / totalCount : 0.0;

		// 画面表示用のグラフデータ
		List<Map<String, Object>> itemRating = new ArrayList<>();
		for (int i = 5; i >= 1; i--) {
			int rating = i;
			long count = ratingData.stream()
					.filter(row -> Integer.parseInt(row.get("rating").toString()) == rating)
					.mapToLong(row -> (Long) row.get("count"))
					.findFirst()
					.orElse(0L);

			double percentage = totalCount > 0 ? ((double) count / totalCount) * 100 : 0;

			Map<String, Object> rowMap = new HashMap<>();
			rowMap.put("rating", rating);
			rowMap.put("count", count);
			rowMap.put("percentage", Math.round(percentage));
			itemRating.add(rowMap);
		}

		// レビューの「総数」「平均値」「グラフデータ」
		model.addAttribute("itemRating", itemRating);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("averageRating", averageRating); // 平均値を渡す

		// レビュー全件数
		model.addAttribute("ratingCounts", ratingCounts);

		//レビュー表示
		//詳細画面を表示している商品IDかつ削除フラグが0のものを検索
		List<Reviews> itemReviews = reviewRepository.findByItemIdAndDeleteFlag(id, Constant.NOT_DELETED);

		//レビュー情報
		model.addAttribute("itemReviews", itemReviews);
		//		レビュー総数
		model.addAttribute("reviewsSize", itemReviews.size());
		//		売れ筋順
		model.addAttribute("bestSelling", itemRepository.findAllByQuantityDesc());
		
		if (userBean != null) {
			Integer userId = userBean.getId();
			// ログイン中ならお気に入りリストの商品IDを取得
			List<Integer> favoriteItemIds = favoriteRepository.findItemIdsByUserId(userId);
	        model.addAttribute("favoriteItemIds", favoriteItemIds);
		}
		
		// セッションにエラーメッセージがあればリクエストスコープへ
		String error = (String) session.getAttribute("error");
		if (error != null) {
			model.addAttribute("error", error);
			session.removeAttribute("error");
		}

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
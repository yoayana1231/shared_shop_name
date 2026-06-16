package jp.co.sss.shop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.ViewHistories;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.RecommendRepository;

@Service
public class RecommendsService {
	
	// Recommendリポジトリ
	@Autowired
	RecommendRepository recommendRepository;
	
	// Itemリポジトリ
	@Autowired
	ItemRepository itemRepository;
	
	
	/*
	 * 吉永実装  おすすめ表示処理
	 */
	public void recommend(Model model, HttpSession session) {
		
		// 表示するリスト
		List<ViewHistories> recommends = new ArrayList<ViewHistories>();
		
		// 現在ログイン中のユーザIDを取得
		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		// 閲覧履歴があるかどうか検索
		List<ViewHistories> allList = recommendRepository.findAll();
		
		if (allList != null) {
			// ログインユーザの最新の閲覧商品情報を1件取得
			ViewHistories latest = 
					recommendRepository.findLatestViewHistoryByUserId(userId);
			
			if (latest != null) {
				
				// 取得した商品情報のカテゴリIDを取得
				Integer categoryId = latest.getItem().getCategory().getId();
				// 他ユーザが閲覧した同カテゴリの商品を新しいものから取得
				List<ViewHistories>temporarily1 = recommendRepository.
						findOtherUsersViewHistoriesByCategory(categoryId, userId);
				// データのチェック
				fillRecommends(recommends, temporarily1);
				
			}
			
			// 本リストの情報が4件以下なら次の処理へ
			if (recommends.size() < 4) {
				
				// 他ユーザが閲覧した商品を新しいものから取得
				List<ViewHistories>temporarily2 = recommendRepository.
						findTop20OtherUsersLatestViewHistories(userId);
				// データのチェック
				fillRecommends(recommends, temporarily2);
				
			}
		}
		
		// 閲覧履歴が無い場合、4件に達していない場合は売れ筋を4件表示
		if (allList == null || recommends.size() < 4) {
			
			// 売れ筋商品を検索
			List<Item> bestSeller = itemRepository.findAllByQuantityDesc();
			
			// データのチェック
			for (Item item : bestSeller) {
				if (recommends.size() >= 4) {
					break;
				}
				// 重複チェック
				if (!isDupCheckItem(recommends, item)) {
					ViewHistories history = new ViewHistories();
					history.setItem(item);
					recommends.add(history);
				}
			}
			
		}
		
		// 取得したリストをリクエストスコープへ格納
		model.addAttribute("recommends", recommends);
		
	}
	
	
	/*
	 * 重複チェック(ViewHistories)
	 * @return true : 重複あり  false : 重複なし
	 * @param list : 確認対象のリスト
	 * @param target : リストに新規追加したい要素
	 */
	private boolean isDupCheck(List<ViewHistories> list, ViewHistories target) {
		
		for (ViewHistories v : list) {
			if (v.getItem().getId().equals(target.getItem().getId())) {
				return true;
			}
		}
		return false;
		
	}
	
	
	/*
	 * 重複チェック(Item)
	 * @return true : 重複あり  false : 重複なし
	 * @param list : 確認対象の本リスト
	 * @param target : リストに新規追加したい要素
	 */
	private boolean isDupCheckItem(List<ViewHistories> rec, Item target) {
		
		for (ViewHistories v : rec) {
			if (v.getItem() != null &&
					v.getItem().getId().equals(target.getId())) {
				return true;
			}
		}
		return false;
		
	}
		
	
	/*
	 * 重複チェックしながら最大4件のリストにする処理
	 * @param recommends : 表示用本リスト
	 * @param list : 追加したい候補データリスト
	 */
	private void fillRecommends(List<ViewHistories> rec,
			List<ViewHistories> list) {
		
		for (ViewHistories history : list) {
			if (rec.size() > 4) {
				// 4件たまったら処理終了
				break;
			}
			// 重複チェック
			if (!isDupCheck(rec, history)) {
				// 重複なし、recommendsに追加
				rec.add(history);
			}
		}
		
	}
	

}

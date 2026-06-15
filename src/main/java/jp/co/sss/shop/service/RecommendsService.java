package jp.co.sss.shop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.ViewHistories;
import jp.co.sss.shop.repository.RecommendRepository;

@Service
public class RecommendsService {
	
	// Recommendリポジトリ
	@Autowired
	RecommendRepository recommendRepository;
	
	
	/*
	 * 吉永実装  おすすめ表示処理
	 */
	public void recommend(Model model, HttpSession session) {
		
		// 現在ログイン中のユーザIDを取得
		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		
		// ログインユーザの最新の閲覧商品情報を1件取得
		ViewHistories latest = recommendRepository.findLatestViewHistoryByUserId(userId);
		
		// 表示するリスト
		List<ViewHistories> recommends;
		
		if (latest != null) {
			
			// 取得した商品情報のカテゴリIDを取得
			Integer categoryId = latest.getItem().getCategory().getId();
			
			// 他ユーザが閲覧した同カテゴリの商品を新しいものから4件取得
			recommends = recommendRepository.
					findTop4OtherUsersViewHistoriesByCategory(categoryId, userId);
			
		} else {
			
			// 他ユーザが閲覧した商品を新しいものから4件取得
			recommends = recommendRepository.
					findTop4OtherUsersLatestViewHistories(userId);
			
		}
		
		// 取得したリストをリクエストスコープへ格納
		model.addAttribute("recommends", recommends);
		
	}

}

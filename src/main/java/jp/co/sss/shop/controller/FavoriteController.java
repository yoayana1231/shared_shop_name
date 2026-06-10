package jp.co.sss.shop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Favorite;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.FavoriteRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.UserRepository;

/*
 * お気に入り機能のコントローラクラス
 * 作成：吉永
 */

@Controller
public class FavoriteController {
	
	// Favoriteリポジトリ
	@Autowired
	FavoriteRepository favoriteRepository;
	
	// Itemリポジトリ
	@Autowired
	ItemRepository itemRepository;
	
	// Userリポジトリ
	@Autowired
	UserRepository userRepository;
	
	/*
	 * お気に入りの一覧表示
	 */
	@GetMapping("/client/favorite/list")
	public String list(HttpSession session, Model model) {
		
		// ログイン中のユーザーIDを取得
		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		// ユーザーオブジェクトを生成
		User user = new User();
		user.setId(userId);
		
		// ログイン中のユーザIDかつ削除フラグが0のものを検索
		List<Favorite> favoriteItems =
				favoriteRepository.findByUserIdAndDeleteFlag(userId, 0);
		
		// リクエストスコープに格納
		model.addAttribute("favoriteItems", favoriteItems);
				
		return "client/item/favolite_list";
		
	}
	
	/*
	 * お気に入り登録・削除
	 */
	@PostMapping("/client/favorite/update")
	public String update(@RequestParam("itemId") Integer itemId, HttpSession session) {
		
		// ログイン中のユーザーIDを取得
		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		
		Favorite favorite = favoriteRepository.findByItemIdAndUserId(itemId, userId);
		
		if (favorite != null) {
			// 削除フラグを確認
			if (favorite.getDeleteFlag() == 0) {
				// 登録中→削除
				favorite.setDeleteFlag(1);
			} else {
				// 削除済み→再登録(復活)
				favorite.setDeleteFlag(0);
			}
			favoriteRepository.save(favorite);
		} else {
			// 登録処理を行う
			Item item = new Item();
			item.setId(itemId);
			User user = new User();
			user.setId(userId);
			
			Favorite newFavorite = new Favorite();
			newFavorite.setItem(item);
			newFavorite.setUser(user);
			favoriteRepository.save(newFavorite);
		}
		
		// 元居たページに戻す
		return "redirect:/";
		
	}

}

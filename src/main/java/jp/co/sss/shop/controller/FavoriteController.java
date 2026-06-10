package jp.co.sss.shop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Favorite;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.FavoriteRepository;

/*
 * お気に入り機能のコントローラクラス
 */

@Controller
public class FavoriteController {
	
	//お気に入りリポジトリ
	@Autowired
	FavoriteRepository favoriteRepository;
	
	/*
	 * お気に入りの一覧表示
	 */
	@GetMapping("/client/favorite/list")
	public String list(HttpSession session, Model model) {
		
		//ログイン中のユーザーIDを取得
		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		//ユーザーオブジェクトを生成
		User user = new User();
		user.setId(userId);
		List<Favorite> favoriteItems = favoriteRepository.findByUser(user);
		
		//お気に入りテーブルから新しい順で一覧を取得
		//リクエストスコープに格納
		model.addAttribute("favoriteItems", favoriteItems);
		
		//model.addAttribute("favoriteItems", favoriteRepository.findAll());
		
		return "client/item/favolite_list";
		
	}

}

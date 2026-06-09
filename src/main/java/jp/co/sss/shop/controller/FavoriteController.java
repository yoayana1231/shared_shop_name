package jp.co.sss.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jp.co.sss.shop.repository.FavoriteRepository;

/*
 * お気に入り機能のコントローラクラス
 */

@Controller
public class FavoriteController {
	
	//お気に入りリポジトリ
	FavoriteRepository favoriteRepository;
	
	/*
	 * お気に入りの一覧表示
	 */
	@GetMapping("/client/favorite/list")
	public String list(Model model) {
		
		//お気に入りテーブルから新しい順で一覧を取得
		//リクエストスコープに格納
		model.addAttribute("favoriteItems", favoriteRepository.findAll());
		
		//お気に入りリスト.htmlに飛ばす
		return "client/item/favolite_list";
		
	}

}

package jp.co.sss.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * お気に入り機能のコントローラクラス
 */

@Controller
public class FavoriteController {
	
	/*
	 * お気に入りの一覧表示
	 */
	@GetMapping("/favorite/list")
	public String list(Model model) {
		
		//
		
		//お気に入りリスト.htmlに飛ばす
		return "favorite/list";
		
	}

}

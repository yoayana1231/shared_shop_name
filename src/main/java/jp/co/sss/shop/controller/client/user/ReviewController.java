package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.UserRepository;

/*
 * レビュー機能のコントローラクラス
 * 作成:吉永
 */

@Controller
public class ReviewController {
	
	// Itemリポジトリ
	@Autowired
	ItemRepository itemRepository;
	
	// Userリポジトリ
	@Autowired
	UserRepository userRepository;
	
	/*
	 * レビュー入力画面に遷移
	 */

}

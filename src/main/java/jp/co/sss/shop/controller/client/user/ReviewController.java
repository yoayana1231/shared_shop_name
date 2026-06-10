package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Reviews;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.ReviewForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.ReviewsRepository;
import jp.co.sss.shop.repository.UserRepository;

/*
 * レビュー機能のコントローラクラス
 * 作成:吉永
 */

@Controller
public class ReviewController {
	
	// Reviewリポジトリ
	@Autowired
	ReviewsRepository reviewRepository;
	
	// Itemリポジトリ
	@Autowired
	ItemRepository itemRepository;
	
	// Userリポジトリ
	@Autowired
	UserRepository userRepository;
	
	/*
	 * レビュー入力画面に遷移
	 */
	@GetMapping("/client/review/input/{id}")
	public String review(@PathVariable Integer itemId,
			@ModelAttribute ReviewForm form, HttpSession session) {
		
		// セッションに商品IDを保存しておく
		session.setAttribute("itemId", itemId);
		
		return "client/item/review_input";
		
	}
	
	/*
	 * レビュー入力状態チェック
	 */
	@PostMapping("/client/review/check")
	public String check(@Valid @ModelAttribute ReviewForm form,
			BindingResult result, Model model) {
		
		// 入力画面にエラーがないかチェック
		if (result.hasErrors()) {
			// エラーあり→入力画面に戻す
			return "client/item/review_input";
		} else {
			// エラーなし→formに保存して確認画面へ	
			model.addAttribute(form);
			return "client/ritem/review_check";
		}
		
	}
	
	/*
	 * レビュー確認画面 登録処理
	 */
	@PostMapping("/client/review/check/save")
	public String save(@ModelAttribute ReviewForm form, 
			HttpSession session, Model model) {
		
		//Reviewオブジェクトを生成
		Reviews review = new Reviews();
		BeanUtils.copyProperties(form, review);
		
		// Userオブジェクトを生成
		User user = new User();
		// ログイン中のユーザーIDを取得
		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		user.setId(userId);
		
		// Itemオブジェクトを生成
		Item item = new Item();
		// セッションに保存した商品IDを取得
		Integer itemId = (Integer) session.getAttribute("itemId");
		item.setId(itemId);
		
		//ReviewエンティティにItemとUserをセット
		review.setUser(user);
		review.setItem(item);
		
		//入力内容をDBに登録
		review = reviewRepository.save(review);
		// 入力完了したらセッションの商品IDを削除
		session.removeAttribute("itemId");
		
		return "client/item/detail";
		
	}
	
	/*
	 * レビュー確認画面 戻る処理
	 */
	@PostMapping("/client/review/check/back")
	public String back(@ModelAttribute ReviewForm form, Model model) {
		
		model.addAttribute("form", form);
		return "client/item/review_input";
		
	}
	
	/*
	 * レビュー削除処理
	 */
	@PostMapping("/client/review/delete")
	public String delete(HttpSession session) {
		
		// ログイン中のユーザーIDを取得
		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		// セッションに保存した商品IDを取得
		Integer itemId = (Integer) session.getAttribute("itemId");
		
		Reviews review =
			reviewRepository.findByItemIdAndUserIdAndDeleteFlag(itemId, userId, 0);
		
		if (review != null) {
			// 表示中→削除フラグを1にする
			review.setDeleteFlag(1);
		}
		
		//DBに変更を保存
		reviewRepository.save(review);
		// 入力完了したらセッションの商品IDを削除
		session.removeAttribute("itemId");
		
		return "redirect:/client/item/detail";
	}

}

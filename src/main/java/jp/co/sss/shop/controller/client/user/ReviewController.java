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
import org.springframework.web.bind.annotation.RequestParam;

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
import jp.co.sss.shop.util.Constant;

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
	 * レビュー入力画面に遷移（新規登録）
	 */
	@GetMapping("/client/review/input/{itemId}")
	public String reviewNew(@PathVariable Integer itemId,
			@ModelAttribute ReviewForm form, Model model) {
		
		// 受け取った商品IDから商品を検索
		Item item = itemRepository.findByIdAndDeleteFlag(itemId, Constant.NOT_DELETED);
		
		// 検索した商品をFormに保存
		form.setItem(item);
		
		model.addAttribute("reviewForm", form);
		
		return "client/item/review_input";
		
	}
	
	/* 
	 * レビュー入力画面に遷移（編集）
	 */
	@GetMapping("/client/review/edit/input/{reviewId}")
	public String reviewEdit(@PathVariable Integer reviewId,
			@ModelAttribute ReviewForm form, HttpSession session, Model model) {
		
		// Userオブジェクトを生成
		User user = new User();
		// ログイン中のユーザーIDを取得
		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		user.setId(userId);
		
		// DBから編集するデータを取得する
		Reviews editReview = reviewRepository.findByIdAndDeleteFlag(reviewId, 0);
		
		if (user == null || user.getId() != editReview.getUser().getId()) {
			// ログイン中ではない時 または 
			// ログインユーザと編集対象のレビューの入力ユーザが一致しない時
			// エラーメッセージをリクエストスコープに保存し、商品詳細へ
			session.setAttribute("error", "レビューが見つからないか、アクセス権限がありません。"
					+ "商品ページへ移動しました。");
			return "redirect:/client/item/detail/" + editReview.getItem().getId();
		} else if (editReview != null) {
			// データをFormにコピー
			BeanUtils.copyProperties(editReview, form);
			Item item = itemRepository.findByIdAndDeleteFlag(editReview.getItem().getId(), Constant.NOT_DELETED);
			form.setItem(item);
		}
		
		model.addAttribute("reviewForm", form);
		
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
			// エラーあり
			// 受け取ったFormの商品IDから商品を検索
			Item item = itemRepository.findByIdAndDeleteFlag(form.getItem().getId(), Constant.NOT_DELETED);
			// Item情報をFormに保存
			form.setItem(item);
			// リクエストスコープに保存してもう一度入力画面へ
			model.addAttribute("reviewForm", form);
			return "client/item/review_input";
		} else {
			// エラーなし
			// 受け取ったFormの商品IDから商品を検索
			Item item = itemRepository.findByIdAndDeleteFlag(form.getItem().getId(), Constant.NOT_DELETED);
			// Item情報をFormに保存
			form.setItem(item);
			// リクエストスコープに保存して確認画面へ
			model.addAttribute("reviewForm", form);
			return "client/item/review_check";
		}
		
	}
	
	/*
	 * レビュー確認画面 登録処理
	 */
	@PostMapping("/client/review/check/save")
	public String save(@ModelAttribute ReviewForm form, 
			HttpSession session, Model model) {
		
		Reviews review;
		
		if (form.getId() == null) {
			review = new Reviews();
			BeanUtils.copyProperties(form, review);
		} else {
			review = reviewRepository.findByIdAndDeleteFlag(form.getId(), Constant.NOT_DELETED);
			review.setRating(form.getRating());
			review.setComments(form.getComments());
		}
				
		// Userオブジェクトを生成
		User user = new User();
		// ログイン中のユーザーIDを取得
		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		user.setId(userId);
		
		// 商品IDをセット（リダイレクト用）
		Integer itemId = review.getItem().getId();
		
		//ReviewエンティティにUserとItemをセット
		review.setUser(user);
		
		//入力内容をDBに登録
		review = reviewRepository.save(review);
		
		//戻すページURLを保存
		String returnURL = "client/item/detail/" + itemId;
		
		return "redirect:/" + returnURL;
		
	}
	
	/*
	 * レビュー確認画面 戻る処理
	 */
	@PostMapping("/client/review/check/back")
	public String back(@ModelAttribute ReviewForm form, Model model) {
		
		// 受け取ったFormの商品IDから商品を検索
		Item item = itemRepository.findByIdAndDeleteFlag(form.getItem().getId(), Constant.NOT_DELETED);
		// Item情報をFormに保存
		form.setItem(item);
		// リクエストスコープに保存
		model.addAttribute("reviewForm", form);
		return "client/item/review_input";
		
	}
	
	/*
	 * レビュー削除処理
	 */
	@PostMapping("/client/review/delete")
	public String delete(@RequestParam("reviewId") Integer reviewId,
			HttpSession session) {
		
		Reviews review = 
				reviewRepository.findByIdAndDeleteFlag(reviewId, Constant.NOT_DELETED);
		
		// 商品IDを保存（リダイレクト用）
		Integer itemId = review.getItem().getId();
		
		if (review != null) {
			// 表示中→削除フラグを1にする
			review.setDeleteFlag(Constant.DELETED);
			//DBに変更を保存
			reviewRepository.save(review);
		}
		
		return "redirect:/client/item/detail/" + itemId;

	}

}

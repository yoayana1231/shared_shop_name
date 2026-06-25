package jp.co.sss.shop.controller.client.user;

import java.sql.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

@Controller
public class ClientUserUpdateController {

	// Userリポジトリ
	@Autowired
	UserRepository userRepository;

	// セッション
	@Autowired
	HttpSession session;

	/*
	 * 入力画面 初期表示処理
	 */
	@GetMapping("/client/user/update/input/init")
	public String updateInit(UserForm userForm) {
		
		// セッションからログイン中のユーザIDを取得
		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		// 変更対象の情報取得
		User user = userRepository.findByIdAndDeleteFlag(userId, Constant.NOT_DELETED);
		// 変更対象の情報をUserFormにコピー
		BeanUtils.copyProperties(user, userForm);
		
		// 入力フォーム情報をセッションに保存
		session.setAttribute("userForm",userForm);
		return "redirect:/client/user/update/input";
		
	}
	
	
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String updateGoInput(Model model) {
		
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		
		// 入力フォーム情報をリクエストスコープに保存
		model.addAttribute("userForm", userForm);
			
		return"redirect:/client/user/update/input";

	}

	/*
	 * 変更入力画面 表示処理
	 */
	@GetMapping("/client/user/update/input")
	public String updateInput(Model model) {
		
		// セッションから入力フォーム取得
		UserForm userForm = (UserForm)session.getAttribute("userForm");
		
		// 入力フォーム情報をリクエストスコープに保存
		model.addAttribute("userForm", userForm);
		
		BindingResult result = (BindingResult) session.getAttribute("errors");
		if (result != null) {
			// エラー情報あり→エラー情報を設定
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			session.removeAttribute("errors");
		}
		
		return "client/user/update_input";
		
	}

	/*
	 * 変更 エラーチェック
	 */
	@PostMapping("/client/user/update/check")
	public String updateInputCheck(@Valid @ModelAttribute UserForm form,
			BindingResult result) {
		
		// セッションから入力フォームの情報取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		// 入力情報に不足がある場合
		if (form.getAuthority() == null) {
			// セッション情報から値をセット
			form.setAuthority(userForm.getAuthority());
		}
		
		// 入力フォーム情報をセッションに保持
		session.setAttribute("userForm", form);
		
		// エラーチェック
		if (result.hasErrors()) {
			// エラーあり
			// 入力エラー情報をセッションに保存してリダイレクト
			session.setAttribute("errors", result);
			return "redirect:/client/user/update/input";
		} else {
			// エラーなし 確認画面表示処理へ
			return "redirect:/client/user/update/check";
		}
		
	}

	/*
	 * 変更確認画面 表示処理
	 */
	@GetMapping("/client/user/update/check")
	public String updateCheck(Model model) {
		
		// セッションから入力フォームの情報取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");

		// 入力情報をリクエストスコープに保存
		model.addAttribute("userForm", userForm);
		
		//確認画面へ
		return "client/user/update_check";
		
	}

	/*
	 * 変更処理
	 */
	@PostMapping("/client/user/update/complete")
	public String updateComplete() {
		
		// セッション情報から入力値を再取得
		UserForm userForm = (UserForm)session.getAttribute("userForm");
		
		// 変更対象情報を取得
		User user = userRepository.findByIdAndDeleteFlag
				(userForm.getId(), Constant.NOT_DELETED);

		Integer deleteFlg = user.getDeleteFlag();
		Date insertDate = user.getInsertDate();
		
		// 入力フォーム情報を変更用エンティティに設定
		BeanUtils.copyProperties(userForm, user);

		// 入力値以外の項目を設定
		user.setDeleteFlag(deleteFlg);
		user.setInsertDate(insertDate);

		// 情報を保存
		userRepository.save(user);

		// セッションの保存ユーザ情報を更新
		UserBean loginUser = (UserBean)session.getAttribute("user") ; 
		if (loginUser.getId() == userForm.getId()) {
			loginUser.setName(userForm.getName()) ;
		}
		session.setAttribute("user", loginUser) ;
		
		//セッション情報の削除
		session.removeAttribute("userForm");
		
		return "redirect:/client/user/update/complete";
		
	}

	/*
	 * 変更完了画面 表示処理
	 */
	@GetMapping("/client/user/update/complete")
	public String updateCompleteFinish() {
		
		return "client/user/update_complete";
		
	}

}

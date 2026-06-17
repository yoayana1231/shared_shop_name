package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

@Controller
public class ClientUserDeleteController {
	
	// Userリポジトリ
	@Autowired
	UserRepository userRepository;

	// セッション
	@Autowired
	HttpSession session;
	
	/*
	 * 削除確認処理
	 */
	@PostMapping("/client/user/delete/check")
	public String deleteCheck() {
		
		// セッションからログイン中のユーザIDを取得
		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		
		User user = userRepository.findByIdAndDeleteFlag(userId, Constant.NOT_DELETED);
		
		// 初期表示用フォーム情報の生成
		UserForm userForm = new UserForm();
		// 削除対象の情報をUserFormにコピー
		BeanUtils.copyProperties(user, userForm);
		
		// 取得情報をセッションに保持
		session.setAttribute("userForm", userForm);
		
		return "redirect:/client/user/delete/check";
		
	}
	
	/*
	 * 確認画面 表示処理
	 */
	@GetMapping("/client/user/delete/check")
	public String updateInput(Model model) {
		
		// セッションから情報取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		
		// リクエストスコープに保存
		model.addAttribute("userForm", userForm);
		
		return "client/user/delete_check";
		
	}
	
	/*
	 * 削除処理
	 */
	@PostMapping("/client/user/delete/complete")
	public String deleteComplete() {
		
		// セッションから削除情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		// 削除対象情報を取得
		User user = 
				userRepository.findByIdAndDeleteFlag
				(userForm.getId(), Constant.NOT_DELETED);
		
		// 削除フラグを立てる
		user.setDeleteFlag(Constant.DELETED);
		// 会員情報を保存
		userRepository.save(user);
		
		// セッションの情報を削除
		session.removeAttribute("user");
		session.removeAttribute("userForm");
		
		return "redirect:/client/user/delete/complete";
		
	}
	
	/*
	 * 削除完了画面 表示処理
	 */
	@GetMapping("/client/user/delete/complete")
	public String deleteCompleteFinish() {
		
		return "client/user/delete_complete";
		
	}

}

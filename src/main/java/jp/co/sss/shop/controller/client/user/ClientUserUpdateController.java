package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserUpdateController {
	
	// Userリポジトリ
	@Autowired
	UserRepository userRepository;
	
	/*
	 * 
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String updateInput(UserForm userForm, HttpSession session) {
		
		// ログイン中のユーザーIDを取得
		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		// テーブルから変更対象にユーザー情報をDBから取得
		User user = userRepository.getReferenceById(userId);
		
		// 入力画面初期表示用のフォーム情報を新規生成
		BeanUtils.copyProperties(user, userForm);
		// セッションスコープに保存
		session.setAttribute("userForm", userForm);
		
		return "redirect:/client/user/update/input";
	}
}

package jp.co.sss.shop.controller.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.LoginForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

/**
 * ログイン機能のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class LoginController {

	//test
	//吉田ばーか

	/**
	 * 会員情報
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * セッション情報
	 */
	@Autowired
	HttpSession session;

	/**
	 * ログイン処理
	 *
	 * @param form ログインフォーム
	 * @return "login" ログイン画面表示
	 */
	@RequestMapping(path = "/login", method = RequestMethod.GET)
	public String login(@ModelAttribute LoginForm form) {

		// セッション情報を無効にする
		session.invalidate();

		return "login";
	}

	/**
	 * ログイン処理
	 *
	 * @param form ログインフォーム
	 * @param result 入力チェック結果
	 * @return
			一般会員の場合 "redirect:/" トップ画面表示処理
			運用管理者、システム管理者の場合 "redirect:/adminmenu"管理者メニュー表示処理
	 */
	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public String doLogin(@Valid @ModelAttribute LoginForm form, BindingResult result) {

		String returnStr = "login";
		if (result.hasErrors()) {
			// 入力値に誤りがあった場合
			// セッション情報を無効にして、ログイン画面再表示
			session.invalidate();
			returnStr = "login";

		}

		// メールアドレスからユーザー情報を取得
		User user = userRepository.findByEmail(form.getEmail());
		
		// 取得したユーザー情報と入力されたパスワードが正しいか判定
		if ((user == null) || (!user.getPassword().equals(form.getPassword()))) {
			// 一致しなかった場合
			// セッション情報を無効にして、ログイン画面再表示
			session.invalidate();
			returnStr = "login";
		} else {
			// 一致した場合
			// セッションに情報をセット
			UserBean userBean = new UserBean();
			userBean.setId(user.getId());
			userBean.setName(user.getName());
			userBean.setAuthority(user.getAuthority());
			
			session.setAttribute("user", userBean);
			
			// 権限による分岐
			if (user.getAuthority() == Constant.AUTH_CLIENT) {
				returnStr = "redirect:/";
			} else {
				returnStr = "redirect:/admin/menu";
			}
		}

		return returnStr;

	}

	/**
	 * 管理者メニュー表示処理
	 *
	 * @return "admin/menu" 管理者メニュー画面表示
	 */
	@RequestMapping(path = "/admin/menu", method = RequestMethod.GET)
	public String showAdminMenu() {

		// 管理者用メニュー画面表示
		return "admin/admin_menu";
	}

}

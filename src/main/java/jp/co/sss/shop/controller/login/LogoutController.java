package jp.co.sss.shop.controller.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

/**
 * ログアウト機能のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class LogoutController {

	//吉田
	
	/**
	 * ログアウト処理
	 *
	 * @param session セッション情報
	 * @return "redirect:/" トップ画面へ
	 */
	@RequestMapping(path = "/logout")
	public String logout(HttpSession session) {
		// セッション情報を無効にする
		session.invalidate();
		return "redirect:/";
	}
}

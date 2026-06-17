package jp.co.sss.shop.controller.client.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.form.UserForm;

/**
 * 会員退会画面のデザイン確認用コントローラー。
 *
 * DB更新やログアウト処理には接続せず、固定データで退会確認・完了画面を確認する。
 */
@Controller
public class ClientUserDeleteDesignController {

	private static final String DESIGN_USER_FORM = "designUserForm";

	/**
	 * 退会確認画面 表示前処理（デザイン確認用）
	 */
	@RequestMapping(path = "/design/client/user/delete/check", method = RequestMethod.POST)
	public String deleteCheckPost(HttpSession session) {
		prepareUserForm(session);
		return "redirect:/design/client/user/delete/check";
	}

	/**
	 * 退会確認画面 表示処理（デザイン確認用）
	 */
	@RequestMapping(path = "/design/client/user/delete/check", method = RequestMethod.GET)
	public String deleteCheck(Model model, HttpSession session) {
		UserForm userForm = prepareUserForm(session);
		model.addAttribute("userForm", userForm);
		model.addAttribute("designMode", true);
		return "client/user/delete_check";
	}

	/**
	 * 退会完了画面 表示前処理（デザイン確認用）
	 */
	@RequestMapping(path = "/design/client/user/delete/complete", method = RequestMethod.POST)
	public String deleteComplete() {
		return "redirect:/design/client/user/delete/complete";
	}

	/**
	 * 退会完了画面 表示処理（デザイン確認用）
	 */
	@RequestMapping(path = "/design/client/user/delete/complete", method = RequestMethod.GET)
	public String deleteComplete(Model model) {
		model.addAttribute("designMode", true);
		return "client/user/delete_complete";
	}

	private UserForm prepareUserForm(HttpSession session) {
		UserForm userForm = (UserForm) session.getAttribute(DESIGN_USER_FORM);
		if (userForm == null) {
			userForm = createSampleUserForm();
			session.setAttribute(DESIGN_USER_FORM, userForm);
		}
		return userForm;
	}

	private UserForm createSampleUserForm() {
		UserForm userForm = new UserForm();
		userForm.setId(1);
		userForm.setEmail("takuya@example.com");
		userForm.setPassword("password123");
		userForm.setName("佐藤 拓弥");
		userForm.setPostalCode("1500001");
		userForm.setAddress("東京都渋谷区神宮前1-1-1 Cry Baby Residence 101");
		userForm.setPhoneNumber("09012345678");
		userForm.setAuthority(2);
		return userForm;
	}
}

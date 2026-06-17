package jp.co.sss.shop.controller.client.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.form.UserForm;

/**
 * 会員情報変更画面のデザイン確認用コントローラー。
 *
 * DB更新や本番の会員変更処理には接続せず、固定データで入力・確認・完了画面を確認する。
 */
@Controller
public class ClientUserUpdateDesignController {

	private static final String DESIGN_USER_FORM = "designUserForm";

	/**
	 * 変更入力画面 表示処理（デザイン確認用）
	 */
	@RequestMapping(path = "/design/client/user/update/input", method = RequestMethod.GET)
	public String updateInput(Model model, HttpSession session) {
		UserForm userForm = (UserForm) session.getAttribute(DESIGN_USER_FORM);
		if (userForm == null) {
			userForm = createSampleUserForm();
			session.setAttribute(DESIGN_USER_FORM, userForm);
		}

		setDesignModel(model, userForm);
		return "client/user/update_input";
	}

	/**
	 * 変更確認画面 表示前処理（デザイン確認用）
	 */
	@RequestMapping(path = "/design/client/user/update/check", method = RequestMethod.POST)
	public String updateCheck(@ModelAttribute UserForm userForm, HttpSession session) {
		session.setAttribute(DESIGN_USER_FORM, userForm);
		return "redirect:/design/client/user/update/check";
	}

	/**
	 * 変更確認画面 表示処理（デザイン確認用）
	 */
	@RequestMapping(path = "/design/client/user/update/check", method = RequestMethod.GET)
	public String updateCheck(Model model, HttpSession session) {
		UserForm userForm = (UserForm) session.getAttribute(DESIGN_USER_FORM);
		if (userForm == null) {
			userForm = createSampleUserForm();
			session.setAttribute(DESIGN_USER_FORM, userForm);
		}

		setDesignModel(model, userForm);
		return "client/user/update_check";
	}

	/**
	 * 変更完了画面 表示前処理（デザイン確認用）
	 */
	@RequestMapping(path = "/design/client/user/update/complete", method = RequestMethod.POST)
	public String updateComplete() {
		return "redirect:/design/client/user/update/complete";
	}

	/**
	 * 変更完了画面 表示処理（デザイン確認用）
	 */
	@RequestMapping(path = "/design/client/user/update/complete", method = RequestMethod.GET)
	public String updateComplete(Model model) {
		model.addAttribute("designMode", true);
		return "client/user/update_complete";
	}

	private void setDesignModel(Model model, UserForm userForm) {
		model.addAttribute("userForm", userForm);
		model.addAttribute("designMode", true);
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

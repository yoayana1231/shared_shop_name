package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserRegistController {
	@Autowired
	UserRepository userRepository;

	// 新規会員登録リンク クリック時
	@RequestMapping(path = "/client/user/regist/input/init", method = RequestMethod.GET)
	public String registInput(UserForm userForm, HttpSession session) {
		// 入力フォーム情報をセッションスコープに保存

		session.setAttribute("userForm", userForm);
		return "redirect:/client/user/regist/input";
	}

	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.POST)
	public String registResist(UserForm userForm, HttpSession session) {

		UserForm user = (UserForm) session.getAttribute("userForm");

		// セッションスコープに入力フォーム情報があるかを確認、なければ下記の処理を実施
		if (user == null) {
			session.setAttribute("userForm", userForm);
		}

		return "redirect:/client/user/regist/input";
	}

	@RequestMapping(path = "/client/user/regist/input", method = RequestMethod.GET)
	public String goInput(UserForm userForm, HttpSession session, Model model) {

		//セッションスコープから入力フォーム情報を取得

		UserForm user = (UserForm) session.getAttribute("userForm");
		// エラーをセッションスコープから取り出す
		BindingResult result = (BindingResult) session.getAttribute("errors");

		//セッションスコープに入力エラー情報がある場合
		if (result != null) {
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			session.removeAttribute("errors");
		}
		//入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", user);

		return "client/user/regist_input";
	}

	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.POST)
	public String inputCheck(@Valid @ModelAttribute UserForm userForm, BindingResult result, HttpSession session) {

		session.setAttribute("userForm", userForm);

		if (result.hasErrors()) {

			session.setAttribute("errors", result);

			return "redirect:/client/user/regist/input";
		}

		return "redirect:/client/user/regist/check";
	}

	@RequestMapping(path = "/client/user/regist/check", method = RequestMethod.GET)
	public String goCheck(UserForm userForm, HttpSession session, Model model) {

		UserForm user = (UserForm) session.getAttribute("userForm");

		model.addAttribute("userForm", user);

		return "client/user/regist_check";
	}

	@RequestMapping(path = "/client/user/regist/complete", method = RequestMethod.POST)
	public String resistComplete(UserForm userForm, HttpSession session, Model model) {
		//セッションスコープからフォーム情報を取得
		UserForm user = (UserForm) session.getAttribute("userForm");
		System.out.println(user.getName());
		//入力フォーム情報を元にDB登録用エンティティオブジェクトを生成
		User resist = new User();
		BeanUtils.copyProperties(user, resist, "id");
		//ユーザー情報をDBに保存
		resist = userRepository.save(resist);
		//セッションスコープの入力情報削除
		session.removeAttribute("userForm");
		//未ログインでの会員登録の場合
		
		if (user.getAuthority() == 2) {
			//セッションスコープに会員情報をセットし、ログイン状態にする
			UserBean beanResist = new UserBean();
			BeanUtils.copyProperties(resist, beanResist);
			session.setAttribute("user", beanResist);

		}

		return "redirect:/client/user/regist/complete";
	}

	@RequestMapping(path = "/client/user/regist/complete", method = RequestMethod.GET)
	public String goComplete() {
		// 登録完了表示
		return "client/user/regist_complete";
	}

}

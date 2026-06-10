package jp.co.sss.shop.controller.client.user;

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
	@Autowired
	UserRepository userRepository;
	
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String updateInput(UserForm userForm, HttpSession session) {
		// 入力フォーム情報をセッションスコープに保存
		
		UserForm user = (UserForm) session.getAttribute("userForm");
		if(user == null) {
			UserBean userBean = (UserBean) session.getAttribute("user");
			User userUpdate = new User();
			userUpdate = userRepository.getReferenceById(userBean.getId());
			session.setAttribute("userForm", userUpdate);
			
		}

		return "redirect:/client/user/regist/input";
	}
}

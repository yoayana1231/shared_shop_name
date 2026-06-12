package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserShowController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	HttpSession session;

	@RequestMapping(path = "/client/user/detail", method = RequestMethod.GET)
	public String registInput(Integer Id, Model model) {

		UserBean userBean = (UserBean) session.getAttribute("user");
		User user = new User();

		user = userRepository.getReferenceById(userBean.getId());

		BeanUtils.copyProperties(user, userBean);

		model.addAttribute("userBean", userBean);
		return "client/user/detail";
	}

}

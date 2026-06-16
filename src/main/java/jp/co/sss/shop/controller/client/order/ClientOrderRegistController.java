package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

@Controller
public class ClientOrderRegistController {

	@Autowired
	HttpSession session;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderItemRepository orderItemRepository;

	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String inputOrder() {
		UserBean userBean = (UserBean) session.getAttribute("user");
		User user = userRepository.findByIdAndDeleteFlag(userBean.getId(), Constant.NOT_DELETED);

		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		if (basketBeans == null || basketBeans.isEmpty()) {
			return "redirect:/client/basket/list";
		}

		// 買い物かごの商品数が在庫数に上回ると、お届け先に遷移させない
		for (BasketBean basketBean : basketBeans) {
			Item item = itemRepository.findByIdAndDeleteFlag(basketBean.getId(), Constant.NOT_DELETED);
			if (item == null || item.getStock() == 0 || basketBean.getOrderNum() > item.getStock()) {
				return "redirect:/client/basket/list";
			}

			basketBean.setPrice(item.getPrice());
			basketBean.setImage(item.getImage());
		}

		// オーダー入力フォーム
		OrderForm orderForm = new OrderForm();
		// userテーブルの情報をorderFormにコピー
		BeanUtils.copyProperties(user, orderForm);
		// クレジットカードを初期値にする
		orderForm.setPayMethod(1);

		session.setAttribute("orderForm", orderForm);
		session.setAttribute("basket", basketBeans);

		return "redirect:/client/order/address/input";
	}

	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.GET)
	public String showAddressInput(HttpSession session, Model model) {
		OrderForm form = (OrderForm) session.getAttribute("orderForm");

		BindingResult bindingResult = (BindingResult) session.getAttribute("result");
		// 入力チェックエラー表示
		if (bindingResult != null) {
			model.addAttribute("org.springframework.validation.BindingResult.orderForm", bindingResult);
			// 入力チェック結果削除
			session.removeAttribute("result");
		}

		model.addAttribute("orderForm", form);

		return "client/order/address_input";

	}

	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String stepToPayment(@Valid @ModelAttribute("orderForm") OrderForm orderForm, BindingResult bindingResult,
			HttpSession session) {
		session.setAttribute("orderForm", orderForm);
		if (bindingResult.hasErrors()) {
			session.setAttribute("result", bindingResult);

			session.setAttribute("orderForm", orderForm);

			return "redirect:/client/order/address/input";
		}
		return "redirect:/client/order/payment/input";

	}

	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.GET)
	public String showPaymentInput(HttpSession session, Model model) {

		return "client/order/payment_input";
	}

	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String stepToCheck(Integer payMethod, HttpSession session) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");

		if (orderForm != null) {
			// ラジオボタンで選ばれた支払方法（name="payMethod" の値）をセット
			orderForm.setPayMethod(payMethod);
			session.setAttribute("orderForm", orderForm);
		}
		return "redirect:/client/order/check";
	}

	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String backToAddressInput() {
		return "redirect:/client/order/address/input";
	}

	@RequestMapping(path = "/client/order/check", method = RequestMethod.GET)
	public String showOrderCheck(HttpSession session, Model model) {
		// ・セッションスコープから注文情報を取得
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		// ・セッションスコープから買い物かご情報を取得
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");

		// ガード処理（セッションが空の場合はバスケット画面へリダイレクト）
		if (orderForm == null || basketBeans == null || basketBeans.isEmpty()) {
			return "redirect:/client/basket/list";
		}

		// 在庫状況を反映した商品を詰め替えるための新しいリスト
		List<BasketBean> newBaskets = new ArrayList<BasketBean>();

		// 買い物かごコントローラーの仕様に合わせたエラー商品名格納用リスト
		List<String> itemNameListZero = new ArrayList<String>();
		List<String> itemNameListLessThan = new ArrayList<String>();

		// ・注文商品の最新情報をDB から取得し、商品の在庫チェックを行う
		for (BasketBean basketItem : basketBeans) {
			Item item = itemRepository.findByIdAndDeleteFlag(basketItem.getId(), Constant.NOT_DELETED);

			if (item == null || item.getStock() == 0) {
				// - 在庫切れの商品は、買い物かごから削除（＝新しいリストに追加しない）
				itemNameListZero.add(basketItem.getName());
			} else {
				if (item.getStock() < basketItem.getOrderNum()) {
					// - 在庫数にあわせて、買い物かご情報を更新（注文数、在庫数）
					basketItem.setOrderNum(item.getStock());
					itemNameListLessThan.add(basketItem.getName());
				}
				// 最新の在庫数をセット
				basketItem.setStock(item.getStock());
				// 在庫がある、または数量調整された商品を新しいリストに保存
				newBaskets.add(basketItem);
			}
		}

		// ・在庫不足、在庫切れ商品がある場合：注文警告メッセージやリストをリクエストスコープに保存
		if (!itemNameListZero.isEmpty() || !itemNameListLessThan.isEmpty()) {
			if (!itemNameListZero.isEmpty()) {
				model.addAttribute("itemNameListZero", itemNameListZero);
			}
			if (!itemNameListLessThan.isEmpty()) {
				model.addAttribute("itemNameListLessThan", itemNameListLessThan);
			}

			// 調整の結果、かごの中身が完全に空になってしまった場合
			if (newBaskets.isEmpty()) {
				session.removeAttribute("basketBeans");
				return "redirect:/client/basket/list";
			}
		}

		// ・在庫状況を反映した買い物かご情報をセッションに保存
		session.setAttribute("basketBeans", newBaskets);

		// ・買い物かご情報から、商品ごとの金額小計を算出し、注文商品情報リストに保存
		// ・注文商品情報リストから合計金額を算出する
		List<OrderItemBean> orderItemBeans = new ArrayList<OrderItemBean>();
		int total = 0;

		for (BasketBean basketItem : newBaskets) {
			Item item = itemRepository.findByIdAndDeleteFlag(basketItem.getId(), Constant.NOT_DELETED);
			if (item != null) {
				//  OrderItemBean を生成してデータを詰める
				OrderItemBean orderItemBean = new OrderItemBean();
				orderItemBean.setId(item.getId());
				orderItemBean.setName(item.getName());
				orderItemBean.setImage(item.getImage());
				orderItemBean.setPrice(item.getPrice());
				orderItemBean.setOrderNum(basketItem.getOrderNum());

				// 商品ごとの金額小計を算出（単価 × 注文数）
				int subtotal = item.getPrice() * basketItem.getOrderNum();
				orderItemBean.setSubtotal(subtotal);

				// 注文商品情報リストに保存
				orderItemBeans.add(orderItemBean);

				// 合計金額の算出に加算
				total += subtotal;
			}
		}

		model.addAttribute("total", total);
		model.addAttribute("orderItemBeans", orderItemBeans);
		model.addAttribute("orderForm", orderForm);

		// ・注文確認画面表示 フォワード: "client/order/check"
		return "client/order/check";
	}

	//入力画面で戻るを押した時の処理
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	@Transactional
	public String showOrderComplete(HttpSession session) {

		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		List<BasketBean> basketBeans = (List<BasketBean>) session.getAttribute("basketBeans");
		List<OrderItem> orderItem = (List<OrderItem>) session.getAttribute("basketBeans");
		UserBean userBean = (UserBean) session.getAttribute("user");

		if (orderForm == null || basketBeans == null || userBean == null) {
			return "redirect:/client/basket/list";
		}

		// 1. 在庫の再チェック（購入直前の確認）
		for (BasketBean basketItem : basketBeans) {
			Item item = itemRepository.findByIdAndDeleteFlag(basketItem.getId(), Constant.NOT_DELETED);
			if (item == null || item.getStock() < basketItem.getOrderNum()) {
				// 在庫が足りない場合はエラー扱い（本来はエラー画面へ遷移）
				return "redirect:/client/basket/list?error=stock";
			}
		}

		User user = userRepository.findById(userBean.getId()).orElse(null);
		if (user == null) {
			return "redirect:/client/login";
		}

		// 2. Order保存
		Order order = new Order();
		BeanUtils.copyProperties(orderForm, order);
		order.setId(null);
		order.setUser(user);
		order.setPayMethod(orderForm.getPayMethod());

		orderRepository.save(order);
		orderRepository.flush();

		// 3. 在庫減算とOrderItem保存
		for (BasketBean basketItem : basketBeans) {
			// 在庫減算
			Item item = itemRepository.findByIdAndDeleteFlag(basketItem.getId(), Constant.NOT_DELETED);
			item.setStock(item.getStock() - basketItem.getOrderNum());
			itemRepository.save(item);

			// OrderItem保存
			OrderItem newOrderItem = new OrderItem();
			newOrderItem.setQuantity(basketItem.getOrderNum());
			newOrderItem.setOrder(order);
			newOrderItem.setItem(item);
			newOrderItem.setPrice(item.getPrice());

			orderItemRepository.save(newOrderItem);
		}

		// 4. セッションクリア
		session.removeAttribute("basketBeans");
		session.removeAttribute("orderForm");

		return "redirect:/";
	}

}
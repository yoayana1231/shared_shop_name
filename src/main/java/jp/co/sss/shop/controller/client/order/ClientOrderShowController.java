package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.PriceCalc;

@Controller
public class ClientOrderShowController {
	
	// 注文情報
	@Autowired
	OrderRepository orderRepository;

	// セッション
	@Autowired
	HttpSession session;
	
	// 合計金額計算サービス
	@Autowired
	PriceCalc priceCalc;
	
	// Entity、Form、Bean間のデータ生成、コピーサービス
	@Autowired
	BeanTools beanTools;
	
	
	/*
	 * 注文一覧表示処理
	 * @param model Viewとの値受渡し
	 * @param pageable ページング情報
	 * @return client/order/list 
	 */
	@RequestMapping(path = "/client/order/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String orderList(Model model, HttpSession session, Pageable pageable) {
		
		// ログイン中のユーザーIDを取得
		String userName = ((UserBean) session.getAttribute("user")).getName();
		
		// ログイン中のユーザーの全ての注文情報を取得
		Page<Order> orderList = orderRepository.findByUserNameOrderByInsertDateDescIdDesc(userName, pageable);
		
		// 注文情報リストを生成
		List<OrderBean> orderBeanList = new ArrayList<OrderBean>();
		for (Order order : orderList) {
			// 表示する注文情報を生成
			OrderBean orderBean = beanTools.copyEntityToOrderBean(order);
			// レコードから紐づくOrderItemのListを取り出す
			List<OrderItem> orderItemList = order.getOrderItemsList();
			// 合計金額を算出
			int total = priceCalc.orderItemPriceTotal(orderItemList);
			
			// 合計金額のセット
			orderBean.setTotal(total);
			orderBeanList.add(orderBean);
		}
		
		//注文情報リストをリクエストスコープへ保存
		model.addAttribute("pages", orderList);
		model.addAttribute("orders", orderBeanList);
		
		// セッションにエラーメッセージがあればリクエストスコープへ
		String error = (String) session.getAttribute("error");
		if (error != null) {
			model.addAttribute("error", error);
			session.removeAttribute("error");
		}
		
		return "client/order/list";
		
	}
	
	/*
	 * 注文詳細表示処理
	 * 
	 */
	@GetMapping("/client/order/detail/{id}")
	public String orderDetail(@PathVariable Integer id, Model model) {
		
		// 選択された注文情報を生成
		Order order = orderRepository.getReferenceById(id);
		
		// ログイン中のユーザーIDを取得
		Integer userId = ((UserBean) session.getAttribute("user")).getId();
		
		// URLから取得した注文IDの情報がログイン中のユーザのものか判定
		if (userId != order.getUser().getId()) {
			// ログインユーザのものではない場合、エラー文を出した上で注文一覧へ戻す
			session.setAttribute("error", "ご指定の注文情報が見つからないか、アクセス権限がありません。"
					+ "ログイン中のアカウントの注文一覧へ移動しました。");
			
			return "redirect:/client/order/list";
		}
		
		// 表示する注文情報を生成
		OrderBean orderBean = beanTools.copyEntityToOrderBean(order);
		
		// 注文商品情報を取得
		List<OrderItemBean> orderItemBeanList = beanTools.generateOrderItemBeanList(order.getOrderItemsList());
		
		// 合計金額を算出
		int total = priceCalc.orderItemBeanPriceTotalUseSubtotal(orderItemBeanList);

		// 注文情報をViewへ渡す
		model.addAttribute("orders", orderBean);
		model.addAttribute("orderItem", orderItemBeanList);
		model.addAttribute("total", total);
		
		return "client/order/detail";
		
	}

}

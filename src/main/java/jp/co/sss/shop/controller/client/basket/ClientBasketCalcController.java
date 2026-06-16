package jp.co.sss.shop.controller.client.basket;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.PriceCalc;

@Controller
public class ClientBasketCalcController {
	@Autowired
	ItemRepository itemRepository;

	@Autowired
	HttpSession session;
	// 注文情報
	@Autowired
	OrderRepository orderRepository;
	// 合計金額計算サービス
	@Autowired
	PriceCalc priceCalc;

	// Entity、Form、Bean間のデータ生成、コピーサービス
	@Autowired
	BeanTools beanTools;

	@RequestMapping(path = "/client/basket/add/calc", method = RequestMethod.POST)
	public String addBasketItem(@RequestParam Integer id,Pageable pageable) {

		// 現在のかごの中身を取得
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("basketBeans");

		// かごに入れるボタンを押したときに送られたIDの商品の情報を取得
		Item item = itemRepository.getReferenceById(id);

		// かごをまだ持っていない場合、空のかごを作成
		if (basketList == null) {
			basketList = new ArrayList<BasketBean>();
		}

		// 既にかごに存在するかチェック(true → 存在する, false → 存在しない)
		boolean isExisting = false;

		// かごの中身を1件ずつチェック
		for (BasketBean basketItem : basketList) {

			// かごに入れるボタンを押したときに送られたIDが、チェックしてる商品のIDと一致した場合、注文数を1増やし、isExistingをtrueに
			if (basketItem.getId().equals(id)) {
				basketItem.setOrderNum(basketItem.getOrderNum() + 1);
				isExisting = true;
//				System.out.println(basketItem.getOrderNum());
//				basketItem.setPrice(basketItem.getPrice() * basketItem.getOrderNum());
//				System.out.println(basketItem.getPrice());
				break;
			}
		}

		// チェックし終えた結果、かごにない商品をかごに入れる場合、商品の情報を入れる。
		if (isExisting == false) {
			BasketBean basketItem = new BasketBean();
			basketItem.setId(item.getId());
			basketItem.setName(item.getName());
			basketItem.setOrderNum(1);
			basketItem.setStock(item.getStock());

			basketList.add(basketItem);
			//画像を挿入

		}

		// かごの中身をセッションに保存し、買い物かご画面にリダイレクト

		session.setAttribute("basketBeans", basketList);
		

		return "redirect:/client/basket/list";
	}

	@GetMapping("/client/basket/add/calc/result")
	public String orderList(Model model, Pageable pageable) {

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
		return "redirect:/client/basket/list";

	}

	@RequestMapping(path = "/client/basket/delete/calc", method = RequestMethod.POST)
	public String deleteBasketItem(@RequestParam Integer id) {

		// 現在のかごの中身を取得
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("basketBeans");

		// かごがある場合、かごの中身を1件ずつチェック
		if (basketList != null) {
			for (int i = 0; i < basketList.size(); i++) {

				// 現在チェックしている商品のデータを取得
				BasketBean basketItem = basketList.get(i);

				// 削除ボタンを押したときに送られたIDが、チェックしてる商品のIDと一致した場合の処理
				if (basketItem.getId().equals(id)) {

					// チェックしてる商品の注文数が1の場合、その商品をかごから削除
					if (basketItem.getOrderNum() == 1) {
						basketList.remove(i);

						// チェックしてる商品の注文数が1でない場合、その商品の注文数を1減らす
					} else {
						basketItem.setOrderNum(basketItem.getOrderNum() - 1);
					}
					break;
				}
			}

			// 商品をかごから削除することによって、かごが空になった場合はセッションを破棄
			if (basketList.isEmpty()) {
				session.removeAttribute("basketBeans");

				// 空でない場合、かごの中身をセッションスコープに保存
			} else {
				session.setAttribute("basketBeans", basketList);
			}
		}

		// 買い物かご画面にリダイレクト
		return "redirect:/client/basket/list";
	}
}

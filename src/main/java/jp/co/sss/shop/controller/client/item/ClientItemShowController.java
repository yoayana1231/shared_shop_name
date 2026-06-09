package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ClientItemShowController {
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;
	
	/*
	 * カテゴリ情報
	 */
	@Autowired
	CategoryRepository categoryRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
	@RequestMapping(path = "/" , method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model) {
		model.addAttribute("items", itemRepository.findAll());
		return "index";
	}

//	売れてない奴も表示する（商品一覧）
	@RequestMapping(path = "/client/item/list/{sortType}", method = { RequestMethod.GET })
	public String clientItem(@PathVariable int sortType, Model model) {
		model.addAttribute("items", itemRepository.findAllByQuantityDesc());
//		新着順
		if (sortType ==1) {
			model.addAttribute("items", itemRepository.findAllByOrderByInsertDateDesc());
//		売れ筋順	
		} else {
			model.addAttribute("items",itemRepository.findAllByQuantityDesc());
		}
		return "client/item/list";
	}
	
	/*
	 * 一覧表示 カテゴリ検索
	 * 吉永作成
	 * 
	 * @param model Viewとの値受渡し
	 * @return "client/item/list" 商品一覧
	 */
	@GetMapping(path = "/client/item/list/category")
	public String categorySort(Integer categoryId,  Model model) {
		Category category = new Category();
		category.setId(categoryId);
		List<Item> items = itemRepository.findByCategory(category);
		model.addAttribute("items", items);

		return "client/item/list";
	}
	
}

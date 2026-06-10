package jp.co.sss.shop.form;

import java.io.Serializable;
import java.sql.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.User;

/*
 * レビュー入力フォーム
 */
public class ReviewForm implements Serializable {
	
	/*
	 * レビューID
	 */
	private Integer id;

	/*
	 * ユーザ情報
	 */
	private User user;

	/*
	 * 商品情報
	 */
	private Item item;

	/*
	 * 評価
	 */
	@NotBlank
	private Integer rating;

	/*
	 * コメント
	 */
	@NotBlank
	@Size(min = 1, max = 140)
	private String comments;

	/*
	 * 削除フラグ
	 */
	private Integer deleteFlag;

	/*
	 * 投稿日時
	 */
	private Date insertDate;

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

}
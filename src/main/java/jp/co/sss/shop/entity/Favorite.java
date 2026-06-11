package jp.co.sss.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "favorites")
public class Favorite {
	
	/*
	 * お気に入りID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_favorites_gen")
	@SequenceGenerator(name = "seq_favorites_gen", sequenceName = "seq_favorites", allocationSize = 1)
	private Integer id;
	
	/*
	 * ユーザ情報
	 */
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
	
	/*
	 * 商品情報
	 */
	@ManyToOne
	@JoinColumn(name = "item_id", referencedColumnName = "id")
	private Item item;
	
	/*
	 * 削除フラグ
	 */
	@Column(insertable = false)
	private Integer deleteFlag;

	
	/**
	 * @return id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id セットする id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user セットする user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * @param item セットする item
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * @return deleteFlag
	 */
	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	/**
	 * @param deleteFlag セットする deleteFlag
	 */
	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

}


package com.ebarapp.ebar.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "item_bill")
public class ItemBill extends BaseEntity {

	@NotNull
	@Column(name = "amount")
	private Integer		amount;

	@NotNull
	@OneToOne(cascade = CascadeType.ALL)
	private ItemMenu	itemMenu;

}

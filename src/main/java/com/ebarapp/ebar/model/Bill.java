
package com.ebarapp.ebar.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bill")
public class Bill extends BaseEntity {

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<ItemBill>	itemBill;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<ItemBill>	itemOrder;

}

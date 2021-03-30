
package com.ebarapp.ebar.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bill")
public class Bill extends BaseEntity {

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<ItemBill>	itemBill;

	@Transient
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<ItemBill>	itemOrder;
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "bill")
	private BarTable barTable;

}

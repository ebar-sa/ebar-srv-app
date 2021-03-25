
package com.ebarapp.ebar.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bill")
public class Bill extends BaseEntity {

	@NotNull
	@OneToOne(cascade = CascadeType.ALL)
	private BarTable		table;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<ItemMenu>	itemMenu;

	@Transient
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<ItemMenu>	itemOrder;

}

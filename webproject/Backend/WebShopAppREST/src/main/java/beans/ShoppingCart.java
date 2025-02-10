package beans;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
	
	private int id;
	private User user;
	private List<Chocolate> chocolates;
	private List<Integer> chocolateAmounts;
	private int listNumber;
	private double cijena;

	
	public ShoppingCart(User user) {
		this.user = user;
        this.chocolates = new ArrayList<>();
        this.chocolateAmounts = new ArrayList<>();
        this.cijena = 0.0;
	}

	public ShoppingCart(User user, List<Chocolate> chocolates, List<Integer> chocolateAmounts, int listNumber,
			double cijena) {
		this.user = user;
		this.chocolates = chocolates;
		this.chocolateAmounts = chocolateAmounts;
		this.listNumber = listNumber;
		this.cijena = cijena;
	}

	public ShoppingCart(int id, User user, List<Chocolate> chocolates, List<Integer> chocolateAmounts, int listNumber,
			double cijena) {
		this.id = id;
		this.user = user;
		this.chocolates = chocolates;
		this.chocolateAmounts = chocolateAmounts;
		this.listNumber = listNumber;
		this.cijena = cijena;
	}

	public int getId() {
		return id;
	}


	public User getUser() {
		return user;
	}


	public List<Chocolate> getChocolates() {
		return chocolates;
	}


	public List<Integer> getChocolateAmounts() {
		return chocolateAmounts;
	}


	public int getListNumber() {
		return listNumber;
	}


	public double getCijena() {
		return cijena;
	}


	public void setId(int id) {
		this.id = id;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public void setChocolates(List<Chocolate> chocolates) {
		this.chocolates = chocolates;
	}

	public void setChocolateAmounts(List<Integer> chocolateAmounts) {
		this.chocolateAmounts = chocolateAmounts;
	}


	public void setListNumber(int listNumber) {
		this.listNumber = listNumber;
	}


	public void setCijena(double cijena) {
		this.cijena = cijena;
	}
	
	
}

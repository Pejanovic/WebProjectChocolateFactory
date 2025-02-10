package beans;

public class CartItemResponse {

	private int id;
    private Chocolate chocolate;
    private int amount;

    public CartItemResponse(Chocolate chocolate, int amount) {
        this.chocolate = chocolate;
        this.amount = amount;
    }
    
    public CartItemResponse(int id, Chocolate chocolate, int amount) {
    	this.id = id;
        this.chocolate = chocolate;
        this.amount = amount;
    }
    
    

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Chocolate getChocolate() {
        return chocolate;
    }

    public void setChocolate(Chocolate chocolate) {
        this.chocolate = chocolate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}

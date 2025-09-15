// Customer 클래스 (일반 고객)
class Customer extends Person {
    protected int money;

    public Customer(String name, int age, int money) {
        super(name,age);
        this.money = money;
    }

    // 기본 할인율 0%
    protected double getDiscountRate() {
        return 0.0;
    }

    public void buyItem(Product product, int quantity) {
        int totalPrice = (int) (product.getPrice() * quantity * (1 - getDiscountRate()));

        if (money < totalPrice) {
            System.out.println("X 잔액 부족: " + name);
        }

        if (product.getStock() < quantity) {
            System.out.println("X 재고 부족: " + product.getName());
            return;
        }

        money -= totalPrice;
        product.reduceStock(quantity);

        String msgPrefix = getDiscountRate() > 0 ? "[할인 적용] " : "";
        System.out.println(msgPrefix + name + "이(가) " +
                product.getName() + " " + quantity +
                "개를 구매했습니다. 남은 돈: " + money);

    }
}
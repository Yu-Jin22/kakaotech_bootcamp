class Clerk extends Person {
    public Clerk(String name, int age) {
        super(name, age);
    }

    public void sellItem(Customer customer, Product product, int quantity) {
        System.out.println(name + " 점원이 " + customer.name + "에게 " +
                product.getName() + " " + quantity + "개 판매 중...");
        customer.buyItem(product, quantity);
    }
}
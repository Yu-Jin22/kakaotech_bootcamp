// CorporateCustomer 클래스
class CorporateCustomer extends Customer {
    private double discountRate = 0.1; // 법인 10% 할인

    public CorporateCustomer(String name, int age,int money) {
        super(name, age, money);
    }

    // 법인 할인율 10%
    @Override
    protected double getDiscountRate() {
        return 0.1;
    }

}
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Person 클래스
class Person {
    protected String name;
    protected int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void introduce() {
        System.out.println("안녕하세요. " + name + "입니다.");
    }

}

// Customer 클래스 (일반 고객)
class Customer extends Person {
    protected int money;

    public Customer(String name, int age, int money) {
        super(name,age);
        this.money = money;
    }

    public void buyItem(Product product, int quantity) {
        int totalPrice = product.getPrice() * quantity;
        if (money >= totalPrice) {
            if (product.getStock() >= quantity) {
                money -= totalPrice;
                product.reduceStock(quantity);
                System.out.println(name + "이(가) " + product.getName() +
                        " " + quantity + "개를 구매했습니다. 남은 돈: " + money);
            } else {
                System.out.println("X 재고 부족: " + product.getName());
            }
        } else {
            System.out.println("X 잔액 부족: " + name);
        }

    }
}

// CorporateCustomer 클래스
class CorporateCustomer extends Customer {
    private double discountRate = 0.1; // 법인 10% 할인

    public CorporateCustomer(String name, int age,int money) {
        super(name, age, money);
    }

    @Override
    public void buyItem(Product product, int quantity) {
        int totalPrice = (int) (product.getPrice() * quantity * (1 - discountRate));
        if (money >= totalPrice) {
            if (product.getStock() >= quantity) {
                money -= totalPrice;
                product.reduceStock(quantity);
                System.out.println("[법인 할인] " + name + "이(가) " + product.getName() +
                        " " + quantity + "개를 구매했습니다. 남은 돈: " + money);
            } else {
                System.out.println("X 재고 부족: " + product.getName());
            }
        } else {
            System.out.println("X 잔액 부족: " + name);
        }
    }

}

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

// 상품 클래스
class Product {
    private String name;
    private int price;
    private int stock; // 재고 수량

    public Product(String name, int price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public void reduceStock(int quantity) {
        stock -= quantity;
    }

    public int getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getName() {
        return name;
    }
}

// 재고 체크 스레드
class StockChecker implements Runnable {
    private List<Product> products;
    private boolean running = true;

    public StockChecker(List<Product> products) {
        this.products = products;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            System.out.println("\n[재고 점검]");
            for (Product p : products) {
                System.out.println(" - " + p.getName() + ": 남은 재고 " + p.getStock());
            }

            try {
                Thread.sleep(20000); // 20초마다 재고 확인
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("[재고 점검 스레드 종료]");
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== 올리브영 입장 ===");
        // 1. 점원 생성
        Clerk clerk = new Clerk("점원1", 30);
        clerk.introduce();

        // 2. 고객정보 받기
        // 이름
        System.out.print("고객 이름 입력: ");
        String name = sc.nextLine();

        // 나이
        int age = 0;
        while (true) {
            System.out.print("고객 나이 입력: ");
            String ageInput = sc.nextLine().trim();
            try {
                age = Integer.parseInt(ageInput);
                if(age <= 0){
                    System.out.println("나이는 1 이상이어야 합니다.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("유효한 숫자를 입력해주세요.");
            }
        }

        // 소지금액
        int money = 0;
        while (true) {
            System.out.print("소지 금액 입력: ");
            String moneyInput = sc.nextLine().trim();
            try {
                money = Integer.parseInt(moneyInput);
                if(money < 0){
                    System.out.println("금액은 0 이상이어야 합니다.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("유효한 숫자를 입력해주세요.");
            }
        }

        // 유형
        int type = 0;
        while (true) {
            System.out.print("고객 유형 선택 (1: 일반, 2: 법인): ");
            String typeInput = sc.nextLine().trim();
            try {
                type = Integer.parseInt(typeInput);
                if (type != 1 && type != 2) {
                    System.out.println("1 또는 2를 입력하세요.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("유효한 숫자를 입력해주세요.");
            }
        }

        // type에 따라 다른 객체 생성
        Customer customer;
        if (type == 1) {
            customer = new Customer(name, age, money);
        } else {
            customer = new CorporateCustomer(name, age, money);
        }

        // 3. 상품 목록 생성
        List<Product> products = new ArrayList<>();
        products.add(new Product("스킨", 3000, 0));
        products.add(new Product("세럼", 5000, 2));
        products.add(new Product("크림", 2000, 15));
        products.add(new Product("로션", 4000, 3));
        products.add(new Product("에센스", 8000, 1));

        // 4. 재고체크 스레드 시작
        StockChecker stockChecker = new StockChecker(products);
        Thread stockThread = new Thread(stockChecker);
        stockThread.start();

        // 5. 상품 목록 출력
        System.out.println("\n=== 올리브영 상품 목록 ===");
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            System.out.println((i + 1) + ". " + p.getName() + " - " + p.getPrice() +
                    "원 (재고: " + p.getStock() + ")");
        }

        // 6. 구매할 상품 번호 입력
        int choice = 0;
        while (true) {
            System.out.print("구매할 상품 번호 입력: ");
            String input = sc.nextLine().trim();
            try {
                choice = Integer.parseInt(input);
                if (choice < 1 || choice > products.size()) {
                    System.out.println("X 잘못된 번호입니다.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("X 숫자로 입력해야 합니다.");
            }
        }

        Product selectedProduct = products.get(choice - 1);

        // 7. 법인 고객일 때만 수량 입력
        int quantity = 1;
        if (customer instanceof CorporateCustomer) {
            while (true) {
                System.out.print("수량 입력: ");
                String qtyInput = sc.nextLine().trim();
                try {
                    quantity = Integer.parseInt(qtyInput);
                    if (quantity <= 0) {
                        System.out.println("X 수량은 1 이상이어야 합니다.");
                        continue;
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("X 유효한 숫자를 입력해주세요.");
                }
            }
        }

        // 8. 구매 시도
        clerk.sellItem(customer, selectedProduct, quantity);

        // 9. 쇼핑 종료
        stockChecker.stop(); // 재고 스레드 종료
        System.out.println("\n=== 쇼핑 종료 ===");

        sc.close();

    }

}

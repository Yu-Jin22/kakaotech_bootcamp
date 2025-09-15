import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        try {
            stockThread.join();  // 스레드가 진짜 끝날 때까지 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("\n=== 쇼핑 종료 ===");

        sc.close();

    }

}

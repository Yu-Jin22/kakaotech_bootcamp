import java.util.List;

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
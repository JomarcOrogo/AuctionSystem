import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        MutableAuctionSystemHolder auctionSystemHolder = new MutableAuctionSystemHolder();
        SwingUtilities.invokeLater(() -> new SellerGUI(auctionSystemHolder));
        // sample comment
    }
}

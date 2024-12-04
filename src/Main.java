import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        MutableAuctionSystemHolder auctionSystemHolder = new MutableAuctionSystemHolder();
        DBConnection dbconn = new DBConnection();

        SwingUtilities.invokeLater(() -> new SellerGUI(auctionSystemHolder));


    }
}

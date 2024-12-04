import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SellerGUI {
    private JFrame frame;
    private JTextField itemNameField;
    private JTextField startingBidField;
    private JButton startAuctionButton;

    public SellerGUI(MutableAuctionSystemHolder auctionSystemHolder) {
        frame = new JFrame("Seller - Auction Setup");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel itemNameLabel = new JLabel("Item Name:");
        itemNameLabel.setBounds(50, 50, 100, 25);
        frame.add(itemNameLabel);

        itemNameField = new JTextField();
        itemNameField.setBounds(150, 50, 200, 25);
        frame.add(itemNameField);

        JLabel startingBidLabel = new JLabel("Starting Bid:");
        startingBidLabel.setBounds(50, 100, 100, 25);
        frame.add(startingBidLabel);

        startingBidField = new JTextField();
        startingBidField.setBounds(150, 100, 200, 25);
        frame.add(startingBidField);

        startAuctionButton = new JButton("Start Auction");
        startAuctionButton.setBounds(150, 150, 150, 30);
        frame.add(startAuctionButton);

        startAuctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String itemName = itemNameField.getText();
                double startingBid;
                try {
                    startingBid = Double.parseDouble(startingBidField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Enter a valid starting bid.");
                    return;
                }

                AuctionItem item = new AuctionItem(itemName, startingBid);
                AuctionSystem auctionSystem = new AuctionSystem(item, 30);
                auctionSystemHolder.setAuctionSystem(auctionSystem);

                frame.dispose(); // Close Seller GUI
                new BidderGUI(auctionSystemHolder.getAuctionSystem()); // Open Bidder GUI
            }
        });

        frame.setVisible(true);
    }
}

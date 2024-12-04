import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BidderGUI {
    private JFrame frame;
    private JLabel itemNameLabel;
    private JLabel currentBidLabel;
    private JLabel highestBidderLabel;
    private JLabel timerLabel;
    private JTextField bidderNameField;
    private JTextField bidAmountField;
    private JButton placeBidButton;
    private JButton showHistoryButton;
    private JTextArea historyArea;

    public BidderGUI(AuctionSystem auctionSystem) {
        frame = new JFrame("Bidder - Auction");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        itemNameLabel = new JLabel("Item: " + auctionSystem.getAuctionItem().getItemName());
        itemNameLabel.setBounds(20, 20, 300, 25);
        frame.add(itemNameLabel);

        currentBidLabel = new JLabel("Current Bid: " + auctionSystem.getAuctionItem().getCurrentBid());
        currentBidLabel.setBounds(20, 50, 300, 25);
        frame.add(currentBidLabel);

        highestBidderLabel = new JLabel("Highest Bidder: " + auctionSystem.getAuctionItem().getHighestBidder());
        highestBidderLabel.setBounds(20, 80, 300, 25);
        frame.add(highestBidderLabel);

        timerLabel = new JLabel("Time left: " + auctionSystem.getAuctionDuration() + " seconds");
        timerLabel.setBounds(20, 110, 300, 25);
        frame.add(timerLabel);

        JLabel bidderNameLabel = new JLabel("Bidder Name:");
        bidderNameLabel.setBounds(20, 140, 100, 25);
        frame.add(bidderNameLabel);

        bidderNameField = new JTextField();
        bidderNameField.setBounds(130, 140, 200, 25);
        frame.add(bidderNameField);

        JLabel bidAmountLabel = new JLabel("Bid Amount:");
        bidAmountLabel.setBounds(20, 170, 100, 25);
        frame.add(bidAmountLabel);

        bidAmountField = new JTextField();
        bidAmountField.setBounds(130, 170, 200, 25);
        frame.add(bidAmountField);

        placeBidButton = new JButton("Place Bid");
        placeBidButton.setBounds(130, 200, 100, 25);
        frame.add(placeBidButton);

        placeBidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!auctionSystem.isAuctionRunning()) {
                    JOptionPane.showMessageDialog(frame, "The auction has ended.");
                    return;
                }

                String bidderName = bidderNameField.getText();
                double bidAmount;
                try {
                    bidAmount = Double.parseDouble(bidAmountField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Enter a valid bid amount.");
                    return;
                }

                try {
                    auctionSystem.placeBid(bidderName, bidAmount);
                    currentBidLabel.setText("Current Bid: " + auctionSystem.getAuctionItem().getCurrentBid());
                    highestBidderLabel.setText("Highest Bidder: " + auctionSystem.getAuctionItem().getHighestBidder());
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage());
                }
            }
        });

        showHistoryButton = new JButton("Show History");
        showHistoryButton.setBounds(250, 200, 150, 25);
        frame.add(showHistoryButton);

        historyArea = new JTextArea();
        historyArea.setBounds(20, 240, 450, 100);
        historyArea.setEditable(false);
        frame.add(historyArea);

        showHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder history = new StringBuilder();
                for (String record : auctionSystem.getBidHistory()) {
                    history.append(record).append("\n");
                }
                historyArea.setText(history.toString());
            }
        });

        new Thread(() -> {
            int timeLeft = auctionSystem.getAuctionDuration();
            auctionSystem.startAuction(); // Start the auction when the timer begins
            while (timeLeft > 0) {
                final int remainingTime = timeLeft;
                SwingUtilities.invokeLater(() -> timerLabel.setText("Time left: " + remainingTime + " seconds"));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                timeLeft--;
            }
            auctionSystem.endAuction();
            SwingUtilities.invokeLater(() -> {
                timerLabel.setText("Auction ended!");
                placeBidButton.setEnabled(false); // Disable bid button when auction ends
            });
        }).start();

        frame.setVisible(true);
    }
}

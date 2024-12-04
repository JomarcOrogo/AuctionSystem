import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuctionGUI {
    private AuctionSystem auctionSystem;

    public AuctionGUI(AuctionSystem auctionSystem) {
        this.auctionSystem = auctionSystem;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Auction System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new BorderLayout());

        // Panel for auction information
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(4, 1));

        JLabel itemLabel = new JLabel("Item: " + auctionSystem.getAuctionItem().getItemName());
        JLabel currentBidLabel = new JLabel("Current Bid: " + auctionSystem.getAuctionItem().getCurrentBid());
        JLabel highestBidderLabel = new JLabel("Highest Bidder: " + auctionSystem.getAuctionItem().getHighestBidder());
        JLabel timerLabel = new JLabel("Time left: -");

        infoPanel.add(itemLabel);
        infoPanel.add(currentBidLabel);
        infoPanel.add(highestBidderLabel);
        infoPanel.add(timerLabel);

        // Panel for bidding
        JPanel bidPanel = new JPanel();
        bidPanel.setLayout(new GridLayout(3, 1));

        JTextField bidderNameField = new JTextField();
        JTextField bidAmountField = new JTextField();
        JButton bidButton = new JButton("Place Bid");

        bidPanel.add(new JLabel("Bidder Name:"));
        bidPanel.add(bidderNameField);
        bidPanel.add(new JLabel("Bid Amount:"));
        bidPanel.add(bidAmountField);
        bidPanel.add(bidButton);

        // Panel for history and control buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 1));

        JButton startAuctionButton = new JButton("Start Auction");
        JButton showHistoryButton = new JButton("Show Bid History");
        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);

        controlPanel.add(startAuctionButton);
        controlPanel.add(showHistoryButton);
        controlPanel.add(new JScrollPane(historyArea));

        // Adding listeners
        startAuctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                auctionSystem.startAuction();
                new Thread(() -> {
                    for (int i = auctionSystem.getAuctionDuration(); i > 0; i--) {
                        int finalI = i;
                        SwingUtilities.invokeLater(() -> timerLabel.setText("Time left: " + finalI + " seconds"));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    SwingUtilities.invokeLater(() -> {
                        timerLabel.setText("Time left: 0");
                        currentBidLabel.setText("Current Bid: " + auctionSystem.getAuctionItem().getCurrentBid());
                        highestBidderLabel.setText("Highest Bidder: " + auctionSystem.getAuctionItem().getHighestBidder());
                    });
                }).start();
            }
        });

        bidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bidderName = bidderNameField.getText();
                double bidAmount;
                try {
                    bidAmount = Double.parseDouble(bidAmountField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Enter a valid number for the bid amount.");
                    return;
                }

                auctionSystem.placeBid(bidderName, bidAmount);
                currentBidLabel.setText("Current Bid: " + auctionSystem.getAuctionItem().getCurrentBid());
                highestBidderLabel.setText("Highest Bidder: " + auctionSystem.getAuctionItem().getHighestBidder());
            }
        });

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

        // Adding panels to the frame
        frame.add(infoPanel, BorderLayout.NORTH);
        frame.add(bidPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}

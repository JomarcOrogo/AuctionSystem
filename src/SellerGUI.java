import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SellerGUI {
    private JFrame frame;
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private JTextField itemNameField;
    private JTextField startingBidField;
    private JTextField bidAmountField;
    private JButton addItemButton;
    private JButton startAuctionButton;
    private JButton bidButton;
    private JButton logoutButton;
    private MutableAuctionSystemHolder auctionSystemHolder;
    private String sellerName;

    public SellerGUI(MutableAuctionSystemHolder auctionSystemHolder) {
        this.auctionSystemHolder = auctionSystemHolder;
        initializeGUI();
    }

    private void initializeGUI() {
        sellerLogin();

        frame = new JFrame("Seller - Auction Management");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel tableLabel = new JLabel("Auction Items:");
        tableLabel.setBounds(20, 20, 200, 25);
        frame.add(tableLabel);

        tableModel = new DefaultTableModel(new Object[]{"Item Name", "Starting Bid", "Seller Name", "Status"}, 0);
        itemsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setBounds(20, 50, 740, 150);
        frame.add(scrollPane);

        JLabel itemNameLabel = new JLabel("Item Name:");
        itemNameLabel.setBounds(20, 220, 100, 25);
        frame.add(itemNameLabel);

        itemNameField = new JTextField();
        itemNameField.setBounds(120, 220, 200, 25);
        frame.add(itemNameField);

        JLabel startingBidLabel = new JLabel("Starting Bid:");
        startingBidLabel.setBounds(20, 260, 100, 25);
        frame.add(startingBidLabel);

        startingBidField = new JTextField();
        startingBidField.setBounds(120, 260, 200, 25);
        frame.add(startingBidField);

        addItemButton = new JButton("Add Item");
        addItemButton.setBounds(350, 220, 150, 25);
        frame.add(addItemButton);

        startAuctionButton = new JButton("Start Auction");
        startAuctionButton.setBounds(350, 260, 150, 25);
        frame.add(startAuctionButton);

        JLabel bidAmountLabel = new JLabel("Your Bid:");
        bidAmountLabel.setBounds(20, 300, 100, 25);
        frame.add(bidAmountLabel);

        bidAmountField = new JTextField();
        bidAmountField.setBounds(120, 300, 200, 25);
        frame.add(bidAmountField);

        bidButton = new JButton("Place Bid");
        bidButton.setBounds(350, 300, 150, 25);
        frame.add(bidButton);

        logoutButton = new JButton("Logout");
        logoutButton.setBounds(550, 300, 150, 25);
        frame.add(logoutButton);

        addListeners();

        frame.setVisible(true);
    }

    private void sellerLogin() {
        sellerName = JOptionPane.showInputDialog(null, "Enter your name to log in as a seller:",
                "Seller Login", JOptionPane.PLAIN_MESSAGE);

        if (sellerName == null || sellerName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Seller name is required. Exiting...");
            System.exit(0);
        }
    }

    private void addListeners() {
        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String itemName = itemNameField.getText();
                String startingBidText = startingBidField.getText();
                double startingBid;

                try {
                    startingBid = Double.parseDouble(startingBidText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid starting bid.");
                    return;
                }

                if (itemName.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Item name cannot be empty.");
                    return;
                }

                tableModel.addRow(new Object[]{itemName, startingBid, sellerName, "Still Up for Bid"});
                itemNameField.setText("");
                startingBidField.setText("");
            }
        });

        startAuctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = itemsTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Please select an item to start the auction.");
                    return;
                }

                String itemName = (String) tableModel.getValueAt(selectedRow, 0);
                double startingBid = (double) tableModel.getValueAt(selectedRow, 1);

                AuctionItem auctionItem = new AuctionItem(itemName, startingBid);
                AuctionSystem auctionSystem = new AuctionSystem(auctionItem, 60);
                auctionSystemHolder.setAuctionSystem(auctionSystem);

                auctionSystem.startAuction();
                JOptionPane.showMessageDialog(frame, "Auction for '" + itemName + "' has started.");
                tableModel.setValueAt("Still Up for Bid", selectedRow, 3);

                new Thread(() -> {
                    try {
                        Thread.sleep(auctionSystem.getAuctionDuration() * 1000L);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                    SwingUtilities.invokeLater(() -> {
                        if (!auctionSystem.isAuctionRunning()) {
                            tableModel.setValueAt("Sold", selectedRow, 3);
                        }
                    });
                }).start();
            }
        });

        bidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bidText = bidAmountField.getText();
                double bidAmount;

                try {
                    bidAmount = Double.parseDouble(bidText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid bid amount.");
                    return;
                }

                AuctionSystem auctionSystem = auctionSystemHolder.getAuctionSystem();
                if (auctionSystem == null || !auctionSystem.isAuctionRunning()) {
                    JOptionPane.showMessageDialog(frame, "No active auction to bid on.");
                    return;
                }

                boolean bidAccepted = auctionSystem.placeBid(bidAmount, sellerName);
                if (bidAccepted) {
                    JOptionPane.showMessageDialog(frame, "Bid placed successfully!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Bid too low. Try again.");
                }

                bidAmountField.setText("");
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    frame.dispose();
                    new SellerGUI(auctionSystemHolder);
                }
            }
        });
    }
}

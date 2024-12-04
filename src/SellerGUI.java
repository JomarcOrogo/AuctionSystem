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
    private JButton addItemButton;
    private JButton startAuctionButton;
    private MutableAuctionSystemHolder auctionSystemHolder;
    private String sellerName;

    public SellerGUI(MutableAuctionSystemHolder auctionSystemHolder) {
        this.auctionSystemHolder = auctionSystemHolder;

        // Seller Login
        sellerName = JOptionPane.showInputDialog(null, "Enter your name to log in as a seller:",
                "Seller Login", JOptionPane.PLAIN_MESSAGE);

        if (sellerName == null || sellerName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Seller name is required. Exiting...");
            System.exit(0);
        }

        frame = new JFrame("Seller - Auction Management");
        frame.setSize(800, 400);
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

                // Add item to the table with the seller's name and "Still Up for Bid" status
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

                // Start the auction
                auctionSystem.startAuction();
                JOptionPane.showMessageDialog(frame, "Auction for '" + itemName + "' has started.");

                // Update the status of the selected item
                tableModel.setValueAt("Still Up for Bid", selectedRow, 3);

                // Start a timer to automatically update the status when the auction ends
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

        frame.setVisible(true);
    }
}

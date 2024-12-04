public class AuctionItem {
    private String itemName;
    private double startingBid;
    private double currentBid;
    private String highestBidder;

    public AuctionItem(String itemName, double startingBid) {
        this.itemName = itemName;
        this.startingBid = startingBid;
        this.currentBid = startingBid;
        this.highestBidder = "None";
    }

    public String getItemName() {
        return itemName;
    }

    public double getStartingBid() {
        return startingBid;
    }

    public double getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(double currentBid) {
        this.currentBid = currentBid;
    }

    public String getHighestBidder() {
        return highestBidder;
    }

    public void setHighestBidder(String highestBidder) {
        this.highestBidder = highestBidder;
    }
}

import java.util.ArrayList;
import java.util.List;

public class AuctionSystem {
    private AuctionItem auctionItem;
    private int auctionDuration;
    private boolean isAuctionRunning;
    private final List<String> bidHistory;

    public AuctionSystem(AuctionItem auctionItem, int auctionDuration) {
        this.auctionItem = auctionItem;
        this.auctionDuration = auctionDuration;
        this.isAuctionRunning = false;
        this.bidHistory = new ArrayList<>();
    }

    public AuctionItem getAuctionItem() {
        return auctionItem;
    }

    public int getAuctionDuration() {
        return auctionDuration;
    }

    public boolean isAuctionRunning() {
        return isAuctionRunning;
    }

    public synchronized void startAuction() {
        if (isAuctionRunning) {
            throw new IllegalStateException("Auction is already running.");
        }
        isAuctionRunning = true;
    }

    public synchronized void placeBid(String bidderName, double bidAmount) {
        if (!isAuctionRunning) {
            throw new IllegalStateException("Auction is not running.");
        }
        if (bidAmount > auctionItem.getCurrentBid()) {
            auctionItem.setCurrentBid(bidAmount);
            auctionItem.setHighestBidder(bidderName);
            bidHistory.add(bidderName + " bid $" + bidAmount);
        } else {
            throw new IllegalArgumentException("Bid must be higher than the current bid.");
        }
    }

    public List<String> getBidHistory() {
        return bidHistory;
    }

    public synchronized void endAuction() {
        isAuctionRunning = false;
    }
}

package rt.server.lobbychat.flood;

import rt.server.ServerProperties;

public class FloodController {
    private String lastMessage;
    private int messagesRepeated;
    private long lastMessageTime;

    public boolean detected(String msg) {
        if (System.currentTimeMillis() - this.lastMessageTime < ServerProperties.MIN_FLOOD_TIMEOUT) {
            return true;
        } else {
            if (msg.equals(this.lastMessage)) {
                ++this.messagesRepeated;
                if (this.messagesRepeated >= ServerProperties.MIN_FLOOD_COUNT) {
                    return true;
                }
            } else {
                this.messagesRepeated = 0;
            }
            this.lastMessageTime = System.currentTimeMillis();
            this.lastMessage = msg;
            return false;
        }
    }
}

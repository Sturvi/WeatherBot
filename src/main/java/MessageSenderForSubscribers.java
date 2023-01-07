import org.telegram.telegrambots.meta.api.methods.groupadministration.DeleteChatStickerSet;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MessageSenderForSubscribers extends Thread {
    HashMap<String, Message> subscribers;


    public MessageSenderForSubscribers(HashMap<String, Message> subscribers) {
        this.subscribers = subscribers;
    }

    public void start() {
        LocalDateTime timeToSendMessages = LocalDateTime.now();
        if (timeToSendMessages.getHour() >= 21) {
            timeToSendMessages = timeToSendMessages.withDayOfMonth(timeToSendMessages.getDayOfMonth() + 1);
        }
        timeToSendMessages = timeToSendMessages.withHour(21);
        timeToSendMessages = timeToSendMessages.withMinute(00);
        timeToSendMessages = timeToSendMessages.withSecond(00);
        InputExportFromTelegram messageSender = new InputExportFromTelegram(subscribers);

        while (true) {
            var currentTime = LocalDateTime.now();
            if (currentTime.isAfter(timeToSendMessages)) {
                Weather weather = new Weather();
                var messageText = weather.getOnlyTomorrowWeather();

                for (Map.Entry<String, Message> entry : subscribers.entrySet()) {
                    String key = entry.getKey();
                    Message message = entry.getValue();
                    messageSender.sendMsg(message, messageText);
                }

                timeToSendMessages.plusDays(1);
            } else {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

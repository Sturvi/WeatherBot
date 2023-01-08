import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class MessageSenderForSubscribers extends Thread {
    HashMap<String, Message> subscribers;
    LocalDateTime timeToSendMessages;
    ZoneId timeZone = ZoneId.of("Etc/GMT-4");

    public MessageSenderForSubscribers(HashMap<String, Message> subscribers) {
        this.subscribers = subscribers;
    }

    public void run() {

        setMessageSendTime();

        if (subscribers.isEmpty()) {
            recoverySubscribers();
        }

        LocalDateTime testTime = LocalDateTime.now();

        while (true) {
            subscribers.isEmpty();
            LocalDateTime currentTime = LocalDateTime.now(timeZone);

            if (currentTime.isAfter(timeToSendMessages)) {
                sendMessageToAllSubscribers();
            } else {
                try {
                    sleep(30000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void sendMessageToAllSubscribers() {
        InputExportFromTelegram messageSender = new InputExportFromTelegram(subscribers);
        Weather weather = new Weather();
        var messageText = weather.getOnlyTomorrowWeather();

        for (Map.Entry<String, Message> entry : subscribers.entrySet()) {
            String key = entry.getKey();
            Message message = entry.getValue();
            messageSender.sendMsg(message, messageText);
        }

        timeToSendMessages = timeToSendMessages.plusDays(1);
    }

    private void recoverySubscribers() {
        //File folder = new File("/Bots/MersinWeatherBot/backupSub/");
        File folder = new File("/Bots/BakuWeatherBot/backupSub/");
        File[] files = folder.listFiles();

        if (files != null && files.length > 0) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file);
                     ObjectInputStream objectInputStream = new ObjectInputStream(fis)) {
                    Message message = (Message) objectInputStream.readObject();
                    subscribers.put(file.getName().replaceAll(".txt", ""), message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void setMessageSendTime() {
        timeToSendMessages = LocalDateTime.now(timeZone);
        timeToSendMessages = timeToSendMessages.withHour(21);
        timeToSendMessages = timeToSendMessages.withMinute(00);
        timeToSendMessages = timeToSendMessages.withSecond(00);
        LocalDateTime currentTime = LocalDateTime.now(timeZone);
        if (timeToSendMessages.isBefore(currentTime)) {
            timeToSendMessages = timeToSendMessages.plusDays(1);
        }
    }
}

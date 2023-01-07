import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {

        var subscribers = new HashMap<String, Message>();

        MessageSenderForSubscribers messageSenderForSubscribers = new MessageSenderForSubscribers(subscribers);
        messageSenderForSubscribers.start();

        TelegramBotsApi telegramBotsApi = null;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new InputExportFromTelegram(subscribers));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


}
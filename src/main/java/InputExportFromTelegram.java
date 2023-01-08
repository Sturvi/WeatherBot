import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputExportFromTelegram extends TelegramLongPollingBot {

    HashMap<String, Message> subscribers;

    public InputExportFromTelegram(HashMap<String, Message> subscribers) {
        this.subscribers = subscribers;
    }


    @Override
    public void onUpdateReceived(Update update) {
        var message = new Message();
        message = update.getMessage();

        saveUsersData(message);

        switch (message.getText()) {
            case ("Get the weather forecast"):
                getCurrentWeather(message);
                break;
            case ("/start"):
                sendMsg(message, "Welcome to Baku Weather Bot");
                break;
            case ("Automatically get the weather for tomorrow"):
                if (!subscribers.containsKey(message.getChatId().toString())) {
                    subscribers.put(message.getChatId().toString(), message);
                    backupSubs(message);
                }
                sendMsg(message, "You will receive the weather for tomorrow every evening at 21:00 GTM +4");
                break;
            case ("No more getting the weather for tomorrow"):
                if (subscribers.containsKey(message.getChatId().toString())) {
                    subscribers.remove(message.getChatId().toString());
                    deleteSubs(message);
                }
                sendMsg(message, "You will no longer automatically receive the weather.");
                break;
            default:
                sendMsg(message, "Incorrect input data");
        }
    }

    private void saveUsersData(Message message) {
        File usersDataDir = new File("/Bots/BakuWeatherBot/All User");
        if (!usersDataDir.exists()){
            usersDataDir.mkdirs();
        }

        File file = new File("/Bots/BakuWeatherBot/All User/" + message.getChatId().toString() + ".txt");
        try (PrintWriter printWriter = new PrintWriter(file);){

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteSubs(Message message) {
        //File file = new File("/Bots/MersinWeatherBot/backupSub/" + message.getChatId().toString() + ".txt");
        File file = new File("/Bots/BakuWeatherBot/backupSub/" + message.getChatId().toString() + ".txt");
        file.delete();
    }

    private void backupSubs(Message message) {
        //File file = new File("/Bots/MersinWeatherBot/backupSub/" + message.getChatId().toString() + ".txt");
        File dirForBackup = new File("/Bots/BakuWeatherBot/backupSub");
        if (!dirForBackup.exists()) {
            dirForBackup.mkdirs();
        }

        File file = new File("/Bots/BakuWeatherBot/backupSub/" + message.getChatId().toString() + ".txt");
        try (PrintWriter printWriter = new PrintWriter(file);
             FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos)) {
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getCurrentWeather(Message message) {
        var weather = new Weather();
        var messageList = weather.getWeather();
        for (int i = 0; i < messageList.size(); i++) {
            sendMsg(message, messageList.get(i));
        }
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        // sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            setButtons(sendMessage, message);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setButtons(SendMessage sendMessage, Message message) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();

        if (subscribers.containsKey(message.getChatId().toString())) {
            keyboardFirstRow.add(new KeyboardButton("No more getting the weather for tomorrow"));
        } else {
            keyboardFirstRow.add(new KeyboardButton("Automatically get the weather for tomorrow"));
        }
        keyboardSecondRow.add(new KeyboardButton("Get the weather forecast"));

        keyboardRowList.add(keyboardFirstRow);
        keyboardRowList.add(keyboardSecondRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    @Override
    public String getBotUsername() {
        return "BakuWeatherBot";
    }

    @Override
    public String getBotToken() {
        return "5854170777:AAFFYS-9R2md0zd-ERnYNGDuwHbrKDxBn1Q";
    }
}

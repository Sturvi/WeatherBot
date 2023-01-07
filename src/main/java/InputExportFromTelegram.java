import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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


        switch (message.getText()){
            case ("Get the weather forecast"):
                getCurrentWeather(message);
                break;
            case ("Automatically get the weather for tomorrow"):
                if (!subscribers.containsKey(message.getChatId().toString())){
                    subscribers.put(message.getChatId().toString(), message);
                }
                sendMsg(message, "You will receive the weather for tomorrow every evening at 21:00 GTM +3");
                break;
            case ("No more getting the weather for tomorrow"):
                if (subscribers.containsKey(message.getChatId().toString())){
                    subscribers.remove(message.getChatId());
                }
                sendMsg(message, "You will no longer automatically receive the weather.");
                break;
            default:
                sendMsg(message, "Incorrect input data");
        }
    }

    private void getCurrentWeather (Message message){
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
            setButtons(sendMessage);
            execute (sendMessage);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setButtons (SendMessage sendMessage){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();

        if (subscribers.containsKey(sendMessage.getChatId())){
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
        return "WeatherInMersinBot";
    }

    @Override
    public String getBotToken() {
        return "5589399615:AAEymBtNIJo-EP6kKFtm5Hr3uMdn4oVm9zo";
    }
}

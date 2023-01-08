import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Weather {

    private String currentTemp;
    private String currentWindSpeed;
    private String currentWeatherCod;
    private String tomorrowTempMin;
    private String tomorrowTempMax;
    private String tomorrowWindSpeed;
    private String tomorrowWeatherCod;

    public ArrayList<String> getWeather() {
        try {
            getWeatherFromAPI();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var message = new ArrayList<String>();
        message.add("Current Weather\nTemperature: " + currentTemp + " °C\nWind Speed: " + currentWindSpeed +
                " m/s\n" + weatherInterpretation(Integer.parseInt(currentWeatherCod)));
        message.add("Tomorrow's Weather Forecast:\nTemperature (min): " + tomorrowTempMin +
                "°C \nTemperature (max): " + tomorrowTempMax + "°C \nWind Speed: " + tomorrowWindSpeed +
                " m/s\n" + weatherInterpretation(Integer.parseInt(tomorrowWeatherCod)));

        return message;
    }

    public String getOnlyTomorrowWeather() {
        try {
            getWeatherFromAPI();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LocalDate localDate = LocalDate.now().plusDays(1);
        return localDate + "\nTomorrow's Weather Forecast:\nTemperature (min): " + tomorrowTempMin +
                "°C \nTemperature (max): " + tomorrowTempMax + "°C \nWind Speed: " + tomorrowWindSpeed +
                " m/s\n" + weatherInterpretation(Integer.parseInt(tomorrowWeatherCod));
    }

    private String weatherInterpretation(int code) {
        switch (code) {
            case (0):
                return "Clear sky";
            case (1):
                return "Mainly clear";
            case (2):
                return "Partly cloudy";
            case (3):
                return "Overcast";
            case (45):
                return "Fog";
            case (48):
                return "Depositing rime fog";
            case (51):
                return "Light Drizzle";
            case (53):
                return "Moderate Drizzle";
            case (55):
                return "Dense intensity Drizzle";
            case (56):
                return "Light Freezing Drizzle";
            case (57):
                return "Dense intensity Freezing Drizzle";
            case (61):
                return "Slight rain";
            case (63):
                return "Moderate rain";
            case (65):
                return "Heavy intensity rain";
            case (66):
                return "Light Freezing Rain";
            case (67):
                return "Heavy intensity Freezing Rain";
            case (71):
                return "Slight Snow fall";
            case (73):
                return "Moderate Snow fall";
            case (75):
                return "Heavy intensity Snow fall";
            case (77):
                return "Snow grains";
            case (80):
                return "Slight Rain showers";
            case (81):
                return "Moderate Rain showers";
            case (82):
                return "Violent Rain showers";
            case (85):
                return "Slight Snow showers";
            case (86):
                return "Heavy Snow showers";
            default:
                return ":-(";
        }
    }

    public void getWeatherFromAPI() throws IOException {
        //Mersin URL url = new URL("https://api.open-meteo.com/v1/forecast?latitude=36.81&longitude=34.64&hourly=temperature_2m,weathercode&models=best_match&daily=weathercode,temperature_2m_max,temperature_2m_min,windspeed_10m_max&current_weather=true&windspeed_unit=ms&timezone=auto");
        URL url = new URL("https://api.open-meteo.com/v1/forecast?latitude=40.38&longitude=49.89&hourly=temperature_2m,weathercode&models=best_match&daily=weathercode,temperature_2m_max,temperature_2m_min,windspeed_10m_max&current_weather=true&windspeed_unit=ms&timezone=auto");

        Scanner scanner = new Scanner((InputStream) url.getContent());
        StringBuilder result = new StringBuilder();

        while (scanner.hasNext()) {
            result.append(scanner.nextLine());
        }

        JSONObject jsonObject = new JSONObject(result.toString());
        JSONObject currentWeather = jsonObject.getJSONObject("current_weather");
        currentTemp = String.valueOf(currentWeather.getDouble("temperature"));
        currentWindSpeed = String.valueOf(currentWeather.getDouble("windspeed"));
        currentWeatherCod = String.valueOf(currentWeather.getInt("weathercode")).replaceAll(".0", "");

        JSONObject dailyWeather = jsonObject.getJSONObject("daily");
        JSONArray temp = dailyWeather.getJSONArray("temperature_2m_min");
        tomorrowTempMin = String.valueOf(temp.getDouble(0));
        temp = dailyWeather.getJSONArray("temperature_2m_max");
        tomorrowTempMax = String.valueOf(temp.getDouble(0));
        temp = dailyWeather.getJSONArray("weathercode");
        tomorrowWeatherCod = String.valueOf(temp.getDouble(0)).replaceAll(".0", "");
        temp = dailyWeather.getJSONArray("windspeed_10m_max");
        tomorrowWindSpeed = String.valueOf(temp.getDouble(0));
    }
}

package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
public class Weather {
    private final String prefix = "https://api.openweathermap.org/data/2.5/weather?q=";
    private final String suffix = "&mode=xml&appid=";
    private String url, apiKey;
    private double latitude, longitude;
    private LocalDateTime sunrise, sunset;

    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd:MM:yy HH:mm:ss");

    public Weather(String apiKey) {
        this.apiKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
        this.url = null;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.sunrise = null;
        this.sunset = null;
    }

    public boolean doRequest(String city, String XMLFile, String TXTFile) throws IOException, ParserConfigurationException, SAXException {
        URL server;
        HttpsURLConnection service;
        BufferedWriter fileTXT = new BufferedWriter(new FileWriter(TXTFile));
        int status;

        url = prefix + URLEncoder.encode(city, StandardCharsets.UTF_8) + suffix + apiKey;
        server = new URL(url);
        service = (HttpsURLConnection) server.openConnection();
        service.setRequestProperty("Host", "api.openweathermap.org");
        service.setRequestProperty("Accept", "application/xml");
        service.setRequestProperty("Accept-Charset", "UTF-8");
        service.setRequestMethod("GET");
        service.setDoOutput(true); //attivare la ricezione

        status = service.getResponseCode();
        if (status != 200) {
            return false;
        }
        Files.copy(service.getInputStream(), new File(XMLFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
        parseXML(XMLFile);
        fileTXT.append("Got lat=").append(String.valueOf(latitude)).append("; long=").append(String.valueOf(longitude)).append("; sunrise=").append(dateFormatter.format(sunrise)).append("; sunset=").append(dateFormatter.format(sunset));
        fileTXT.close();
        return true;
    }

    private void parseXML(String XMLDocument) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document document;
        Element root, element;
        NodeList nodeList;

        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        document = builder.parse(XMLDocument);
        root = document.getDocumentElement();

        nodeList = root.getElementsByTagName("coord");
        element = (Element) nodeList.item(0);
        longitude = Double.parseDouble(element.getAttribute("lon"));
        latitude = Double.parseDouble(element.getAttribute("lat"));

        nodeList = root.getElementsByTagName("sun");
        element = (Element) nodeList.item(0);
        sunrise = LocalDateTime.parse(element.getAttribute("rise"));
        sunset = LocalDateTime.parse(element.getAttribute("set"));
    }

    public static void main(String[] args) {
        var weather = new Weather("b24362663f40770d8d3cab03ddf23226");
        try {
            var check = weather.doRequest("Livorno", "ciao.xml", "ciao.txt");
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }

    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getSunrise() {
        return sunrise;
    }

    public void setSunrise(LocalDateTime sunrise) {
        this.sunrise = sunrise;
    }

    public LocalDateTime getSunset() {
        return sunset;
    }

    public void setSunset(LocalDateTime sunset) {
        this.sunset = sunset;
    }
}
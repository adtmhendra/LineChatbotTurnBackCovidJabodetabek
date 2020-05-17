package com.dicoding.turnbackcovidjabodetabek;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.ReplyEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.FlexMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.flex.container.FlexContainer;
import com.linecorp.bot.model.objectmapper.ModelObjectMapper;
import com.linecorp.bot.model.profile.UserProfileResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class Controller {

    @Autowired
    @Qualifier("lineMessagingClient")
    private LineMessagingClient lineMessagingClient;

    @Autowired
    @Qualifier("lineSignatureValidator")
    private LineSignatureValidator lineSignatureValidator;

    @Autowired
    private BotTemplate botTemplate;

    private UserProfileResponse sender = null;

    @RequestMapping(value="/webhook", method= RequestMethod.POST)
    public ResponseEntity<String> callback(
            @RequestHeader("X-Line-Signature") String xLineSignature,
            @RequestBody String eventsPayload)
    {
        try {
//            if (!lineSignatureValidator.validateSignature(eventsPayload.getBytes(), xLineSignature)) {
//                throw new RuntimeException("Invalid Signature Validation");
//            }

            // parsing event
            ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
            EventsModel eventsModel = objectMapper.readValue(eventsPayload, EventsModel.class);

            eventsModel.getEvents().forEach((event)->{
                if (event instanceof JoinEvent || event instanceof FollowEvent) {
                    String replyToken = ((ReplyEvent) event).getReplyToken();
                    handleJointOrFollowEvent(replyToken);
                } else if (event instanceof MessageEvent) {
                    handleMessageEvent((MessageEvent) event);
                }
            });

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Flex message utama yg akan ditampilkan saat pertama kali chatbot ditambahkan sebagai teman
    private void replyMainFlexMessage(String replyToken) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();

            // Mengambil data & template flex message dari flex_main.json
            String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_main.json"));

            ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
            FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);

            // Judul yg akan muncul di notifikasi pesan user
            ReplyMessage replyMessage = new ReplyMessage(replyToken, new FlexMessage("Bersama kita lawan Covid-19!", flexContainer));
            reply(replyMessage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Flex message yg akan ditampilkan ketika user mengirim keyword "Cek kota"
    private void replyFlexCity(String replyToken) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();

            String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_region.json"));

            ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
            FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);

            ReplyMessage replyMessage = new ReplyMessage(replyToken, new FlexMessage("Daftar daerah terdampak pandemi", flexContainer));
            reply(replyMessage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Flex message yg akan ditampilkan ketika user mengirim keyword "Lihat berita"
    private void replyFlexNews(String replyToken) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();

            String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_news.json"));

            ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
            FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);

            ReplyMessage replyMessage = new ReplyMessage(replyToken, new FlexMessage("Berita Terbaru Covid-19 jabodetabek", flexContainer));
            reply(replyMessage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Flex message yg akan ditampilkan ketika user mengirim keyword "Rumah sakit rujukan jakarta"
    private void replyFlexJakartaHospital(String replyToken) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();

            String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_jakarta_hospital.json"));

            ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
            FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);

            ReplyMessage replyMessage = new ReplyMessage(replyToken, new FlexMessage("Daftar rumah sakit rujukan daerah Jakarta", flexContainer));
            reply(replyMessage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Flex message yg akan ditampilkan ketika user mengirim keyword "Rumah sakit rujukan Bogor"
    private void replyFlexBogorHospital(String replyToken) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();

            String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_bogor_hospital.json"));

            ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
            FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);

            ReplyMessage replyMessage = new ReplyMessage(replyToken, new FlexMessage("Daftar rumah sakit rujukan daerah Bogor", flexContainer));
            reply(replyMessage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Flex message yg akan ditampilkan ketika user mengirim keyword "Rumah sakit rujukan depok"
    private void replyFlexDepokHospital(String replyToken) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();

            String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_depok_hospital.json"));

            ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
            FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);

            ReplyMessage replyMessage = new ReplyMessage(replyToken, new FlexMessage("Daftar rumah sakit rujukan daerah Depok", flexContainer));
            reply(replyMessage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Flex message yg akan ditampilkan ketika user mengirim keyword "Rumah sakit rujukan tangerang"
    private void replyFlexTangerangHospital(String replyToken) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();

            String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_tangerang_hospital.json"));

            ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
            FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);

            ReplyMessage replyMessage = new ReplyMessage(replyToken, new FlexMessage("Daftar rumah sakit rujukan daerah Tangerang", flexContainer));
            reply(replyMessage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Flex message yg akan ditampilkan ketika user mengirim keyword "Rumah sakit rujukan bekasi"
    private void replyFlexBekasiHospital(String replyToken) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();

            String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_bekasi_hospital.json"));

            ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
            FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);

            ReplyMessage replyMessage = new ReplyMessage(replyToken, new FlexMessage("Daftar rumah sakit rujukan daerah Bekasi", flexContainer));
            reply(replyMessage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Flex message yg akan ditampilkan ketika user mengirim keyword "Video"
    private void replyFlexVideo(String replyToken) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();

            String flexTemplate = IOUtils.toString(classLoader.getResourceAsStream("flex_video.json"));

            ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
            FlexContainer flexContainer = objectMapper.readValue(flexTemplate, FlexContainer.class);

            ReplyMessage replyMessage = new ReplyMessage(replyToken, new FlexMessage("Berita Terbaru Covid-19 jabodetabek", flexContainer));
            reply(replyMessage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void reply(ReplyMessage replyMessage) {
        try {
            lineMessagingClient.replyMessage(replyMessage).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void reply(String replyToken, Message message) {
        ReplyMessage replyMessage = new ReplyMessage(replyToken, message);
        reply(replyMessage);
    }

    public void reply(String replyToken, List<Message> message) {
        ReplyMessage replyMessage = new ReplyMessage(replyToken, message);
        reply(replyMessage);
    }

    // Mengirim pesan balasan
    public void replyText(String replyToken, String messageText) {
        TextMessage textMessage = new TextMessage(messageText);
        reply(replyToken, textMessage);
    }

    // Mengambil id user
    public UserProfileResponse getProfile (String userId) {
        try {
            return lineMessagingClient.getProfile(userId).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // Handle event yg masuk
    private void handleMessageEvent(MessageEvent event) {
        String replyToken      = event.getReplyToken();
        MessageContent content = event.getMessage();
        Source source          = event.getSource();
        String senderId        = source.getSenderId();
        sender                 = getProfile(senderId);

        // Handle pesan masuk berupa teks/bukan
        if(content instanceof TextMessageContent) {
            handleTextMessage(replyToken, (TextMessageContent) content, source);
        } else {
            invalidContent(replyToken, source, "Harap kirim pesan berupa teks yang valid!");
        }
    }

    // Handle pesan masuk jika berasal dari user/bukan
    private void handleTextMessage(String replyToken, TextMessageContent content, Source source) {
        if (source instanceof UserSource) {
            handleOneOnOneChats(replyToken, source, content.getText());
        } else {
            replyText(replyToken,"Sumber tidak diketahui!");
        }
    }

    // Menampilkan flex message utama ketika user menambahkan bot sebagai teman
    private void handleJointOrFollowEvent(String replyToken) {
        replyMainFlexMessage(replyToken);
    }

    // Mengembalikan user ke flex message utama
    private void handleFallbackMessage(String replyToken) {
        replyMainFlexMessage(replyToken);
    }

    // Handle pesan masuk dan membalasnya sesuai dengan keyword
    private void handleOneOnOneChats(String replyToken, Source source, String textMessage) {
        String msgText = textMessage.toLowerCase();

        if (msgText.contains("cek kota")) {
            replyFlexCity(replyToken);
        } else if (msgText.contains("keluar")) {
            handleFallbackMessage(replyToken);
        } else if (msgText.contains("kembali")) {
            handleFallbackMessage(replyToken);
        } else if (msgText.contains("lihat berita")) {
            replyFlexNews(replyToken);
        } else if (msgText.contains("video")) {
            replyFlexVideo(replyToken);
        } else if (msgText.contains("rumah sakit rujukan jakarta")) {
            replyFlexJakartaHospital(replyToken);
        } else if (msgText.contains("rumah sakit rujukan bogor")) {
            replyFlexBogorHospital(replyToken);
        } else if (msgText.contains("rumah sakit rujukan depok")) {
            replyFlexDepokHospital(replyToken);
        } else if (msgText.contains("rumah sakit rujukan tangerang")) {
            replyFlexTangerangHospital(replyToken);
        } else if (msgText.contains("rumah sakit rujukan bekasi")) {
            replyFlexBekasiHospital(replyToken);
        } else {
            invalidKeyword(replyToken, source, "Kata kunci \"" + msgText + "\" tidak valid!");
        }
    }

    // Jika user mengirim pesan dengan keyword yg tidak sesuai
    private void invalidKeyword(String replyToken, Source source, String invalidKeywordMessage) {
        if (sender == null) {
            String senderId = source.getSenderId();
            sender          = getProfile(senderId);
        }

        TemplateMessage bubbleOut = botTemplate.invalidKeyword(source, sender);

        if (invalidKeywordMessage != null) {
            List<Message> messages = new ArrayList<>();
            messages.add(new TextMessage(invalidKeywordMessage));
            messages.add(bubbleOut);
            reply(replyToken, messages);
        } else {
            reply(replyToken, bubbleOut);
        }
    }

    // Jika user mengirim pesan selain dalam bentuk teks (berupa file)
    private void invalidContent(String replyToken, Source source, String invalidContentMessage) {
        if (sender == null) {
            String senderId = source.getSenderId();
            sender          = getProfile(senderId);
        }

        TemplateMessage invalidContent = botTemplate.invalidContent(source, sender);

        if (invalidContentMessage != null) {
            List<Message> messages = new ArrayList<>();
            messages.add(new TextMessage(invalidContentMessage));
            messages.add(invalidContent);
            reply(replyToken, messages);
        } else {
            reply(replyToken, invalidContent);
        }
    }
}
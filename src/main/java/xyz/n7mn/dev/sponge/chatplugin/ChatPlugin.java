package xyz.n7mn.dev.sponge.chatplugin;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.inject.Inject;
import com.ibm.icu.text.Transliterator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStoppedEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextStyles;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Plugin(
        id = "chatplugin",
        name = "ChatPlugin",
        description = "ChatPlugin",
        url = "https://n7mn.xyz/",
        authors = {
                "n3m_"
        },
        version = "1.0"
)
public class ChatPlugin {

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("ChatPlugin Start");
    }

    @Listener
    public void onServerStop(GameStoppedEvent event){
        logger.info("ChatPlugin Stop");
    }

    @Listener
    public void onChat (MessageChannelEvent.Chat e){
        try {
            Text rawMessage = e.getRawMessage();
            String s = rawMessage.toPlain();

            Transliterator transliterator = Transliterator.getInstance("Latin-Hiragana");

            Gson gson = new Gson();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("http://www.google.com/transliterate?langpair=ja-Hira|ja&text=" + URLEncoder.encode(transliterator.transliterate(s), "UTF-8")).build();
            Response response = client.newCall(request).execute();

            String json = response.body().string();
            StringBuffer stringBuffer = new StringBuffer();
            JsonArray jsonElement = gson.fromJson(json, JsonArray.class);
            for (int i = 0; i < jsonElement.size(); i++){
                stringBuffer.append(jsonElement.get(i).getAsJsonArray().get(1).getAsJsonArray().get(0).getAsString());
            }

            Text build = Text.builder().append(Text.builder().append(Text.of(s + " (" +stringBuffer.toString() + ")")).build()).build();
            if (s.length() != 0) {
                e.setMessage(build);
            }

        } catch (UnsupportedEncodingException ex) {
            ex.fillInStackTrace();
        } catch (IOException ex) {
            ex.fillInStackTrace();
        }

    }
}

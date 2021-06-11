package api.shared;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FirebaseConfig {

    public static void initialize(){
        try{
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .setDatabaseUrl("https://spotify-server-4e6f2-default-rtdb.europe-west1.firebasedatabase.app/")
                    .build();

            FirebaseApp.initializeApp(options);

            System.out.println("FirebaseApp Initialized");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void sendMessage(HashMap<String, String> data, List<String> registrationTokens){
        System.out.println(data);
        System.out.println(registrationTokens);
        try{
            MulticastMessage message = MulticastMessage.builder()
                    .putAllData(data)
                    .addAllTokens(registrationTokens)
                    .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            System.out.println("Batch Message Sent! " + response.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getAccessToken() throws IOException {
//        System.out.println("ENV CREDS: " + System.getenv());
        InputStream stream = new ByteArrayInputStream(System.getenv("GOOGLE_APPLICATION_CREDENTIALS").getBytes(StandardCharsets.UTF_8));
        GoogleCredential googleCredential = GoogleCredential
                .fromStream(stream)
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));
        googleCredential.refreshToken();
        return googleCredential.getAccessToken();
    }

}

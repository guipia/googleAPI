import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.DocsScopes;
import com.google.api.services.docs.v1.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* class to demonstarte use of Docs get documents API */
public class DocsQuickstart {
    /** Application name. */
    private static final String APPLICATION_NAME = "Google Docs API Java Quickstart";
    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /** Directory to store authorization tokens for this application. */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static String DOCUMENT_ID = "195j9eDD3ccgjQRttHhJPymLJUCOUjs-jmwTrekvdjFE";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DocsScopes.DOCUMENTS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = DocsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
    }
    public static void setName(String name,String ti, String ques, String ans) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Docs service = new Docs.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        Document doc = new Document()
                .setTitle(name);
        doc = service.documents().create(doc)
                .execute();
        System.out.println("Created document with title: " + doc.getTitle());

        DOCUMENT_ID = doc.getDocumentId();




        List<Request> requests = new ArrayList<>();

        String question = ques;
        String answer = ans;
        String title = ti;

        requests.add(new Request().setInsertText(new InsertTextRequest()
                .setText(title)
                .setLocation(new Location().setIndex(1))));

        requests.add(new Request().setInsertInlineImage(new InsertInlineImageRequest()
                .setUri("https://fonts.gstatic.com/s/i/productlogos/docs_2020q4/v6/web-64dp/logo_docs_2020q4_color_1x_web_64dp.png")
                .setLocation(new Location().setIndex(title.length()+1))
                .setObjectSize(new Size()
                        .setHeight(new Dimension()
                                .setMagnitude(50.0)
                                .setUnit("PT"))
                        .setWidth(new Dimension()
                                .setMagnitude(50.0)
                                .setUnit("PT")))));

        requests.add(new Request().setInsertText(new InsertTextRequest()
                .setText(question)
                .setLocation(new Location().setIndex(title.length()+1))));

        requests.add(new Request().setUpdateTextStyle(new UpdateTextStyleRequest()
                .setTextStyle(new TextStyle()
                        .setBold(true)
                        .setItalic(false))
                .setRange(new Range()
                        .setStartIndex(title.length())
                        .setEndIndex(title.length() + question.length()))
                .setFields("bold")));

        requests.add(new Request().setInsertText(new InsertTextRequest()
                .setText(answer)
                .setLocation(new Location().setIndex(question.length() + title.length()+ 1))));


        BatchUpdateDocumentRequest body = new BatchUpdateDocumentRequest().setRequests(requests);
        BatchUpdateDocumentResponse response = service.documents()
                .batchUpdate(DOCUMENT_ID, body).execute();

    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        setName("New Name1","Title", "How are you?", "Im fine");


        //lets see



    }

}
package com.faber.googleapisheetproject;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ServiceAccountStart {

    private static final String CREDENTIALS_FILE_PATH = "D:\\Code hub\\GoogleAPISheetProject\\src\\main\\resources\\key.p12";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String SERVICE_ACCOUNT_EMAIL = "demoapi@cohesive-vine-271302.iam.gserviceaccount.com";

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "1K6ScAs5N-EPzbJZqgGaXdk9fD5H3eF4ELwREPblJu54";

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(getCredentials()))
                .setApplicationName("Tung Anh")
                .build();
        final String range = "Sheet1!A2:C19";
//        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredential(SERVICE_ACCOUNT_EMAIL))
//                .setApplicationName("Tung Anh")
//                .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("Id, Name, Test");
            for (List row : values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                System.out.printf("%s, %s, %s\n", row.get(0), row.get(1), row.get(2));
            }
        }

    }
    /*
     * for GoogleCredential deprecated
     * @param userEmail
     * @return
     * @throws GeneralSecurityException
     * @throws IOException 
     * 
     */
    public static GoogleCredential getCredential(String userEmail) throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
                .setServiceAccountScopes(Arrays.asList(SheetsScopes.SPREADSHEETS))
                .setServiceAccountPrivateKeyFromP12File(new java.io.File(CREDENTIALS_FILE_PATH))
                .build();

        return credential;
    }
    /*
     * for GoogleCredentials most udpated funciton using json
     * @return
     * @throws GeneralSecurityException
     * @throws IOException 
     */
    public static GoogleCredentials getCredentials() throws GeneralSecurityException, IOException {
        FileInputStream credentialsStream = new FileInputStream("D:\\Code hub\\GoogleAPISheetProject\\src\\main\\resources\\demo.json");
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(credentialsStream);
        credentials = credentials.createScoped(Arrays.asList(SheetsScopes.SPREADSHEETS));
        AccessToken accessToken = credentials.refreshAccessToken();
        return credentials;
    }
}

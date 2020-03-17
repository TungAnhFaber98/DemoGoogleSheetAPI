package com.faber.googleapisheetproject.demo;

//<editor-fold defaultstate="collapsed" desc="IMPORT">
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.UpdateSheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
//</editor-fold>

public class SheetsQuickstart {

    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart. If modifying
     * these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/client_secrets.json";

    //<editor-fold defaultstate="collapsed" desc="getCredential">
    /*
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8000).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    //</editor-fold>
    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     *
     * @param args
     * @throws java.io.IOException
     * @throws java.security.GeneralSecurityException
     */
//    public static void main(String... args) throws IOException, GeneralSecurityException {
//
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        final String spreadsheetId = "1K6ScAs5N-EPzbJZqgGaXdk9fD5H3eF4ELwREPblJu54";
//
//        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//
//        //<editor-fold defaultstate="collapsed" desc="Append new line to sheet">
//                String range = "Sheet1!A2:C13";
//        
//        ValueRange response = service.spreadsheets().values()
//                .get(spreadsheetId, range)
//                .execute();
//        
//        List<List<Object>> values = response.getValues();
//        if(values==null|| values.isEmpty()){
//            System.out.println("No data found.");
//        }
//        else{
//            for(List row:values){
//                System.out.printf("Id: %s,Name: %s,Email: %s\n",row.get(0),row.get(1),row.get(2));
//            }
//        }
//        
//        ValueRange appendbody = new ValueRange()
//                .setValues(Arrays.asList(Arrays.asList(13,"Tung","gae23@gmail.com")));
//        
//        AppendValuesResponse result = service.spreadsheets().values()
//                .append(spreadsheetId, "Sheet1", appendbody)
//                .setValueInputOption("USER_ENTERED")
//                .setInsertDataOption("INSERT_ROWS")
//                .setIncludeValuesInResponse(true)
//                .execute();
//        //</editor-fold>
//        //<editor-fold defaultstate="collapsed" desc="Add new sheet to spreadsheet">
////        AddSheetRequest addSheetRequest = new AddSheetRequest();
////        addSheetRequest.setProperties(new SheetProperties().setTitle("Thuan Tran 1"));
////        BatchUpdateSpreadsheetRequest batchUpdateSpreadsheetRequest = new BatchUpdateSpreadsheetRequest();
////        batchUpdateSpreadsheetRequest.setRequests(new ArrayList<>());
////        batchUpdateSpreadsheetRequest.getRequests().add(new Request().setAddSheet(addSheetRequest));
////
////        Sheets.Spreadsheets.BatchUpdate batchUpdateRequest = service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateSpreadsheetRequest);
////        batchUpdateRequest.execute();
//        //</editor-fold>
//        //<editor-fold defaultstate="collapsed" desc="UPDATE Sheet">
////        DeleteDimensionRequest deleteRequest = new DeleteDimensionRequest()
////                .setRange(
////                        new DimensionRange()
////                                .setSheetId(1156541392)
////                                .setDimension("ROWS")
////                                .setStartIndex(6)
////                );
////        List<Request> requests = new ArrayList<>();
////        requests.add(new Request().setDeleteDimension(deleteRequest));
////        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
////        service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
//        //</editor-fold>
//    }

}

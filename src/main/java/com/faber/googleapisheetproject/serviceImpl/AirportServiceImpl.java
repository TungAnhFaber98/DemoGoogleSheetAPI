package com.faber.googleapisheetproject.serviceImpl;

//<editor-fold defaultstate="collapsed" desc="IMPORT">
import com.faber.googleapisheetproject.controller.MainController;
import com.faber.googleapisheetproject.entity.Airport;
import com.faber.googleapisheetproject.service.AirportService;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateSheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.UpdateSpreadsheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
//</editor-fold>

@Service
public class AirportServiceImpl implements AirportService {

    //<editor-fold defaultstate="collapsed" desc="Fields and Variables">
    @Autowired
    Environment env;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final String spreadsheetId = "1K6ScAs5N-EPzbJZqgGaXdk9fD5H3eF4ELwREPblJu54";

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="getCredentialsJson">
    /**
     *
     * @return @throws GeneralSecurityException
     * @throws IOException
     */
    public GoogleCredentials getCredentialsJson() throws GeneralSecurityException, IOException {
        String CREDENTIALS_FILE_PATH = env.getProperty("CREDENTIALS_FILE_PATH");
        InputStream credentialsStream = AirportServiceImpl.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(credentialsStream);
        credentials = credentials.createScoped(Arrays.asList(SheetsScopes.SPREADSHEETS));
        return credentials;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getSheetService">
    /**
     * Get Sheet Service by Google Credentials
     *
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    private Sheets getSheetService() throws GeneralSecurityException, IOException {
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(getCredentialsJson()))
                .setApplicationName("Tung Anh")
                .build();
        return service;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="findAllAirport">
    /*
     * find all airport
     *
     * @return
     */
    @Override
    public List<Airport> findAllAirport() {
        ArrayList<Airport> airportList = new ArrayList<>();

        try {
            Sheets service = getSheetService();
            final String range = "Airport";

            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                return airportList;
            } else {
                values.remove(0);
                values.forEach((row) -> {
                    airportList.add(new Airport(row.get(0).toString(), row.get(1).toString(), row.get(2).toString()));
                });
            }
        } catch (IOException | GeneralSecurityException ex) {
            Logger.getLogger(AirportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return airportList;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="addAirport">
    /**
     * add new airport
     *
     * @param airport
     */
    @Override
    public void addAirport(Airport airport) {
        try {
            Sheets service = getSheetService();
            final String range = "Airport";

            ValueRange appendbody = new ValueRange()
                    .setValues(Arrays.asList(Arrays.asList(UUID.randomUUID().toString(), airport.getName(), airport.getLocation())));

            AppendValuesResponse result = service.spreadsheets().values()
                    .append(spreadsheetId, range, appendbody)
                    .setValueInputOption("RAW")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();
            LOGGER.info(result.toPrettyString());
        } catch (GeneralSecurityException | IOException ex) {
            Logger.getLogger(AirportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="deleteAirport">
    /**
     * Delete airport
     *
     * @param deleteId
     */
    @Override
    public void deleteAirport(String deleteId) {
        try {
            Sheets service = getSheetService();
            String range = "Airport!A2:A";
            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();

            List<String> listId = new ArrayList<>();
            if (values == null || values.isEmpty()) {
                LOGGER.error("No record found");
            } else {
                values.forEach((row) -> {
                    listId.add(row.get(0).toString());
                });
            }

            for (String id : listId) {
                if (id.equals(deleteId)) {
                    int rangeInList = listId.indexOf(id);
                    List<Request> requests = new ArrayList<>();
                    DimensionRange dimensionRange = new DimensionRange()
                            .setSheetId(2034205618)
                            .setDimension("ROWS")
                            .setStartIndex(rangeInList + 1)
                            .setEndIndex(rangeInList + 2);
                    requests.add(new Request().setDeleteDimension(new DeleteDimensionRequest().setRange(dimensionRange)));

                    BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
                    requestBody.setRequests(requests);

                    Sheets.Spreadsheets.BatchUpdate request = service.spreadsheets().batchUpdate(spreadsheetId, requestBody);
                    BatchUpdateSpreadsheetResponse deleteResponse = request.execute();
                    LOGGER.info(deleteResponse.toPrettyString());
                    break;
                }
            }
        } catch (IOException | GeneralSecurityException e) {
            LOGGER.error(e.getMessage());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getAirportById">
    /**
     * Get Airport By Id
     *
     * @param id
     * @return
     */
    @Override
    public Airport getAirportById(String id) {
        Airport airport = new Airport();
        try {
            Sheets service = getSheetService();
            String range = "Airport";
            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                return airport;
            } else {
                values.remove(0);
                for (List<Object> row : values) {
                    if (row.get(0).toString().equals(id)) {
                        airport = new Airport(row.get(0).toString(), row.get(1).toString(), row.get(2).toString());
                        return airport;
                    }
                }
            }

        } catch (IOException | GeneralSecurityException e) {
            LOGGER.error(e.getMessage());

        }
        return airport;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="updateAirport">
    /**
     * Update Airport
     *
     * @param airport
     */
    @Override
    public void updateAirport(Airport airport) {
        try {
            Sheets service = getSheetService();
            String searchRange = "Airport!A2:A";

            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheetId, searchRange)
                    .execute();

            List<List<Object>> values = response.getValues();
            List<String> listId = new ArrayList<>();

            if (values == null || values.isEmpty()) {
                LOGGER.error("No record found");
            } else {
                values.forEach((row) -> {
                    listId.add(row.get(0).toString());
                });
            }
            for (String id : listId) {
                if (id.equals(airport.getId())) {
                    LOGGER.info("Found");
                    int rangeInList = listId.indexOf(id) + 2;
                    String updateRange = "Airport!A" + rangeInList + ":C" + rangeInList;

                    ValueRange updatebody = new ValueRange()
                            .setValues(Arrays.asList(Arrays.asList(airport.getId(), airport.getName(), airport.getLocation())));

                    UpdateValuesResponse updateResponse = service.
                            spreadsheets()
                            .values()
                            .update(spreadsheetId, updateRange, updatebody)
                            .setValueInputOption("RAW")
                            .execute();
                    LOGGER.info(updateResponse.toPrettyString());
                    break;
                }
            }

        } catch (IOException | GeneralSecurityException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    //</editor-fold>
    public void test() {
        try {
            Sheets service = getSheetService();
            List<Request> requests = new ArrayList<>();

            UpdateSpreadsheetPropertiesRequest updateSpreadsheetPropertiesRequest = new UpdateSpreadsheetPropertiesRequest()
                    .setProperties(new SpreadsheetProperties().setTitle("Tung Teng Anh"))
                    .setFields("*");
            requests.add(new Request().setUpdateSpreadsheetProperties(updateSpreadsheetPropertiesRequest));
            
            UpdateSheetPropertiesRequest updateSheetPropertiesRequest = new UpdateSheetPropertiesRequest()
                    .setProperties(new SheetProperties().setSheetId(349813882).setTitle("Demo Batch Update"))
                    .setFields("*");
            requests.add(new Request().setUpdateSheetProperties(updateSheetPropertiesRequest));

            BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
            requestBody.setRequests(requests);

            Sheets.Spreadsheets.BatchUpdate request = service.spreadsheets().batchUpdate(spreadsheetId, requestBody);
            BatchUpdateSpreadsheetResponse response = request.execute();
            LOGGER.info(response.toPrettyString());
        } catch (IOException | GeneralSecurityException ex) {
            LOGGER.error(ex.getMessage());
        }
    }
}

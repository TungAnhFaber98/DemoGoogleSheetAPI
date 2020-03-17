/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.faber.googleapisheetproject.serviceImpl;

import com.faber.googleapisheetproject.controller.MainController;
import com.faber.googleapisheetproject.entity.Account;
import com.faber.googleapisheetproject.entity.Airport;
import com.faber.googleapisheetproject.service.AirportService;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 *
 * @author Engineer_Account
 */
@Service
public class AirportServiceImpl implements AirportService {
    
    @Autowired
    Environment env;
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

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
    
    @Override
    public List<Airport> findAllAirport() {
        ArrayList<Airport> airportList = new ArrayList<>();
        
        try {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            String spreadsheetId = "1K6ScAs5N-EPzbJZqgGaXdk9fD5H3eF4ELwREPblJu54";
            
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(getCredentialsJson()))
                    .setApplicationName("Tung Anh")
                    .build();
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
                    LOGGER.info(row+"");
                    airportList.add(new Airport(Integer.valueOf(row.get(0).toString()), row.get(1).toString(), row.get(2).toString()));
                });
            }
        } catch (Exception ex) {
            Logger.getLogger(AirportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return airportList;
    }
    
    @Override
    public void addAirport(Airport airport) {
        try {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            String spreadsheetId = "1K6ScAs5N-EPzbJZqgGaXdk9fD5H3eF4ELwREPblJu54";
            
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(getCredentialsJson()))
                    .setApplicationName("Tung Anh")
                    .build();
            final String range = "Airport";
            
            ValueRange appendbody = new ValueRange()
                    .setValues(Arrays.asList(Arrays.asList(airport.getId(), airport.getName(), airport.getLocation())));
            
            AppendValuesResponse result = service.spreadsheets().values()
                    .append(spreadsheetId, range, appendbody)
                    .setValueInputOption("RAW")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();
            
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(AirportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AirportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

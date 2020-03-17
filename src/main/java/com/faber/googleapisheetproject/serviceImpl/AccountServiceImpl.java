package com.faber.googleapisheetproject.serviceImpl;

//<editor-fold defaultstate="collapsed" desc="IMPORT">
import com.faber.googleapisheetproject.entity.Account;
import com.faber.googleapisheetproject.service.AccountService;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

//</editor-fold>
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    Environment env;
    
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * 
     * @return
     * @throws GeneralSecurityException
     * @throws IOException 
     */
    public  GoogleCredentials getCredentialsJson() throws GeneralSecurityException, IOException {
        String CREDENTIALS_FILE_PATH = env.getProperty("CREDENTIALS_FILE_PATH");
        InputStream credentialsStream = AccountServiceImpl.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(credentialsStream);
        credentials = credentials.createScoped(Arrays.asList(SheetsScopes.SPREADSHEETS));
        return credentials;
    }

    /*
     * 
     * @param account
     * @return 
     */
    @Override
    public boolean checkAccount(Account account) {
        try {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            String spreadsheetId = "1K6ScAs5N-EPzbJZqgGaXdk9fD5H3eF4ELwREPblJu54";

            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpCredentialsAdapter(getCredentialsJson()))
                    .setApplicationName("Tung Anh")
                    .build();
            final String range = "Account";

            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            List<Account> listAccount = new ArrayList<>();

            if (values == null || values.isEmpty()) {
                System.out.println("No data found.");
                return false;
            } else {
                values.remove(0);
                values.forEach((row) -> {
                    listAccount.add(new Account(row.get(0).toString(), row.get(1).toString()));
                });
            }
            for (Account acc : listAccount) {
                if (acc.getUsername().equalsIgnoreCase(acc.getUsername()) && acc.getPassword().equals(account.getPassword())) {
                    return true;
                }
            };
        } catch (GeneralSecurityException | IOException ex) {
            Logger.getLogger(AccountServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

}

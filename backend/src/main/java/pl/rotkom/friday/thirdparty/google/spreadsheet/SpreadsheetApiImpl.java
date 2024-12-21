package pl.rotkom.friday.thirdparty.google.spreadsheet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import pl.rotkom.friday.thirdparty.google.GoogleConfig;
import pl.rotkom.friday.thirdparty.google.common.GoogleCommons;
import pl.rotkom.friday.thirdparty.google.oauth2.GoogleOauth2Manager;
import pl.rotkom.friday.thirdparty.google.spreadsheet.api.service.ISpreadsheetApi;


@Component
class SpreadsheetApiImpl implements ISpreadsheetApi {

    private static final String SPREADSHEET_ID = "1_MONBpEoFyKXZj3chgufRi0W_BvCCePhTtV7wIrQBEc";

    private final Sheets sheets;

    public SpreadsheetApiImpl(GoogleOauth2Manager oauth2Manager, GoogleConfig config) {
        this.sheets = new Sheets.Builder(GoogleCommons.HTTP_TRANSPORT, GoogleCommons.JSON_FACTORY, oauth2Manager.getCredentials())
                .setApplicationName(config.getProjectId())
                .build();
    }

    @Override
    public ValueRange getValues(String sheetName) {
        try {
            return this.sheets.spreadsheets().values().get(SPREADSHEET_ID, sheetName).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void appendValues(String sheetName, ValueRange valueRange) {
        try {
            this.sheets.spreadsheets().values().append(SPREADSHEET_ID, sheetName, valueRange)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getColumnValues(String sheetName, int column) {
        return this.getValues(sheetName).getValues().stream()
                .map(row -> row.get(column).toString())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getIds(String sheetName) {
        return this.getColumnValues(sheetName, 0);
    }

    @Override
    public List<String> retainNewIds(String sheetName, List<String> ids) {
        var oldIds = this.getIds(sheetName);
        var newIds = new ArrayList<>(ids);
        newIds.removeAll(oldIds);
        return newIds;
    }
}

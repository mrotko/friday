package pl.rotkom.friday.thirdparty.google.spreadsheet.api.service;

import java.util.List;

import com.google.api.services.sheets.v4.model.ValueRange;

public interface ISpreadsheetApi {

    ValueRange getValues(String sheetName);

    void appendValues(String sheetName, ValueRange valueRange);

    List<String> getColumnValues(String sheetName, int column);

    List<String> getIds(String sheetName);

    List<String> retainNewIds(String sheetName, List<String> ids);
}

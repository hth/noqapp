package com.noqapp.service.report.pdf;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.json.JsonQueuePersonList;
import com.noqapp.domain.json.JsonQueuedPerson;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.datatable.DataTable;
import be.quodlibet.boxable.line.LineStyle;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * hitender
 * 9/29/20 9:54 AM
 */
public class PeopleInQueue extends PdfBoxBase {
    private static final Logger LOG = LoggerFactory.getLogger(PeopleInQueue.class);

    private JsonQueuePersonList jsonQueuePersonList;
    private BizStoreEntity bizStore;

    public File generateReport() {
        try {
            String title = bizStore.getDisplayName().replace(" ", "_");
            PDDocument document = createPDF(PDRectangle.A4, title);
            return generatePDF(document);
        } catch (IOException e) {
            LOG.error("Failed generating report reason={}", e.getLocalizedMessage(), e);
            return null;
        }
    }

    @Override
    void populateReport(PDDocument document, PDPage page) {
        try {
            if (0 != jsonQueuePersonList.getQueuedPeople().size()) {
                createTable(document, page, bizStore.getDisplayName());
            } else {
                markAsBlankPage(document, page);
            }
        } catch (IOException e) {
            LOG.error("Failed populating report reason={}", e.getLocalizedMessage(), e);
        }
    }

    public void createTable(PDDocument document, PDPage page, String tableTitle) throws IOException {
        List<List> data = new ArrayList<>();
        data.add(
            new ArrayList<>(
                Arrays.asList(
                    "Token",
                    "Name",
                    "Token",
                    "Name",
                    "Token",
                    "Name")));

        int count = 0;
        String c1 = "";
        String c2 = "";
        String c3 = "";
        String c4 = "";
        String c5;
        String c6;

        List<JsonQueuedPerson> sorted = jsonQueuePersonList.getQueuedPeople().stream()
            .sorted(Comparator.comparing(JsonQueuedPerson::getToken))
            .collect(Collectors.toList());

        for (JsonQueuedPerson jsonQueuedPerson : sorted) {
            count++;

            if (StringUtils.isBlank(c1)) {
                c1 = jsonQueuedPerson != null ? jsonQueuedPerson.getDisplayToken() : "";
                c2 = jsonQueuedPerson != null ? jsonQueuedPerson.getCustomerName() : "";
            } else if (StringUtils.isNotBlank(c1) && StringUtils.isBlank(c3)) {
                c3 = jsonQueuedPerson != null ? jsonQueuedPerson.getDisplayToken() : "";
                c4 = jsonQueuedPerson != null ? jsonQueuedPerson.getCustomerName() : "";
            } else if (count % 3 == 0) {
                c5 = jsonQueuedPerson != null ? jsonQueuedPerson.getDisplayToken() : "";
                c6 = jsonQueuedPerson != null ? jsonQueuedPerson.getCustomerName() : "";

                data.add(new ArrayList<>(Arrays.asList(c1, c2, c3, c4 ,c5, c6)));
                c1 = ""; c2 = ""; c3 = ""; c4 = "";
            }
        }

        if (StringUtils.isNotBlank(c1) && StringUtils.isNotBlank(c3)) {
            data.add(new ArrayList<>(Arrays.asList(c1, c2, c3, c4, "", "")));
        } else if (StringUtils.isNotBlank(c1)) {
            data.add(new ArrayList<>(Arrays.asList(c1, c2, "", "", "", "")));
        }

        //Initialize table
        float margin = PADDING_TOP_OF_DOCUMENT + 5;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = PADDING_BOTTOM_OF_DOCUMENT + 5;

        BaseTable dataTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, document, page, true, true);
        DataTable t = new DataTable(dataTable, page);
        Row<PDPage> headerRow = dataTable.createRow(15f);

        Cell<PDPage> cell = headerRow.createCell(100, tableTitle);
        cell.setFont(PDType1Font.HELVETICA_BOLD);
        cell.setFillColor(Color.WHITE);
        cell.setTextColor(Color.BLACK);
        cell.setBottomBorderStyle(new LineStyle(Color.black, 1));
        dataTable.addHeaderRow(headerRow);

        t.addListToTable(data, DataTable.HASHEADER);
        dataTable.draw();
    }

    public JsonQueuePersonList getJsonQueuePersonList() {
        return jsonQueuePersonList;
    }

    public PeopleInQueue setJsonQueuePersonList(JsonQueuePersonList jsonQueuePersonList) {
        this.jsonQueuePersonList = jsonQueuePersonList;
        return this;
    }

    public BizStoreEntity getBizStore() {
        return bizStore;
    }

    public PeopleInQueue setBizStore(BizStoreEntity bizStore) {
        this.bizStore = bizStore;
        return this;
    }
}

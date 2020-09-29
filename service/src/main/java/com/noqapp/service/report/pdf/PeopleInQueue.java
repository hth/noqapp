package com.noqapp.service.report.pdf;

import com.noqapp.domain.BizStoreEntity;
import com.noqapp.domain.json.JsonQueuePersonList;
import com.noqapp.domain.json.JsonQueuedPerson;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.datatable.DataTable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                createTable(document, page);
            } else {
                markAsBlankPage(document, page);
            }
        } catch (IOException e) {
            LOG.error("Failed populating report reason={}", e.getLocalizedMessage(), e);
        }
    }

    public void createTable(PDDocument document, PDPage page) throws IOException {
        //Initialize table
        float margin = PADDING_TOP_OF_DOCUMENT + 5;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = PADDING_BOTTOM_OF_DOCUMENT + 5;


        List<List> data = new ArrayList<>();
        data.add(
            new ArrayList<>(
                Arrays.asList(
                    "Token",
                    "Name",
                    "Token",
                    "Name")));

        int count = 0;
        String c1 = null;
        String c2 = null;
        for (JsonQueuedPerson jsonQueuedPerson : jsonQueuePersonList.getQueuedPeople()) {
            if (count % 2 == 0) {
                data.add(
                    new ArrayList<>(
                        Arrays.asList(
                            c1,
                            c2,
                            jsonQueuedPerson != null ? jsonQueuedPerson.getDisplayToken() : "",
                            jsonQueuedPerson != null ? jsonQueuedPerson.getCustomerName() : "")));
            } else {
                c1 = jsonQueuedPerson != null ? jsonQueuedPerson.getDisplayToken() : "";
                c2 = jsonQueuedPerson != null ? jsonQueuedPerson.getCustomerName() : "";
            }
            count++;
        }

        BaseTable dataTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, document, page, true, true);
        DataTable t = new DataTable(dataTable, page);
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

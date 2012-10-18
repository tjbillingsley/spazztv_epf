/**
 * 
 */
package com.spazztv.epf;

import java.io.FileNotFoundException;

import com.spazztv.epf.dao.EPFDbException;
import com.spazztv.epf.dao.EPFDbWriter;

/**
 * 
 * @author Thomas Billingsley
 * 
 */
public class EPFImportTask implements Runnable {

	EPFImportTranslator importTranslator;
	EPFDbWriter dbWriter;
	long recordCount = 0;

	public EPFImportTask(String filePath, EPFDbWriter dbWriter)
			throws FileNotFoundException, EPFFileFormatException {
		importTranslator = new EPFImportTranslator(new EPFFileReader(filePath));
		this.dbWriter = dbWriter;
	}

	@Override
	public void run() {
		try {
			setupImportDataStore();
			importData();
			finalizeImport();
		} catch (EPFDbException e) {
			e.printStackTrace();
		}
	}

	public void setupImportDataStore() throws EPFDbException {
		dbWriter.initImport(importTranslator.getExportType(),
				importTranslator.getTableName(),
				importTranslator.getColumnAndTypes(),
				importTranslator.getTotalDataRecords());
	}

	public void importData() throws EPFDbException {
		try {
			while (importTranslator.hasNextRecord()) {
				recordCount++;
				dbWriter.insertRow(importTranslator.nextRecord());
			}
			if (recordCount != importTranslator.getTotalDataRecords()) {
				throw new EPFDbException(
						String.format(
								"Incorrect number of import records. Expecting %d, found %d",
								importTranslator.getTotalDataRecords(),
								recordCount));
			}
		} catch (EPFDbException e) {
			EPFImporterQueue.getInstance().setFailed(
					importTranslator.getFilePath());
			throw e;
		}
	}

	public void finalizeImport() throws EPFDbException {
		dbWriter.finalizeImport();
		EPFImporterQueue.getInstance()
				.setCompleted(importTranslator.getFilePath());
	}
}
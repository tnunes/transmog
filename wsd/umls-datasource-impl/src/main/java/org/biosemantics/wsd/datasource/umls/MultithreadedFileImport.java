package org.biosemantics.wsd.datasource.umls;

import java.io.File;
import java.io.IOException;

import org.springframework.core.task.TaskExecutor;

public class MultithreadedFileImport {

	private File folder;
	private TaskExecutor taskExecutor;
	private GraphFileImporter graphFileImporter;

	public MultithreadedFileImport(TaskExecutor taskExecutor, GraphFileImporter graphFileImporter) {
		this.taskExecutor = taskExecutor;
		this.graphFileImporter = graphFileImporter;
	}

	public void setFolder(File folder) {
		this.folder = folder;
		if (!this.folder.exists()) {
			throw new IllegalArgumentException("does not exist");
		}
		if (!this.folder.isDirectory()) {
			throw new IllegalArgumentException("not a folder");
		}
	}

	public void fire() {
		File[] files = folder.listFiles();
		for (final File file : files) {
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						graphFileImporter.parseFileAndImport(file, ENCODING);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	private static final String ENCODING = "UTF-8";
}

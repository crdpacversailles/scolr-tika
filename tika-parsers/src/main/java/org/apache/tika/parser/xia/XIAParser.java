package org.apache.tika.parser.xia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.xml.DcXMLParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import SevenZip.ArchiveExtractCallback;
import SevenZip.HRESULT;
import SevenZip.MyRandomAccessFile;
import SevenZip.Archive.IArchiveExtractCallback;
import SevenZip.Archive.IInArchive;
import SevenZip.Archive.SevenZipEntry;
import SevenZip.Archive.SevenZip.Handler;

/**
 * UBZ parser (Sankore interactive white board free software) Looks for metadata
 * in the RDF file and for text and educational informations in svg slides
 * 
 */
public class XIAParser extends AbstractParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -784304924718735685L;

	private static final Set<MediaType> SUPPORTED_TYPES = Collections
			.singleton(MediaType.application("x-images-actives+zip"));

	private static final String MAIN_SVG_FILE_NAME = "pictures/content.svg";

	private Parser meta = new DcXMLParser();

	private Parser content = new XIAContentParser();

	private IInArchive archive;

	public Parser getMetaParser() {
		return meta;
	}

	public void setMetaParser(Parser meta) {
		this.meta = meta;
	}

	public Parser getContentParser() {
		return content;
	}

	public void setContentParser(Parser content) {
		this.content = content;
	}

	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return SUPPORTED_TYPES;
	}

	public void parse(InputStream stream, ContentHandler handler,
			Metadata metadata, ParseContext context) throws IOException,
			SAXException, TikaException {
		File tempFile = dumpStreamToFile(stream);
		File svgFile = extractSVGFileFrom7zContainer(tempFile);
		InputStream is = new FileInputStream(svgFile);
		content.parse(is, handler, metadata, context);
		svgFile.delete();
		archive.close();
		metadata.set(Metadata.CONTENT_TYPE, "application/x-images-actives+zip");

	}

	private File extractSVGFileFrom7zContainer(File tempFile)
			throws TikaException {
		MyRandomAccessFile istream;
		try {
			istream = new MyRandomAccessFile(tempFile.getAbsolutePath(), "r");
		} catch (IOException e1) {
			throw new TikaException(" Unable to open xia file "
					+ tempFile.getAbsolutePath());
		}

		archive = new Handler();

		int ret = 0;
		try {
			ret = archive.Open(istream);
		} catch (IOException e1) {
			throw new TikaException(" Unable to extract xia file "
					+ tempFile.getAbsolutePath());
		}

		if (ret != 0) {
			throw new TikaException("Unable to open stream");
		}

		ArchiveExtractCallback extractCallbackSpec = new ArchiveExtractCallback();
		IArchiveExtractCallback extractCallback = extractCallbackSpec;
		extractCallbackSpec.Init(archive);

		try {
			int numberOfEntriesFound = 0;
			int arrays[] = new int[1];

			for (int i = 0; i < archive.size(); i++) {
				if (MAIN_SVG_FILE_NAME.equals(archive.getEntry(i).getName())) {
					arrays[numberOfEntriesFound++] = i;
				}
			}
			int extractionResult;

			int mode = IInArchive.NExtract_NAskMode_kExtract;
			if (numberOfEntriesFound == 0)
				throw new TikaException(
						" We didn't find content.svg in this xia file, abort");
			String property = "java.io.tmpdir";
			String tempDir = System.getProperty(property) + "/"
					+ UUID.randomUUID();
			extractionResult = archive.Extract(arrays, numberOfEntriesFound,
					mode, extractCallback, tempDir);

			if (extractionResult == HRESULT.S_OK) {
				if (extractCallbackSpec.NumErrors == 0) {
					SevenZipEntry item = archive.getEntry(arrays[0]);
					String filePath = item.getName();
					File file = new File(tempDir + "/" + filePath);
					return file;
				} else
					throw new TikaException(
							" There are errors while extracting xia "
									+ extractCallbackSpec.NumErrors + " errors");
			} else {
				throw new TikaException(
						"Extraction errors while trying to extract xia file");
			}
		} catch (java.io.IOException e) {
			throw new TikaException("IO error : " + e.getLocalizedMessage());
		}
	}

	private File dumpStreamToFile(InputStream stream) throws IOException {
		File tempFile = File.createTempFile("_tikaxia_", "_");
		OutputStream out = new FileOutputStream(tempFile);
		byte buf[] = new byte[1024];
		int len;
		while ((len = stream.read(buf)) > 0)
			out.write(buf, 0, len);
		out.close();
		return tempFile;
	}
}

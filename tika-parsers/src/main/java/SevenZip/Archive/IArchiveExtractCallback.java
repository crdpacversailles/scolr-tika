package SevenZip.Archive;

import java.io.FileInputStream;

public interface IArchiveExtractCallback extends SevenZip.IProgress {
    
    // GetStream OUT: S_OK - OK, S_FALSE - skip this file
	//HACK
    int GetStream(int index, java.io.OutputStream [] outStream,  int askExtractMode, String tempFileDirPath) throws java.io.IOException;
    FileInputStream getFileInputStream(int index);
    
    int PrepareOperation(int askExtractMode);
    int SetOperationResult(int resultEOperationResult) throws java.io.IOException;
}

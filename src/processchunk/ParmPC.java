/**
 * 
 */
package processchunk;

import batchreplace.Logger;
import filemng.FileTemplate;

/**
 * @author Administrator
 *
 */
public class ParmPC implements Cloneable {
    private byte[] _chunk;
    private int _szChunk;
    private FileTemplate _inFileTmpl;
    private Logger _logger;
    private boolean _wholeWordSearch;

    public ParmPC() {
    }

    public ParmPC(ParmPC objSrc) {
        this.set_chunk(objSrc.get_chunk());
        this.set_inFileTmpl(objSrc.get_inFileTmpl());
        this.set_logger(objSrc.get_logger());
        this.set_szChunk(objSrc.get_szChunk());
        this.set_wholeWordSearch(objSrc.is_wholeWordSearch());
    }

    public byte[] get_chunk() {
        return _chunk;
    }

    public void set_chunk(byte[] _chunk) {
        this._chunk = _chunk;
    }

    public int get_szChunk() {
        return _szChunk;
    }

    public void set_szChunk(int _szChunk) {
        this._szChunk = _szChunk;
    }

    public FileTemplate get_inFileTmpl() {
        return _inFileTmpl;
    }

    public void set_inFileTmpl(FileTemplate _inFileTmpl) {
        this._inFileTmpl = _inFileTmpl;
    }

    public Logger get_logger() {
        return _logger;
    }

    public void set_logger(Logger _logger) {
        this._logger = _logger;
    }

    public void set_wholeWordSearch(boolean is_wholeWordSearch) {
        this._wholeWordSearch = is_wholeWordSearch;
    }

    public boolean is_wholeWordSearch() {
        return this._wholeWordSearch;
    }
}

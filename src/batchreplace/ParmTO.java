/**
 * 
 */
package batchreplace;

/**
 * @author Administrator
 *
 */
public class ParmTO {
    private int     _maxChunkSz,
                    _maxThdQty;

    private String  inFileName,
                    templFileName,
                    outFileName;

    private byte    separator[];

    private Logger  _logger;

    private boolean _wholeWordSearch;

    public int get_maxChunkSz() {
        return _maxChunkSz;
    }

    public void set_maxChunkSz(int _maxChunkSz) {
        this._maxChunkSz = _maxChunkSz;
    }

    public int get_maxThdQty() {
        return _maxThdQty;
    }

    public void set_maxThdQty(int _maxThdQty) {
        this._maxThdQty = _maxThdQty;
    }

    public String getInFileName() {
        return inFileName;
    }

    public void setInFileName(String inFileName) {
        this.inFileName = inFileName;
    }

    public String getTemplFileName() {
        return templFileName;
    }

    public void setTemplFileName(String templFileName) {
        this.templFileName = templFileName;
    }

    public String getOutFileName() {
        return outFileName;
    }

    public void setOutFileName(String outFileName) {
        this.outFileName = outFileName;
    }

    public byte[] getSeparator() {
        return separator;
    }

    public void setSeparator(byte separator[]) {
        this.separator = separator;
    }

    protected Logger get_logger() {
        return _logger;
    }

    protected void set_logger(Logger _logger) {
        this._logger = _logger;
    }

    public boolean is_wholeWordSearch() {
        return _wholeWordSearch;
    }

    public void set_wholeWordSearch(boolean _wholeWordSearch) {
        this._wholeWordSearch = _wholeWordSearch;
    }
}

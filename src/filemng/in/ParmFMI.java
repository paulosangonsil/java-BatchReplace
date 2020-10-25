/**
 * 
 */
package filemng.in;

/**
 * @author Administrator
 *
 */
public class ParmFMI {
    private String _fileName    = "stdin.txt";

    private int _maxBufferSz    = 1 * (1024 * 1024);

    // '\n' is the default end marker
    private byte _endMarker[]   = String.format("%n").getBytes();

    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String fileName) {
        this._fileName = fileName;
    }

    public int getMaxBufferSz() {
        return _maxBufferSz;
    }

    public void setMaxBufferSz(int _maxBufferSz) {
        this._maxBufferSz = _maxBufferSz;
    }

    public byte[] getEndMarker() {
        return _endMarker;
    }

    public void setEndMarker(byte _endMarker[]) {
        this._endMarker = _endMarker;
    }
}

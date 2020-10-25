package filemng.in;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import batchreplace.KPM;

public class FileMngIn {
    private int     _lastChunkSz;

    private byte[]    internBuff;

    private BufferedInputStream is  = null;

    private FileInputStream fis = null;

    private ParmFMI _parmIn;

    private boolean _fileBigger;

    private void _initClass(ParmFMI parmIn) throws IOException {
        int maxBufferSz = parmIn.getMaxBufferSz();

        fis = new FileInputStream(parmIn.getFileName());

        is = new BufferedInputStream(fis, maxBufferSz);

        int szFile = is.available();

        if (szFile < 1) {
            throw new IOException("Zero bytes sized file");
        }
        else if (szFile <= maxBufferSz) {
            maxBufferSz = szFile;
        }
        else {
            this.setFileBigger(true);
        }

        this.setBuffer(new byte[maxBufferSz]);

        this.setParmIn(parmIn);
    }

    public FileMngIn(ParmFMI parmIn) throws IOException {
        this._initClass(parmIn);
    }

    public int get_lastChunkSz() {
        return _lastChunkSz;
    }

    private void set_lastChunkSz(int _lastChunkSz) {
        this._lastChunkSz = _lastChunkSz;
    }

    protected ParmFMI getParmIn() {
        return _parmIn;
    }

    protected void setParmIn(ParmFMI _parmIn) {
        this._parmIn = _parmIn;
    }

    public byte[] getBuffer() {
        return internBuff;
    }

    public void setBuffer(byte internBuff[]) {
        this.internBuff = internBuff;
    }

    public byte[] getChunk() throws IOException {
        int read = is.read(this.getBuffer());

        byte[] mtdRet   = null;

        if (read != -1) {
            if (getBuffer().length - read > 0) {
                Arrays.fill(getBuffer(), read, getBuffer().length, (byte) 0);
            }

            mtdRet = this.getParmIn().getEndMarker();

            if ( (mtdRet != null) && this.isFileBigger() ) {
                int posFound = KPM.lastIndexOf(this.getBuffer(), mtdRet);

                if (posFound != -1) {
                    read = posFound + mtdRet.length;

                    is.skip( - (this.getBuffer().length - read) );
                }
            }

            set_lastChunkSz(read);

            mtdRet = this.getBuffer();
        }

        return mtdRet;
    }

    protected boolean isFileBigger() {
        return _fileBigger;
    }

    protected void setFileBigger(boolean _fileBigger) {
        this._fileBigger = _fileBigger;
    }
}

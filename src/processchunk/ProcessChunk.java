package processchunk;

//import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import batchreplace.KPM;

public class ProcessChunk extends Thread {
    private int _buffOutSz;

    private boolean _foundKey;

    private ByteBuffer  _buffOut;

    private ParmPC _parmsIn;

    public ProcessChunk(ParmPC parms) {
        this.setParmsIn(parms);
    }

    protected void setParmsIn(ParmPC parms) {
        this._parmsIn = parms;
    }

    protected ParmPC getParmsIn() {
        return this._parmsIn;
    }

    public boolean is_foundKey() {
        return _foundKey;
    }

    protected void set_foundKey(boolean _foundKey) {
        this._foundKey = _foundKey;
    }

    public int get_buffOutSz() {
        return _buffOutSz;
    }

    protected void set_buffOutSz(int _buffOutSz) {
        this._buffOutSz = _buffOutSz;
    }

    public ByteBuffer get_buffOut() {
        return _buffOut;
    }

    protected void set_buffOut(ByteBuffer _buffOut) {
        this._buffOut = _buffOut;
    }

    public void run(){
        _doExecute();
    }

    /**
     * 
     */
    private void _doExecute() {
        ByteBuffer  buffIn;

        byte[]      buffInArr   = getParmsIn().get_chunk();

        int         buffInSz,
                    buffOutSz   = 0;

        HashMap<byte[], byte[]> buffTmpl    = getParmsIn().get_inFileTmpl().getMap();

        try {
            buffInSz = getParmsIn().get_szChunk();

            buffIn = ByteBuffer.wrap(buffInArr);

            // The first visit is to know the exactly buffer size after the replace operation
            // The second, the one executes the procedure
            for (int iterCnt = 0; iterCnt < 2; ++iterCnt) {
                if (iterCnt == 1) {
                    if ( (get_buffOut() == null) ||
                        ( buffInSz > get_buffOut().limit() ) ) {
                        set_buffOut(ByteBuffer.allocate( Math.max( buffInSz, getParmsIn().get_szChunk() ) ) );
                    }

                    buffIn = ByteBuffer.allocate( get_buffOut().limit() );

                    System.arraycopy(buffInArr, 0, buffIn.array(), 0, getParmsIn().get_szChunk());

                    buffInSz = getParmsIn().get_szChunk();
                }

                // Search each key in the given array
                for ( byte[] currKey : buffTmpl.keySet() ) {
                    int posFound,
                        lastPosFound    = -1,
                        buffPosIn       = 0,
                        szDiff          = 0;

                    // The search should go on till no key is found in the byte array
                    do {
                        posFound = KPM.indexOf(buffIn.array(), buffPosIn,
                                (iterCnt == 0) ? buffIn.limit() : buffInSz, currKey);

                        if ( (posFound > 0) && ( getParmsIn().is_wholeWordSearch() ) ) {
                            if ( Character.isLetterOrDigit((char) buffIn.array()[posFound - 1]) ||
                                    Character.isLetterOrDigit((char) buffIn.array()[posFound + currKey.length]) ) {
                                posFound = -1;
                            }
                        }

                        if (posFound != -1) {
                            byte[] buffRepl = buffTmpl.get(currKey);

                            lastPosFound = posFound;

                            set_foundKey(true);

                            szDiff = posFound - buffPosIn;

                            // Copy the previous chunk till the found position
                            if (iterCnt == 1) {
                                /*get_logger().println("Found " + new String(currKey, "UTF-8") +
                                                    " to be replaced " + new String(buffRepl, "UTF-8"));*/

                                if (szDiff > 0) {
                                    get_buffOut().put(buffIn.array(), buffPosIn, szDiff);
                                }

                                buffOutSz = get_buffOut().position();
                            }

                            szDiff = buffRepl.length - currKey.length;

                            // Just sum the difference in the buffer size
                            if (iterCnt == 0) {
                                buffInSz += szDiff;
                            }
                            // Replace the value in the original buffer
                            else if (iterCnt == 1) {
                                get_buffOut().put(buffRepl);

                                buffOutSz += buffRepl.length;
                            }

                            buffPosIn = posFound + currKey.length;
                        }
                    }
                    while (posFound != -1);

                    if ( (iterCnt == 1) && (lastPosFound != -1) ) {
                        szDiff = lastPosFound + currKey.length;

                        // If there's change in the 'in' buffer, copy
                        // the entire post found position to the new one
                        if (szDiff > 0) {
                            int len     = buffInSz - szDiff,
                                remain  = get_buffOut().remaining();

                            if (remain > 0) {
                                get_buffOut().put(buffIn.array(), buffPosIn, len);

                                buffOutSz += len;
                            }

                            get_buffOut().rewind();

                            System.arraycopy(get_buffOut().array(), 0,
                                                buffIn.array(), 0, buffOutSz);

                            buffInSz = buffOutSz;
                        }
                    }
                }
            }

            set_buffOut(buffIn);
            set_buffOutSz( (buffOutSz != 0) ? buffOutSz : buffInSz );
        }/* catch (IOException e) {
            get_logger().println("Problema no processamento do arquivo...");

            e.printStackTrace();

            return;
        }*/
        finally {
        }
    }
}

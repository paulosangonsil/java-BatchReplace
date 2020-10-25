/**
 * 
 */
package batchreplace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import filemng.FileTemplate;
import filemng.in.FileMngIn;
import filemng.in.ParmFMI;
import filemng.out.FileMngOut;
import processchunk.ParmPC;
import processchunk.ProcessChunk;

/**
 * @author Administrator
 *
 */
public class BatchReplace {
    private int _threadQty;

    private Logger _logger;

    protected int get_threadQty() {
        return _threadQty;
    }

    /**
     * 
     * @param _threadQty
     */
    protected void set_threadQty(int _threadQty) {
        this._threadQty = _threadQty;
    }

    protected Logger getLogger() {
        return _logger;
    }

    protected void setLogger(Logger logger) {
        this._logger = logger;
    }

    /**
     * 
     */
    private void _defineWrkThdQty() {
        int machProcs = Runtime.getRuntime().availableProcessors();

        if (machProcs > 1) {
            machProcs -= 1;
        }

        this.set_threadQty(machProcs);
    }

    private void _initClass(ParmTO parms) {
        if (parms.get_maxChunkSz() == 0) {
            parms.set_maxChunkSz( 1 * (1024 * 1024) );
        }

        if (parms.get_maxThdQty() != 0) {
            this.set_threadQty( parms.get_maxThdQty() );
        }

        this.setLogger(parms.get_logger());
    }

    private static void _printCmdLineUsage(Logger logger) {
        logger.println("Command line use: BatchReplace [-w] <source_file> <template_file> [<output_file>] [<separator_token>]");
        logger.println("<template_file> layout must be: <what_to_search><tab><replace_with_this><nl>");
    }

    private void _printCmdLineUsage() {
        _printCmdLineUsage(getLogger());
    }

    /**
     * 

     * @param timeStart
     * @param timeEnd
     * @return
     */
    private static String _formatProcessTime(long timeStart, long timeEnd) {
        String mtdRet   = "%.2f ";

        double timeElapsed = timeEnd - timeStart;

        if (timeElapsed > 1000) {
            timeElapsed /= 1000;

            if (timeElapsed > 59) {
                timeElapsed /= 60;

                if (timeElapsed > 59) {
                    mtdRet += "h";
                }
                else {
                    mtdRet += "min";
                }
            }
            else {
                mtdRet += "s";
            }
        }
        else {
            mtdRet += "ms";
        }

        return String.format(mtdRet, timeElapsed);
    }

    /**
     * 
     * @param inFileName
     * @param templFileName
     * @param outFileName
     */
    public BatchReplace(ParmTO parms) {
        boolean     foundKey    = false;

        FileMngIn   inFile      = null;

        FileMngOut  outFile;

        _initClass(parms);

        try {
            ParmFMI pFMI    = new ParmFMI();

            pFMI.setFileName(parms.getInFileName());
            pFMI.setMaxBufferSz(parms.get_maxChunkSz());

            inFile = new FileMngIn(pFMI);

            outFile = new FileMngOut(parms.getOutFileName());
        } catch (IOException e) {
            e.printStackTrace();

            getLogger().println("There's a problem with the in or out file...");

            _printCmdLineUsage();

            return;
        }

        FileTemplate inFileTmpl;

        try {
            if (parms.getSeparator() != null) {
                inFileTmpl = new FileTemplate(parms.getTemplFileName(), parms.getSeparator());
            }
            else {
                inFileTmpl = new FileTemplate(parms.getTemplFileName());
            }

        } catch (IOException e1) {
            e1.printStackTrace();

            getLogger().println("There's a problem in the template file...");

            _printCmdLineUsage();

            return;
        }

        byte[]      buffInArr   = null,
                    clonedBuff;

        List<ProcessChunk> workersThreads = new ArrayList<>();

        ProcessChunk    newThd;

        ParmPC  parmPC  = new ParmPC();

        parmPC.set_logger(this.getLogger());
        parmPC.set_wholeWordSearch(parms.is_wholeWordSearch());

        this._defineWrkThdQty();

        try {
            do {
                // Create and start a number of worker threads
                for (int thdNmb = 0; thdNmb < get_threadQty(); ++thdNmb) {
                    // Visit each file chunk
                    buffInArr = inFile.getChunk();

                    if (buffInArr == null) {
                        break;
                    }

                    // We'll use the original buffer for the last thread - save memory!
                    if ( (thdNmb + 1) != get_threadQty() ) {
                        clonedBuff = new byte[inFile.get_lastChunkSz()];

                        System.arraycopy(buffInArr, 0, clonedBuff, 0,
                                            inFile.get_lastChunkSz());
                    }
                    else {
                        clonedBuff = buffInArr;
                    }

                    parmPC.set_chunk(clonedBuff);
                    parmPC.set_szChunk(inFile.get_lastChunkSz());
                    parmPC.set_inFileTmpl(inFileTmpl);

                    newThd =  new ProcessChunk(new ParmPC(parmPC));

                    workersThreads.add(newThd);

                    getLogger().println("Starting a thread (" + newThd + ")");

                    newThd.start();
                }

                // We've to wait the threads end sequentially
                for (ProcessChunk currThd : workersThreads) {
                    currThd.join();

                    getLogger().println("Thread (" + currThd + ") terminated");

                    outFile.putChunk( currThd.get_buffOut(),
                                        currThd.get_buffOutSz() );

                    if (! foundKey) {
                        foundKey = currThd.is_foundKey();
                    }
                }

                // Clear the thread list
                workersThreads.clear();
            } while (buffInArr != null);
        } catch (IOException | InterruptedException e) {
            getLogger().println("There was a problem in the file processing...");

            e.printStackTrace();

            return;
        }

        if (! foundKey) {
            outFile.delete();

            getLogger().println("No token of the <template_file> was found in the <source_file>. " +
                                "Assure that the enconding of these files are \"UTF-8\" or \"ANSI\"");
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        String  timeExtense;

        ParmTO  parms   = new ParmTO();

        long    start;

        int     offSetParm      = 0,
                extraParmQty    = 0;

        parms.set_logger(new Logger());

        if (args.length > 1) {
            if (args[offSetParm].compareToIgnoreCase("-w") == 0) {
                parms.set_wholeWordSearch(true);

                parms.get_logger().println("\nUsing the whole word mode");

                offSetParm++;

                extraParmQty++;
            }

            parms.setInFileName(args[offSetParm++]);
            parms.setTemplFileName(args[offSetParm++]);

            if (args.length > 2 + extraParmQty) {
                parms.setOutFileName(args[offSetParm++]);

                if (args.length > 3 + extraParmQty) {
                    parms.setSeparator( args[offSetParm++].getBytes() );
                }
            }
            else {
                parms.setOutFileName(parms.getInFileName().concat(".new"));
            }

            start = System.currentTimeMillis();

            new BatchReplace(parms);

            timeExtense = BatchReplace._formatProcessTime( start, System.currentTimeMillis() );

            parms.get_logger().println("\nProcessing took " + timeExtense);
        }
        else {
            BatchReplace._printCmdLineUsage(parms.get_logger());
        }

        System.exit(0);
    }
}

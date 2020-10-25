package filemng.out;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

//https://www.journaldev.com/878/java-write-to-file

public class FileMngOut {
    private String _nameFile;

    private OutputStream _os = null;

    private File _file;

    public FileMngOut(String fName) throws IOException {
        this._initClass(fName);
    }

    private void _initClass(String fName) throws IOException {
        this.set_nameFile(fName);

        set_file(new File(get_nameFile()));

        set_os(new FileOutputStream(get_file()));
    }

    protected OutputStream get_os() {
        return _os;
    }

    protected File get_file() {
        return _file;
    }

    protected void set_file(File _file) {
        this._file = _file;
    }

    protected void set_os(OutputStream _os) {
        this._os = _os;
    }

    public String get_nameFile() {
        return _nameFile;
    }

    protected void set_nameFile(String _nameFile) {
        this._nameFile = _nameFile;
    }

    public void putChunk(ByteBuffer buffOut, int buffSz) throws IOException {
        byte[] toWrite = buffOut.array();

        get_os().write(toWrite, 0, buffSz);
    }

    public void delete() {
        if (get_file() != null) {
            try {
                get_os().close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            get_file().delete();
        }
    }
}

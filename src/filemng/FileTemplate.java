package filemng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

// https://www.geeksforgeeks.org/different-ways-reading-text-file-java/

public class FileTemplate {
    private String  _nameFile;

    BufferedReader br;

    File file;

    HashMap<byte[], byte[]> _intList    = new HashMap<>();

    protected boolean _containsKey(byte key2Check[]) {
        boolean mtdRet  = false;

        for ( byte[] currKey : _intList.keySet() ) {
            if ( java.util.Arrays.equals(key2Check, currKey) ) {
                mtdRet = true;

                break;
            }
        }
        return mtdRet;
    }

    private void _initClass(String fName, byte separator[]) throws IOException {
        this.set_nameFile(fName);

        file = new File(fName);

        br = new BufferedReader(new FileReader(file));

        String  st,
                currTuple[],
                sepStr      = new String(separator, "UTF-8");

        while ((st = br.readLine()) != null) {
          currTuple = st.split(sepStr);

          if ( (currTuple.length > 1) &&
              ( ! this._containsKey( currTuple[0].getBytes() ) ) ) {
              _intList.put(currTuple[0].getBytes(), currTuple[1].getBytes());
          }
        }
    }

    public FileTemplate(String string, byte separator[]) throws IOException {
        _initClass(string, separator);
    }

    public FileTemplate(String string) throws IOException {
        _initClass(string, "\t".getBytes());
    }

    public String get_nameFile() {
        return _nameFile;
    }

    protected void set_nameFile(String _nameFile) {
        this._nameFile = _nameFile;
    }

    public HashMap<byte[], byte[]> getMap() {
        return _intList;
    }
}

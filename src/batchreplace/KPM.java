package batchreplace;

// https://stackoverflow.com/questions/21341027/find-indexof-a-byte-array-within-another-byte-array

public class KPM {
    /**
     * Search the data byte array for the first occurrence of the byte array pattern within given boundaries.
     * @param data
     * @param start First index in data
     * @param stop Last index in data so that stop-start = length
     * @param pattern What is being searched. '*' can be used as wildcard for "ANY character"
     * @return
     */
    public static int indexOf( byte[] data, int start, int stop, byte[] pattern) {
        if( data == null || pattern == null) return -1;

        int failure[] = computeFailure(pattern),
            j = 0;

        for( int i = start; i < stop; i++) {
            while (j > 0 && ( pattern[j] != '*' && pattern[j] != data[i])) {
                j = failure[j - 1];
            }
            if (pattern[j] == '*' || pattern[j] == data[i]) {
                j++;
            }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

    /**
     * Computes the failure function using a boot-strapping process,
     * where the pattern is matched against itself.
     */
    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j>0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }

        return failure;
    }

    /**
     * The same of the {@link #indexOf(byte[], int, int, byte[])} but finds the last occurrence
     * of a token.
     * @param buffer    Where to do the search.
     * @param endMarker What to find (named 'token').
     * @return  The index where the token was found.
     */
    public static int lastIndexOf(byte[] buffer, byte[] endMarker) {
        int end             = buffer.length,
            // Starts at the final 1/4 of the buffer
            start           = end - (end / 4),
            lastPosFound    = -1,
            posFound        = -1;

        if (end > endMarker.length) {
            do {
                posFound = KPM.indexOf(buffer, start, end, endMarker);

                if (posFound != -1) {
                    lastPosFound = posFound;

                    start = posFound + endMarker.length;
                }
                else if ( (lastPosFound == -1) && (start != 0) ) {
                    // Weren't found, we'll do the search by the previous 1/4 index
                    start -= (end / 4);

                    if (start < 0) {
                        start = 0;
                    }
                }
                else {
                    break;
                }
            } while (start < buffer.length);
        }

        return lastPosFound;
    }
}

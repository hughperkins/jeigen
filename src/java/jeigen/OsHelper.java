package jeigen;

final class OsHelper {
    public static final int jvmBits() {
        if( System.getProperty("os.arch").toLowerCase().equals("x86") ) {
             return 32;
        }
        if( System.getProperty("os.arch").toLowerCase().equals("i386") ) {
             return 32;
        }
        return 64;
    }
    public static final boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
    }
    public static final boolean isMac() {
        return System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0;
    }
}


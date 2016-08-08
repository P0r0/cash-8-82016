package co.tslc.cashe.android;

/**
 * 15/11/15.
 */
import java.io.File;

abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}
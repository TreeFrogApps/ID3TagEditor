package com.treefrogapps.tag_editor;

        import com.sun.istack.internal.NotNull;
        import org.jaudiotagger.audio.exceptions.CannotReadException;
        import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
        import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
        import org.jaudiotagger.audio.mp3.MP3File;
        import org.jaudiotagger.tag.FieldDataInvalidException;
        import org.jaudiotagger.tag.FieldKey;
        import org.jaudiotagger.tag.TagException;
        import org.jaudiotagger.tag.id3.ID3v11Tag;
        import org.jaudiotagger.tag.id3.ID3v23Tag;
        import org.jaudiotagger.tag.images.AndroidArtwork;
        import org.jaudiotagger.tag.images.Artwork;
        import org.jaudiotagger.tag.reference.GenreTypes;

        import java.io.File;
        import java.io.IOException;
        import java.util.concurrent.ConcurrentHashMap;
        import java.util.concurrent.LinkedBlockingQueue;
        import java.util.concurrent.ThreadPoolExecutor;
        import java.util.concurrent.TimeUnit;

/**
 * Abstract Super Class for tag editor
 * <p>
 * Documentation for library {@link <a href="http://www.jthink.net/jaudiotagger/maven/apidocs/index.html"}
 */
abstract class AbstractID3TagEditor {

    private ThreadPoolExecutor mThreadPoolExecutor;
    private static final int CORE_POOL_SIZE = 6;
    private static final int MAX_POOL_SIZE = 12;
    private static final long TIME_LIMIT = 200L;
    private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

    private ConcurrentHashMap<String, MP3File> mMP3FileMap;
    private byte[] mImageByteArray;
    private volatile boolean mIsFileWriting = false;

    AbstractID3TagEditor(@NotNull File file) throws ReadOnlyFileException, CannotReadException,
            TagException, InvalidAudioFrameException, IOException {

        this.mMP3FileMap = new ConcurrentHashMap<>();

        this.mThreadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_POOL_SIZE, TIME_LIMIT, TIME_UNIT, new LinkedBlockingQueue<>());

        addMp3File(file);
    }

    ThreadPoolExecutor getThreadPoolExecutor() {
        return this.mThreadPoolExecutor;
    }

    public void addMp3File(File file) throws IOException, TagException, ReadOnlyFileException,
            CannotReadException, InvalidAudioFrameException {

        MP3File mp3File = new MP3File(file);

        if (!mp3File.hasID3v1Tag()) {
            mp3File.setID3v1Tag(new ID3v11Tag());
        }

        if (!mp3File.hasID3v2Tag()) {
            mp3File.setID3v2Tag(new ID3v23Tag());
        }

        mMP3FileMap.put(mp3File.getFile().getAbsolutePath(), mp3File);
    }


    ConcurrentHashMap<String, MP3File> getMP3FileMap() {
        return this.mMP3FileMap;
    }

    byte[] getImageByteArray(){
        return this.mImageByteArray;
    }

    boolean isFileWriting() {
        return this.mIsFileWriting;
    }

    void setFileWriting(boolean isFileWriting) {
        this.mIsFileWriting = isFileWriting;
    }


    /**
     * Methods to be implemented by concrete class
     */

    public abstract void setArtist(@NotNull String artist);

    public abstract void setAlbum(@NotNull String album);

    public abstract void setYear(@NotNull String year);

    public abstract void setSongTitle(@NotNull String title);

    public abstract void setSongLyrics(@NotNull String lyrics);

    public abstract void setSongComment(@NotNull String comment);

    public abstract void setTrackNumber(int track, int totalTracks);


    /**
     * Methods Implemented by abstract class
     */

    public void setGenre(@NotNull String genre) {

        if (!mIsFileWriting) {

            if (GenreTypes.getInstanceOf().getIdForName(genre) != null) {

                for (ConcurrentHashMap.Entry<String, MP3File> entry : mMP3FileMap.entrySet()) {

                    MP3File mp3File = entry.getValue();

                    mp3File.getID3v1Tag().setGenre(genre);

                    try {
                        mp3File.getID3v2Tag().deleteField(FieldKey.GENRE);
                        mp3File.getID3v2Tag().addField(FieldKey.GENRE, genre);
                    } catch (FieldDataInvalidException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                throw new IllegalArgumentException("Genre does not exist");
            }
        }
    }

    public void setAlbumArt(File imageFile) {

        if (!mIsFileWriting) {

            mImageByteArray = ImageUtils.getScaledImage(imageFile);


            if (mImageByteArray != null) {

                String mimeType = "image/" + ImageUtils.getImageType(imageFile);
                byte pictureType = 3; // Cover Image
                String imageDescription = "Album Cover";

                Artwork artwork = new AndroidArtwork();
                artwork.setMimeType(mimeType);
                artwork.setPictureType(pictureType);
                artwork.setDescription(imageDescription);
                artwork.setBinaryData(mImageByteArray);

                for (ConcurrentHashMap.Entry<String, MP3File> entry : mMP3FileMap.entrySet()) {

                    MP3File mp3File = entry.getValue();


                    mp3File.getID3v2Tag().deleteArtworkField();
                    try {
                        mp3File.getID3v2Tag().setField(artwork);
                    } catch (FieldDataInvalidException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

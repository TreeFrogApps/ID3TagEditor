package com.treefrogapps.tag_editor;


import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v11Tag;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;


/**
 * Wrapper Class for Library : jaudiotagger-2.2.6-SNAPSHOT.jar
 * <p>
 * Interacts with Library with convenience methods to update ID3 tag information
 * <p>
 * INFO: Text Encoding
 * 00 – ISO-8859-1 (ASCII).
 * 01 – UCS-2 (UTF-16 encoded Unicode with BOM), in ID3v2.2 and ID3v2.3.
 * 02 – UTF-16BE encoded Unicode without BOM, in ID3v2.4.
 * 03 – UTF-8 encoded Unicode, in ID3v2.4.
 * <p>
 * Documentation for library :
 * {@link <a href="http://www.jthink.net/jaudiotagger/maven/apidocs/index.html"}
 */

public class ID3TagEditor extends AbstractID3TagEditor {

    private static final String TAG = ID3TagEditor.class.getSimpleName();

    private CountDownLatch mCountDownLatch;

    public ID3TagEditor(@NotNull File file) throws TagException, ReadOnlyFileException,
            CannotReadException, InvalidAudioFrameException, IOException {

        super(file);
    }


    public void setArtist(@NotNull String artist) {

        if (!isFileWriting()) {

            for (ConcurrentHashMap.Entry<String, MP3File> entry : getMP3FileMap().entrySet()) {

                MP3File mp3File = entry.getValue();

                mp3File.getID3v1Tag().setArtist(artist);

                mp3File.getID3v2Tag().deleteField(FieldKey.ARTIST);
                mp3File.getID3v2Tag().deleteField(FieldKey.ALBUM_ARTIST);
                try {
                    mp3File.getID3v2Tag().addField(FieldKey.ARTIST, artist);
                    mp3File.getID3v2Tag().addField(FieldKey.ALBUM_ARTIST, artist);
                } catch (FieldDataInvalidException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setAlbum(@NotNull String album) {

        if (!isFileWriting()) {

            for (ConcurrentHashMap.Entry<String, MP3File> entry : getMP3FileMap().entrySet()) {

                MP3File mp3File = entry.getValue();

                mp3File.getID3v1Tag().setAlbum(album);


                mp3File.getID3v2Tag().deleteField(FieldKey.ALBUM);
                try {
                    mp3File.getID3v2Tag().addField(FieldKey.ALBUM, album);
                } catch (FieldDataInvalidException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setSongTitle(@NotNull String title) {

        if (!isFileWriting()) {

            for (ConcurrentHashMap.Entry<String, MP3File> entry : getMP3FileMap().entrySet()) {

                MP3File mp3File = entry.getValue();

                mp3File.getID3v1Tag().setTitle(title);

                mp3File.getID3v2Tag().deleteField(FieldKey.TITLE);
                try {
                    mp3File.getID3v2Tag().addField(FieldKey.TITLE, title);
                } catch (FieldDataInvalidException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setYear(@NotNull String year) {

        if (!isFileWriting()) {

            for (ConcurrentHashMap.Entry<String, MP3File> entry : getMP3FileMap().entrySet()) {

                MP3File mp3File = entry.getValue();

                mp3File.getID3v1Tag().setYear(year);

                mp3File.getID3v2Tag().deleteField(FieldKey.YEAR);
                mp3File.getID3v2Tag().deleteField(FieldKey.ORIGINAL_YEAR);
                try {
                    mp3File.getID3v2Tag().addField(FieldKey.YEAR, year);
                    mp3File.getID3v2Tag().addField(FieldKey.ORIGINAL_YEAR, year);
                } catch (FieldDataInvalidException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setTrackNumber(int trackNumber, int totalTracks) {

        if (!isFileWriting()) {

            for (ConcurrentHashMap.Entry<String, MP3File> entry : getMP3FileMap().entrySet()) {

                MP3File mp3File = entry.getValue();

                if (mp3File.getID3v1Tag() instanceof ID3v11Tag) {
                    ((ID3v11Tag) mp3File.getID3v1Tag()).setTrack(String.valueOf(trackNumber));
                }

                mp3File.getID3v2Tag().deleteField(FieldKey.TRACK);

                if (totalTracks > 0) {
                    mp3File.getID3v2Tag().deleteField(FieldKey.TRACK_TOTAL);
                }

                try {
                    mp3File.getID3v2Tag().addField(FieldKey.TRACK, String.valueOf(trackNumber));

                    if (totalTracks > 0) {
                        mp3File.getID3v2Tag().addField(FieldKey.TRACK_TOTAL, String.valueOf(totalTracks));
                    }
                } catch (FieldDataInvalidException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setSongLyrics(@NotNull String lyrics) {

        if (!isFileWriting()) {

            for (ConcurrentHashMap.Entry<String, MP3File> entry : getMP3FileMap().entrySet()) {

                MP3File mp3File = entry.getValue();

                mp3File.getID3v2Tag().deleteField(FieldKey.LYRICS);

                try {
                    mp3File.getID3v2Tag().addField(FieldKey.LYRICS, lyrics);
                } catch (FieldDataInvalidException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setSongComment(@NotNull String comment) {

        if (!isFileWriting()) {

            for (ConcurrentHashMap.Entry<String, MP3File> entry : getMP3FileMap().entrySet()) {

                MP3File mp3File = entry.getValue();

                mp3File.getID3v1Tag().setComment(comment);

                mp3File.getID3v2Tag().deleteField(FieldKey.COMMENT);

                try {
                    mp3File.getID3v2Tag().addField(FieldKey.COMMENT, comment);
                } catch (FieldDataInvalidException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Update Tags to MP3Files Asynchronously on an Executor - fixed thread pool size
     * <p>
     * Uses simple countdown latch to let us know when all threads have finished accessing files and
     * calls back to the onCompleteListener();
     */
    public void applyID3TagChanges(UpdateCompleteListener completeListener) {

        if (!isFileWriting() && getMP3FileMap().size() > 0) {

            int mapSize = getMP3FileMap().size();
            mCountDownLatch = new CountDownLatch(mapSize);

            new Thread(() -> {

                try {
                    setFileWriting(true);
                    mCountDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    setFileWriting(false);
                    if (completeListener != null) {

                        if (!getThreadPoolExecutor().isShutdown()) {
                            getThreadPoolExecutor().shutdownNow();
                        }

                        //clear all mapping to files
                        getMP3FileMap().clear();

                        completeListener.onComplete(getImageByteArray());                    }
                }
            }).start();

            for (Map.Entry<String, MP3File> entry : getMP3FileMap().entrySet()) {

                getThreadPoolExecutor().execute(new TagUpdateRunnable(entry.getValue(), mCountDownLatch, mapSize));
            }

        } else {
            completeListener.onComplete(null);
        }
    }


    /**
     * Completion listener used with {@link #applyID3TagChanges(UpdateCompleteListener)} method
     */
    public interface UpdateCompleteListener {
        void onComplete(@Nullable byte[] imageByteArray);
    }


    /**
     * Static Runnable class (doesn't hold implicit reference to outer class)
     */
    private static class TagUpdateRunnable implements Runnable {

        private WeakReference<MP3File> mMp3File;
        private WeakReference<CountDownLatch> mCountDownLatch;
        private int mTotalFileCount;

        public TagUpdateRunnable(MP3File mp3File, CountDownLatch countDownLatch, int totalFileCount) {

            // only hold weak references to objects so can be garbage collected
            // otherwise if set to null, or unreferenced in calling class this class will
            // still hold a strong reference prevent GC
            this.mMp3File = new WeakReference<>(mp3File);
            this.mCountDownLatch = new WeakReference<>(countDownLatch);
            this.mTotalFileCount = totalFileCount;
        }


        @SuppressWarnings("ConstantConditions")
        @Override
        public void run() {

            if (mMp3File.get().getFile().canWrite()) {
                updateFileID3Tag(mMp3File.get());
            } else {
                System.out.println((TAG + "  Write access denied : " + mMp3File.get().getFile().getAbsolutePath()));
            }

            this.mCountDownLatch.get().countDown();

            System.out.println(TAG + "   Thread : " + Thread.currentThread().getName() + " finished processing image : "
                    + (mCountDownLatch.get().getCount() + 1) + " of : " + mTotalFileCount);
        }

        private void updateFileID3Tag(MP3File mp3File) {

            try {

                mp3File.save();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (TagException e) {
                e.printStackTrace();
            }
        }
    }
}

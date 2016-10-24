package com.treefrogapps;

import com.treefrogapps.tag_editor.ID3TagEditor;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.reference.GenreTypes;

import java.io.*;

public class Main {

    public static void main(String[] args) {
	// write your code here

        long start = System.currentTimeMillis();

        File mp3FileDir = new File("files/mp3");

        if (mp3FileDir.exists() && mp3FileDir.isDirectory()) {

            try {
                ID3TagEditor tagEditor = new ID3TagEditor(mp3FileDir.listFiles()[1]);

                for (int i = 2; i < mp3FileDir.listFiles().length; i++) {

                    File mp3File2 = mp3FileDir.listFiles()[i];

                    if (mp3File2.exists() && mp3File2.isFile() && mp3File2.getAbsolutePath().endsWith(".mp3")) {
                        try {
                            tagEditor.addMp3File(mp3FileDir.listFiles()[i]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (TagException e) {
                            e.printStackTrace();
                        }
                    }
                }

                tagEditor.setArtist("Artist - Tester");
                tagEditor.setGenre(GenreTypes.getInstanceOf().getValueForId(22)); // Death Metal
                tagEditor.setSongTitle("Title - Tester");
                tagEditor.setYear(String.valueOf(2016));
                tagEditor.setTrackNumber(1, 10);
                tagEditor.setAlbum("Album - Tester");
                tagEditor.setSongComment("this is a test comment");
                tagEditor.setAlbumArt(new File("files/album_art/test.png"));

                FileInputStream fis = new FileInputStream(new File("files/lyrics/lyrics_tester.txt"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                StringBuilder sb = new StringBuilder();

                String line;
                while((line = reader.readLine()) != null){
                    sb.append(line).append('\n');
                }

                fis.close();
                reader.close();
                tagEditor.setSongLyrics(sb.toString());

                System.out.println("Applying Changes...\n");

                tagEditor.applyID3TagChanges((imageByteArray) -> {

                    System.out.println("OnComplete Called\n");
                    System.out.println("Time Taken : " + ((System.currentTimeMillis() - start) / 1000) + " seconds");

                });


            } catch (TagException e) {
                e.printStackTrace();
            } catch (ReadOnlyFileException e) {
                e.printStackTrace();
            } catch (CannotReadException e) {
                e.printStackTrace();
            } catch (InvalidAudioFrameException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

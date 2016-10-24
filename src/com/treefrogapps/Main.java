package com.treefrogapps;

import com.treefrogapps.tag_editor.ID3TagEditor;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.reference.GenreTypes;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
	// write your code here

        long start = System.currentTimeMillis();

        File mp3FileDir = new File("files/mp3");

        if (mp3FileDir.exists() && mp3FileDir.isDirectory()) {

            ID3TagEditor tagEditor = null;
            try {
                tagEditor = new ID3TagEditor(mp3FileDir.listFiles()[1]);

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

                tagEditor.setArtist("Carlos Santana - Name changed");
                tagEditor.setGenre(GenreTypes.getInstanceOf().getValueForId(22)); // Death Metal
                tagEditor.setSongTitle("Nothing At All - Name Changed");
                tagEditor.setYear(String.valueOf(2016));
                tagEditor.setTrackNumber(1, 10);
                tagEditor.setAlbum("Shaman - Name Changed");
                tagEditor.setSongComment("this is a test comment");
                tagEditor.setAlbumArt(new File("files/f.jpg"));

                tagEditor.setSongLyrics("I am a victim of my time\n" +
                        "A produce of my age\n" +
                        "There's no choosing my direction\n" +
                        "I was a holy man but now\n" +
                        "With all my trials behind me\n" +
                        "I am weak in my conviction\n" +
                        "\n" +
                        "And so I walk to try to get away\n" +
                        "Knowing that someday I will finally have to face\n" +
                        "The fear that will come from knowing that\n" +
                        "The one thing I had left was you\n" +
                        "And now you're gone\n" +
                        "\n" +
                        "You were a victim of my crimes\n" +
                        "A product of my rage\n" +
                        "You were a beautiful distraction\n" +
                        "I kept you locked away outside\n" +
                        "Let misery provide\n" +
                        "And now I am ashamed\n" +
                        "\n" +
                        "And so I walk to try to find a space\n" +
                        "Where I can be alone to live with my mistakes\n" +
                        "And the fear that will come\n" +
                        "From knowing that the one thing\n" +
                        "I had left was you\n" +
                        "And now you're gone\n" +
                        "\n" +
                        "[Chorus:]\n" +
                        "Is there nothing at all\n" +
                        "That i can do to turn your hert\n" +
                        "Is there nothing to lean on\n" +
                        "That could help erase the scars\n" +
                        "Te quiero - me quiero\n" +
                        "And I could use a little strength before I fall\n" +
                        "Is there nothing at all\n" +
                        "\n" +
                        "I am victim of my time\n" +
                        "A product of the age\n" +
                        "You alone are my obsession\n" +
                        "You were the one I left behind\n" +
                        "You've been heavy on my mind\n" +
                        "It's been a lonely road I've traveled\n" +
                        "\n" +
                        "And so I walk to try to get away\n" +
                        "Knowing that someday I will finally have to face\n" +
                        "The fear that will come from knowing that\n" +
                        "The one thing I had left was you\n" +
                        "And now you're gone\n" +
                        "\n" +
                        "[Chorus:]\n" +
                        "Is there nothing at all\n" +
                        "That I can do to turn your heart\n" +
                        "Is there nothing to lean on\n" +
                        "Tha cold help erase the scars\n" +
                        "Te quiero - me quiero\n" +
                        "And I could use a little strength before I fall\n" +
                        "Is there nothing at all");

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

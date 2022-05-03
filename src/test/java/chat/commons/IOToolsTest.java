package chat.commons;

import chat.commons.IOTools;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IOToolsTest {
    static String[] fileNames;
    static String[] expectedFileNamesAfterRenaming;
    static int index = 0;
    String returnedFileName;
    String expectedFileName;
    @BeforeAll
    static void setUp() {
        fileNames = new String[] {"AAla makota(4).txt", "AAla makota(23)",
                "AAla makota(23).aaa.txt", "AAla makota.txt", "la makota().tx", "AAla makota(23).jpg"};

        expectedFileNamesAfterRenaming = new String[] {"AAla makota(5).txt", "AAla makota(24)",
                "AAla makota(23).aaa(1).txt", "AAla makota(1).txt", "la makota()(1).tx", "AAla makota(24).jpg"};
    }

    @BeforeEach
    void fileRenamePreparation() {
        returnedFileName = IOTools.renameFileToAvoidDuplication(fileNames[index]);
        expectedFileName = expectedFileNamesAfterRenaming[index];
    }

    @AfterEach
    void incrementIndex() {
        index++;
    }

    @RepeatedTest(value = 6, name = "File renaming test {currentRepetition}/{totalRepetitions}")
    void isFileNameCorrectlyChanged() {
        assertEquals(expectedFileName, returnedFileName);
    }
}
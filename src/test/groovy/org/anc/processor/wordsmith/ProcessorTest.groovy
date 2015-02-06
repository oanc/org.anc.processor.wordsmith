package org.anc.processor.wordsmith

import org.anc.processor.wordsmith.i18n.Messages
import org.junit.*

import javax.ws.rs.core.Response

import static org.junit.Assert.*

/**
 * Created by danmccormack on 12/13/14.
 */
class ProcessorTest {

    private static final Messages MESSAGES = new Messages()

    WordsmithProcessor processor

    @Before
    void setup(){
        processor = new WordsmithProcessor()
    }

    @After
    void cleanup(){
        processor = null
    }

    /**
     * Cases to Test: Passing (1, all, a few)
     *                Failing (none, all wrong, one wrong in a list of accepted)
     */
    @Test
    void testValidAnnotations()
    {
        //PASSING ********
        //ONE
        def pass1 = ["f.penn"]
        assertTrue(processor.validAnnotations(pass1, processor.ACCEPTABLE))

        //FEW
        def pass2 = ["f.penn", "f.fn", "f.ptb"]
        assertTrue(processor.validAnnotations(pass2, processor.ACCEPTABLE))

        //ALL
        def pass3 = ["f.penn", "f.fn", "f.fntok", "f.ptb", "f.ptbtok"]
        assertTrue(processor.validAnnotations(pass3, processor.ACCEPTABLE))

        //FAILING ********
        //NONE
        def fail1 = [] as ArrayList<String>
        assertFalse(processor.validAnnotations(fail1, processor.ACCEPTABLE))

        //ALL
        def fail2 = ["f.c5", "f.biber", "f.ne", "f.mpqa"]
        assertFalse(processor.validAnnotations(fail2, processor.ACCEPTABLE))

        //ONE WRONG IN LIST OF RIGHT
        def fail3 = ["f.penn", "f.fn", "f.biber", "f.fntok"]
        assertFalse(processor.validAnnotations(fail3, processor.ACCEPTABLE))
    }

    @Test
    /**
     *  Cases to test: empty string, one word, two words, n-words
     */
    void testParseAnnotations()
    {
        //EMPTY STRING
        assertTrue("Empty string should return all acceptable annotations", processor.parseAnnotations("") == processor.ACCEPTABLE.toList() )

        //ONE WORD
        List expected = ["f.penn"]
        List actual = processor.parseAnnotations("penn")
        assertTrue "Actual is " + actual, actual == expected

        //TWO WORDS
        expected = ["f.penn", "f.cb"]
        assertTrue(processor.parseAnnotations("penn,cb") == expected)

        //N WORDS
        // Use Groovy range operator to populate the collection, then add the 'f.' prefix.
        expected = (1..9).collect { "f." + it }
        actual = processor.parseAnnotations("1,2,3,4,5,6,7,8,9")
        assertTrue("Expected is " + expected + " Actual is " + actual, actual == expected)
    }

    @Test
    void testProcess() {
        Response response = processor.process("penn", "MASC3-0202")
        assertTrue response.entity, response.status == 200
    }

    @Test
    void testInvalidDocId() {
        Response response = processor.process("penn", "Invalid ID")
        assertTrue response.entity, response.status == 500
        assertTrue "Wrong error message returned", response.entity == MESSAGES.INVALID_ID
    }

    @Test
    void testInvalidAnnotationType() {
        Response response = processor.process("invalid,annotations", "MASC3-0202")
        assertTrue response.entity, response.status == 500
        assertTrue "Wrong error message returned.", response.entity == MESSAGES.INVALID_TYPE
    }
}

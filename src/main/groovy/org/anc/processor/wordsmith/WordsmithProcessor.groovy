package org.anc.processor.wordsmith

import org.anc.processor.Abstract.AbstractProcessor
import javax.ws.rs.Path

/**
 * Created by danmccormack on 12/10/14.
 */
@Path("/wordsmith")
class WordsmithProcessor extends AbstractProcessor {

    public WordsmithProcessor() {
        super(["f.fn", "f.fntok",
               "f.ptb", "f.ptbtok"] as List<String>)
        processor = new org.anc.tool.wordsmith.WordSmithProcessor();
    }
}

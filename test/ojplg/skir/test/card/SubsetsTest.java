package ojplg.skir.test.card;

import ojplg.skir.card.Subsets;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubsetsTest {

    @Test
    public void createSublistsFromLengthFive(){
        List<String> strings = new ArrayList<String>();
        strings.add("a");
        strings.add("b");
        strings.add("c");
        strings.add("d");
        strings.add("e");

        List<List<String>> subLists = Subsets.allSubsets(3, strings);
        outLists(subLists);
        assertEquals(10, subLists.size());
    }

    @Test
    public void createSublistsFromLengthFour(){
        List<String> strings = new ArrayList<String>();
        strings.add("a");
        strings.add("b");
        strings.add("c");
        strings.add("d");

        List<List<String>> subLists = Subsets.allSubsets(3, strings);
        outLists(subLists);
        assertEquals(4, subLists.size());
    }

    @Test
    public void splitThreeItemsIntoTwos(){
        List<String> strings = new ArrayList<String>();
        strings.add("a");
        strings.add("b");
        strings.add("c");

        List<List<String>> subLists = Subsets.allSubsets(2, strings);
        outLists(subLists);

        assertEquals(3, subLists.size());
    }

    @Test
    public void splitThreeItems(){
        List<String> strings = new ArrayList<String>();
        strings.add("a");
        strings.add("b");
        strings.add("c");

        List<List<String>> subLists = Subsets.allSubsets(1, strings);
        outLists(subLists);

        assertEquals(3, subLists.size());
    }

    @Test
    public void splitTwoItems(){
        List<String> strings = new ArrayList<String>();
        strings.add("a");
        strings.add("b");

        List<List<String>> subLists = Subsets.allSubsets(1, strings);
        outLists(subLists);

        assertEquals(2, subLists.size());
    }

    @Test
    public void testSimplestCase(){
        List<String> strings = new ArrayList<String>();
        strings.add("a");

        List<List<String>> subs = Subsets.allSubsets(1, strings);

        outLists(subs);

        assertEquals(1, subs.size());
    }

    private void outLists(List<List<String>> subs){
        for(List<String> sub : subs){
            Collections.sort(sub);
            StringBuffer buf = new StringBuffer();
            for( String item : sub ){
                buf.append(item);
                buf.append(", ");
            }
            //System.out.println(buf.toString());
        }
    }
}
